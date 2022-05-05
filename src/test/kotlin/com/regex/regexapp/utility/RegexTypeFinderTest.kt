package com.regex.regexapp.utility

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import com.regex.regexapp.model.Regex

@SpringBootTest
class RegexTypeFinderTest(
    @Autowired val regexTypeFinder: RegexTypeFinder
) {

    @Test
    fun `should return definition of regex with multiple anchor tags and other exact string match`() {
        val actual = regexTypeFinder.describe(Regex("hel\\Abh"))

        val expectedDescription = listOf("h", "e", "l", "Start of string", "b", "h")

        assertEquals(expectedDescription, actual)
    }

    @Test
    fun `should return definition of regex with anchor tags`() {
        val regex = Regex("^")
        val expectedDescription = listOf("Start of string")

        val description = regexTypeFinder.describe(regex)

        assertEquals(expectedDescription, description)
    }

    @Test
    fun `should return definition of regex with multiple anchor tags`() {
        val regex = Regex("^\$")
        val expectedDescription = listOf("Start of string", "End of string")

        val description = regexTypeFinder.describe(regex)

        assertEquals(expectedDescription, description)
    }

    @Test
    fun `should return empty list when given regex is empty`() {
        val regex = Regex("")
        val expectedDescription = listOf<String>()

        val description = regexTypeFinder.describe(regex)

        assertEquals(expectedDescription, description)
    }

    @Test
    fun `should return definition of regex with range expression`() {
        val regex = Regex("[a-b]")
        val expectedDescription = listOf("matches any character from a-b")

        val description = regexTypeFinder.describe(regex)

        assertEquals(expectedDescription, description)
    }

    @Test
    fun `should return definition of regex with anchor and range expression`() {
        val regex = Regex("^[^a-b]")
        val expectedDescription = listOf("Start of string","not in range of character from a-b")

        val description = regexTypeFinder.describe(regex)

        assertEquals(expectedDescription, description)
    }

}


