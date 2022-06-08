package com.regex.regexapp.controller

import com.regex.regexapp.model.Regex
import com.regex.regexapp.model.ImproperRegexException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import org.springframework.web.reactive.function.BodyInserters
import reactor.test.StepVerifier
import javax.validation.Validation

@SpringBootTest
@AutoConfigureWebTestClient
class RegexControllerIntegrationTest(
    @Autowired
    val webTestClient: WebTestClient
) {
    private val validationFactory = Validation.buildDefaultValidatorFactory()
    private val validator = validationFactory.validator

    @Test
    fun `should return conjunction of two regex when the api is called`() {
        val addRegexRequest = AddRegexRequest(Regex("abc"), Regex("def"))

        val result = webTestClient
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

    @Test
    fun `should return description of regex when the api is called`() {
        val regex = Regex("^[^a-b]*{3,6}")
        val expectedDescription = "[[\"Start of string\",\"not matches any character from a-b\",\"matches previous token 0 or more times\",\"matches previous token between 3,6 times\"]]"

        val result = webTestClient
            .post()
            .uri("/describe")
            .body(BodyInserters.fromValue(regex))
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .is2xxSuccessful
            .returnResult<String>()
            .responseBody

        StepVerifier.create(result)
            .consumeNextWith {
                assertEquals(expectedDescription, it)
            }
            .verifyComplete()
    }

    @Test
    fun `should return bad request when the api is called with improper regex`() {
        val regex = Regex("^[^a-")

        val responseBody = webTestClient
            .post()
            .uri("/describe")
            .body(BodyInserters.fromValue(regex))
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isBadRequest
            .returnResult<ImproperRegexException>()
            .responseBody

        StepVerifier.create(responseBody)
            .consumeNextWith { assertEquals(it,ImproperRegexException("Please enter a valid regex")) }
            .verifyComplete()

    }
}