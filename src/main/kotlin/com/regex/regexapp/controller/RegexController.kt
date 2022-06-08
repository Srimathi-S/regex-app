package com.regex.regexapp.controller

import com.regex.regexapp.model.Regex
import com.regex.regexapp.service.RegexService
import com.regex.regexapp.utility.RegexTypeFinder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import javax.validation.Valid


@RestController
class RegexController(private val regexService: RegexService, private val regexTypeFinder: RegexTypeFinder) {

    @PostMapping("/and")
    fun conjunction(@RequestBody(required = true) addRegexRequest: AddRegexRequest): Mono<Regex> {
        return regexService.conjunction(addRegexRequest.regex1, addRegexRequest.regex2)
    }

    @PostMapping("/describe")
    fun describe(@Valid @RequestBody regex: Regex): Flux<MutableList<String>> {
        return regexTypeFinder.describe(regex).map { it }.toFlux()
    }

}

class AddRegexRequest(val regex1: Regex, val regex2: Regex)
