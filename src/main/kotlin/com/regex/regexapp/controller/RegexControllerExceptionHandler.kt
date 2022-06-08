package com.regex.regexapp.controller

import com.regex.regexapp.model.ImproperRegexException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException

@RestControllerAdvice
class RegexControllerExceptionHandler {

    @ExceptionHandler(WebExchangeBindException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun improperRegex(): ImproperRegexException {
        return ImproperRegexException("Please enter a valid regex")
    }
}