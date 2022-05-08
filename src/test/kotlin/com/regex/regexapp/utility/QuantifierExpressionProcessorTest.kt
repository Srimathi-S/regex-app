package com.regex.regexapp.utility

import com.regex.regexapp.model.MatchedElement
import com.regex.regexapp.model.Regex
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class QuantifierExpressionProcessorTest {
    private lateinit var quantifierExpressionProcessor : QuantifierExpressionProcessor
    @BeforeEach
    fun setUp() {
        quantifierExpressionProcessor = QuantifierExpressionProcessor("/src/test/resources/quantifier.csv")
    }

    @Test
   fun `should give definition and index at which quantifier expression is present`() {
        val actual = quantifierExpressionProcessor.firstMatchedExpression(Regex("a{3,6}"))

        assertEquals(MatchedElement(1, 5, "matches previous token between 3 and 6 times"), actual)
   }
}