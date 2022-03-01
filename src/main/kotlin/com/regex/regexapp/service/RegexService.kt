package com.regex.regexapp.service

import com.regex.regexapp.model.Regex
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class RegexService {

    fun conjunction(regex1: Regex, regex2: Regex): Mono<Regex> {
        return regex1.and(regex2).toMono()
    }

}
