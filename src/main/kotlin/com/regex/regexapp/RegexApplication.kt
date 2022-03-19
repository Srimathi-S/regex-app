package com.regex.regexapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class RegexApplication

fun main(args: Array<String>) {
    runApplication<RegexApplication>(*args)
}
