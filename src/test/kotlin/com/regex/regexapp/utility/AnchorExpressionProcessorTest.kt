package com.regex.regexapp.utility

import com.regex.regexapp.model.MatchedElement
import com.regex.regexapp.model.Regex
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource

class AnchorExpressionProcessorTest {

    private lateinit var anchorExpressionProcessor: AnchorExpressionProcessor

    @BeforeEach
    internal fun setUp() {
        anchorExpressionProcessor = AnchorExpressionProcessor("/src/test/resources/anchor.csv")
    }

    @Test
    fun `should return index at which anchor expression is found when it is at the start`() {
        val actual = anchorExpressionProcessor.firstMatchedExpression(Regex("^"))

        assertEquals(MatchedElement(0, 1, "Start of string"), actual)
    }


    @Test
    fun `should return index at which anchor expression is found when it is at the last`() {
        val actual = anchorExpressionProcessor.firstMatchedExpression(Regex("hel$"))

        assertEquals(MatchedElement(3, 4, "End of string"), actual)
    }

    @Test
    fun `should return index at which first anchor expression is found when there are more than 1 expressions`() {
        val actual = anchorExpressionProcessor.firstMatchedExpression(Regex("helo\\A\\B"))

        assertEquals(MatchedElement(4, 6, "Start of string"), actual)
    }

    @Test
    fun `should return null at which anchor expression is not there in the string`() {
        val actual = anchorExpressionProcessor.firstMatchedExpression(Regex("hello"))

        assertNull(actual)
    }

    @ParameterizedTest
    @CsvFileSource(files = ["src/test/resources/anchor.csv"])
    fun `return correct definition for all anchor expression in list`(expression: String, definition: String) {
        val actual = anchorExpressionProcessor.firstMatchedExpression(Regex(expression))

        assertEquals(MatchedElement(0, expression.length, definition), actual)
    }
}