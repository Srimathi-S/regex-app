package com.regex.regexapp.utility

import com.regex.regexapp.model.MatchedElement
import com.regex.regexapp.model.Regex
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
    fun `should return index at which range expression is found when it has single exact range`() {
        val actual = rangeExpressionProcessor.firstMatchedExpression("[a]")

        assertEquals(MatchedElement(0, 3, "matches one of characters in list a"), actual)
    }

    @Test
    fun `should return index at which range expression is found when it has multiple element range`() {
        val actual = rangeExpressionProcessor.firstMatchedExpression("[^abc]")

        assertEquals(MatchedElement(0, 6, "not matches any of characters in list abc"), actual)
    }

    @Test
    fun `should return null at which range expression is not there in the string`() {
        val actual = rangeExpressionProcessor.firstMatchedExpression("hello")

        assertNull(actual)
    }

    @Test
    fun `should return index at which range expression is found when it has range between elements`() {
        val actual = rangeExpressionProcessor.firstMatchedExpression("[a-c]")

        assertEquals(MatchedElement(0, 5, "matches any character from a-c"), actual)
    }

    @Test
    fun `should return index at which range expression is found when it has range not between numbers`() {
        val actual = rangeExpressionProcessor.firstMatchedExpression("[^1-3]")

        assertEquals(MatchedElement(0, 6, "not in range of character from 1-3"), actual)
    }

}