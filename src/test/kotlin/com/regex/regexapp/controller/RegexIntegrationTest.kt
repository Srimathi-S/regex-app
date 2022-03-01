package com.regex.regexapp.controller

import com.regex.regexapp.model.Regex
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import org.springframework.web.reactive.function.BodyInserters

@SpringBootTest
@AutoConfigureWebTestClient
class RegexIntegrationTest(
    @Autowired
    val webTestClient: WebTestClient
) {

    @Test
    fun `should return conjunction of two regex when the api is called`() {
        val addRegexRequest = AddRegexRequest(Regex("abc"),Regex("def"))

        val result =webTestClient
            .post()
            .uri("/and")
            .body(BodyInserters.fromValue(addRegexRequest))
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .returnResult<Regex>()
            .responseBody.blockFirst()

        assertEquals(result, Regex("(abc)(def)"))

    }
}