package com.regex.regexapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class RegexAppApplication

fun main(args: Array<String>) {
    runApplication<RegexAppApplication>(*args)
}
