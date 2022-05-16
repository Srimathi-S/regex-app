package com.regex.regexapp.utility

import com.regex.regexapp.model.MatchedElement
import com.regex.regexapp.model.Regex
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class QuantifierExpressionProcessorTest {
    private lateinit var quantifierExpressionProcessor: QuantifierExpressionProcessor

    @BeforeEach
    fun setUp() {
        quantifierExpressionProcessor = QuantifierExpressionProcessor("/src/test/resources/quantifier.csv")
    }

    @Test
    fun `should give definition and index when quantifier expression is present at last for format {a,b}`() {
        val actual = quantifierExpressionProcessor.firstMatchedExpression(Regex("a{3,6}"))

        assertEquals(MatchedElement(1, 6, "matches previous token between 3,6 times"), actual)
    }

    @Test
    fun `should give definition and index when quantifier expression is present at last for format star`() {
        val actual = quantifierExpressionProcessor.firstMatchedExpression(Regex("a*"))

        assertEquals(MatchedElement(1, 2, "matches previous token 0 or more times"), actual)
    }

    @Test
    fun `should give definition and index when quantifier expression is present at last for format +`() {
        val actual = quantifierExpressionProcessor.firstMatchedExpression(Regex("a+"))

        assertEquals(MatchedElement(1, 2, "matches previous token 1 or more times"), actual)
    }

    @Test
    fun `should give definition and index when quantifier expression is present at last for format question mark`() {
        val actual = quantifierExpressionProcessor.firstMatchedExpression(Regex("a?"))

        assertEquals(MatchedElement(1, 2, "matches previous token 0 or 1 times"), actual)
    }

    @Test
    fun `should give definition and index when quantifier expression is present in middle for format {a,}`() {
        val actual = quantifierExpressionProcessor.firstMatchedExpression(Regex("a{3,}b"))

        assertEquals(MatchedElement(1, 5, "matches previous token 3 or more times"), actual)
    }

    @Test
    fun `should give definition and index when quantifier expression is present in middle for format {a}`() {
        val actual = quantifierExpressionProcessor.firstMatchedExpression(Regex("a{3}b"))

        assertEquals(MatchedElement(1, 4, "matches previous token exactly 3 times"), actual)
    }
}