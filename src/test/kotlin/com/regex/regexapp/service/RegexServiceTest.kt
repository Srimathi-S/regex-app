package com.regex.regexapp.service

import com.regex.regexapp.model.Regex
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import reactor.kotlin.test.test

class RegexServiceTest {

    private val regexService = RegexService()

    private var regex1 = mockk<Regex>()
    private var regex2 = mockk<Regex>()

    @Test
    fun `should return conjunction of the expression`() {
        every { regex1.and(any()) } returns Regex("")

        val conjunction = regexService.conjunction(regex1, regex2)

        conjunction.test()
            .expectNext(Regex(""))
            .verifyComplete()
    }

    @Test
    fun `should call add of the first regex with second regex to find conjunction`() {
        regex2 = Regex("regex2")
        every { regex1.and(any()) } returns Regex("")

        regexService.conjunction(regex1, regex2)

        verify { regex1.and(regex2) }
    }
}