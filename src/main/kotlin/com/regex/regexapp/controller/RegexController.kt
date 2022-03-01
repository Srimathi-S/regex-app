package com.regex.regexapp.controller

import com.regex.regexapp.model.Regex
import com.regex.regexapp.service.RegexService
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import reactor.core.publisher.Mono

@RestController
class RegexController(private val regexService: RegexService) {

    @PostMapping("/and")
    fun conjunction(@RequestBody(required = true) addRegexRequest: AddRegexRequest): Mono<Regex> {
        return regexService.conjunction(addRegexRequest.regex1, addRegexRequest.regex2)
    }

}

class AddRegexRequest(val regex1: Regex, val regex2: Regex)
