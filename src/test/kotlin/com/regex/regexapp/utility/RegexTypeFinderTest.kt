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

        val expectedDescription = listOf(listOf("h", "e", "l", "Start of string", "b", "h"))

        assertEquals(expectedDescription, actual)
    }

    @Test
    fun `should return definition of regex with anchor tags`() {
        val regex = Regex("^")
        val expectedDescription = listOf(listOf("Start of string"))

        val description = regexTypeFinder.describe(regex)

        assertEquals(expectedDescription, description)
    }

    @Test
    fun `should return definition of regex with multiple anchor tags`() {
        val regex = Regex("^\$")
        val expectedDescription = listOf(listOf("Start of string", "End of string"))

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
        val expectedDescription = listOf(listOf("matches any character from a-b"))

        val description = regexTypeFinder.describe(regex)

        assertEquals(expectedDescription, description)
    }

    @Test
    fun `should return definition of regex with anchor and range expression`() {
        val regex = Regex("^[^a-b]")
        val expectedDescription = listOf(listOf("Start of string","not in range of character from a-b"))

        val description = regexTypeFinder.describe(regex)

        assertEquals(expectedDescription, description)
    }

    @Test
    fun `should return definition of regex with quantifier`() {
        val actual = regexTypeFinder.describe(Regex("a{3,6}"))

        val expectedDescription = listOf(listOf("a","matches previous token between 3,6 times"))

        assertEquals(expectedDescription, actual)
    }

    @Test
    fun `should return definition of regex with quantifier and anchor tags`() {
        val actual = regexTypeFinder.describe(Regex("^a+"))

        val expectedDescription = listOf(listOf("Start of string","a","matches previous token 1 or more times"))

        assertEquals(expectedDescription, actual)
    }

    @Test
    fun `should return definition of regex with quantifier,range and anchor tags`() {
        val actual = regexTypeFinder.describe(Regex("^a*[1-3]"))

        val expectedDescription = listOf(listOf("Start of string","a","matches previous token 0 or more times","matches any character from 1-3"))

        assertEquals(expectedDescription, actual)
    }

    @Test
    fun `should return definition of regex with group`() {
        val actual = regexTypeFinder.describe(Regex("(abc)"))

        val expectedDescription = listOf(listOf("a","b","c"))

        assertEquals(expectedDescription, actual)
    }

    @Test
    fun `should return definition of regex with multiple group`() {
        val actual = regexTypeFinder.describe(Regex("^(abc)(def)"))

        val expectedDescription = listOf(listOf("Start of string"),listOf("a","b","c"),listOf("d","e","f"))

        assertEquals(expectedDescription, actual)
    }

    @Test
    fun `should return definition of regex with group and assertions`() {
        val actual = regexTypeFinder.describe(Regex("(?=abc)"))

        val expectedDescription = listOf(listOf("Lookahead assertion","a","b","c"))

        assertEquals(expectedDescription, actual)
    }

    @Test
    fun `should return definition of regex with group ,assertions and anchor tag`() {
        val actual = regexTypeFinder.describe(Regex("(?=abc)?"))

        val expectedDescription = listOf(listOf("Lookahead assertion","a","b","c"), listOf("matches previous token 0 or 1 times"))

        assertEquals(expectedDescription, actual)
    }

}



