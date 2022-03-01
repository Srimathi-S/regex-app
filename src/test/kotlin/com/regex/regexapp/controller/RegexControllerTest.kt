package com.regex.regexapp.controller

import com.regex.regexapp.model.Regex
import com.regex.regexapp.service.RegexService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.stereotype.Controller
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test

class RegexControllerTest {

    private val regexService = mockk<RegexService>()
    private val regexController = RegexController(regexService)

    @Test
    fun `should call regex service with conjunction method`() {
        val regex1 = Regex("regex")
        every { regexService.conjunction(regex1, regex1) } returns Regex("return value").toMono()

        val addRegexRequest = AddRegexRequest(regex1, regex1)
        val conjunction = regexController.conjunction(addRegexRequest)

        conjunction.test()
            .expectNext(Regex("return value"))
            .verifyComplete()
        verify { regexService.conjunction(regex1, regex1) }
    }
}