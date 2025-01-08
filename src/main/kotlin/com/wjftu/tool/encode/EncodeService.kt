package com.wjftu.tool.encode

import org.springframework.stereotype.Service
import java.lang.RuntimeException
import java.util.*

@Service
class EncodeService {

    fun base64Encode(plain: String): String {
        val base64 = Base64.getEncoder().encodeToString(plain.toByteArray(Charsets.UTF_8))
        return base64
    }

    fun base64Decode(base64: String) : String {
        val decode = Base64.getDecoder().decode(base64)
        return String(decode)
    }
}