package com.regex.regexapp.utility

import com.regex.regexapp.model.MatchedElement
import com.regex.regexapp.model.Regex
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource

class PosixExpressionProcessorTest {
    private lateinit var posixExpressionProcessor: PosixExpressionProcessor

    @BeforeEach
    internal fun setUp() {
        posixExpressionProcessor = PosixExpressionProcessor("/src/test/resources/posix.csv")
    }

    @ParameterizedTest
    @CsvFileSource(files = ["src/test/resources/posix.csv"] , delimiter = '|')
    fun `return correct definition for all posix expression in list`(expression: String, definition: String) {
        val actual = posixExpressionProcessor.firstMatchedExpression(expression)

        Assertions.assertEquals(MatchedElement(0, expression.length, definition), actual)
    }

}