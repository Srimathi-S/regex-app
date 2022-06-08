package com.regex.regexapp.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RegexTest {

    private lateinit var regex1: Regex
    private lateinit var regex2: Regex

    @Test
    fun `should return empty expression in conjunction of two regex for empty regex`() {
        regex1 = Regex("")
        regex2 = Regex("")
        val conjunction = regex1.and(regex2)

        assertTrue(conjunction == Regex(""))
    }

    @Test
    fun `should return empty expression in conjunction of two regex for single empty regex`() {
        regex1 = Regex("[1]{2}")
        regex2 = Regex("")
        val conjunction = regex1.and(regex2)

        assertTrue(conjunction == Regex("[1]{2}"))
    }

    @Test
    fun `should return conjunction of two regex for number based regex`() {
        regex1 = Regex("[1]{1}")
        regex2 = Regex("[1]{2}")
        val conjunction = regex1.and(regex2)

        assertTrue(conjunction == Regex("([1]{1})([1]{2})"))
    }

    @Test
    fun `should return conjunction of two regex for string based regex`() {
        regex1 = Regex("a{1}")
        regex2 = Regex("a{2}")
        val conjunction = regex1.and(regex2)

        assertTrue(conjunction == Regex("(a{1})(a{2})"))
    }
}