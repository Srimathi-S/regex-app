package com.regex.regexapp.controller

import com.regex.regexapp.model.Regex
import com.regex.regexapp.service.RegexService
import com.regex.regexapp.utility.RegexTypeFinder
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux


@RestController
class RegexController(private val regexService: RegexService, private val regexTypeFinder: RegexTypeFinder) {

    @PostMapping("/and")
    fun conjunction(@RequestBody(required = true) addRegexRequest: AddRegexRequest): Mono<Regex> {
        return regexService.conjunction(addRegexRequest.regex1, addRegexRequest.regex2)
    }

    @PostMapping("/describe")
    fun describe(@RequestBody(required = true) regex: Regex): Flux<MutableList<String>> {
        return regexTypeFinder.describe(regex).map { it }.toFlux()
    }

}

class AddRegexRequest(val regex1: Regex, val regex2: Regex)
