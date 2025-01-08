package com.wjftu.tool

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


fun main(args: Array<String>) {
    runApplication<ToolApplication>(*args)
}

@SpringBootApplication
class ToolApplication