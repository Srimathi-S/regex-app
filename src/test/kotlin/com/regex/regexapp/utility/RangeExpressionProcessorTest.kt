package com.regex.regexapp.utility

import com.regex.regexapp.model.MatchedElement
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RangeExpressionProcessorTest {

    private lateinit var rangeExpressionProcessor : RangeExpressionProcessor
    @BeforeEach
    fun setUp() {
        rangeExpressionProcessor = RangeExpressionProcessor("/src/test/resources/range.csv")
    }

    @Test
    fun `should return null at which range expression is not there in the string`() {
        val actual = rangeExpressionProcessor.firstMatchExpression("hello")

        assertNull(actual)
    }

    @Test
    fun `should return index at which range expression is found when it has range between elements`() {
        val actual = rangeExpressionProcessor.firstMatchExpression("[a-c]")

        assertEquals(MatchedElement(1, 4, "matches any character from a-c"), actual)
    }

    @Test
    fun `should return index at which range expression is found when it has range not between numbers`() {
        val actual = rangeExpressionProcessor.firstMatchExpression("[^1-3]")

        assertEquals(MatchedElement(2, 5, "matches any character from 1-3"), actual)
    }

}