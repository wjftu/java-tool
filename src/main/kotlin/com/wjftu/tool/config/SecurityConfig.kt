package com.wjftu.tool.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig(
    @Value("\${userName:user}")
    val userName: String,
    @Value("\${password:123}")
    val password: String
) {


    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/", "/login", "/css/**", "/js/**").permitAll()
                    .anyRequest().authenticated()
            }
            .formLogin { formLogin ->
                formLogin
                    .loginPage("/login")
                    .permitAll()
            }
            .logout { logout ->
                logout
                    .permitAll()
            }
        return http.build()
    }

    @Bean
    fun userDetailsService(passwordEncoder: PasswordEncoder) : UserDetailsService {
        val manager = InMemoryUserDetailsManager()
        val user = User.withUsername(userName)
            .password(passwordEncoder.encode(password))
            .roles("user")
            .build()
        manager.createUser(user)
        return manager
    }

    @Bean
    fun passwordEncoder() : PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}


