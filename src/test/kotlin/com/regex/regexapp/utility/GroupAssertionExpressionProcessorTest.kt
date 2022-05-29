package com.regex.regexapp.utility

import com.regex.regexapp.model.MatchedElement
import com.regex.regexapp.model.Regex
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvFileSource

class GroupAssertionExpressionProcessorTest {

    lateinit var groupAssertionExpressionProcessor: GroupAssertionExpressionProcessor
    @BeforeEach
    fun setUp() {
        groupAssertionExpressionProcessor = GroupAssertionExpressionProcessor("/src/test/resources/groupAssertion.csv")
    }

    @ParameterizedTest
    @CsvFileSource(files = ["src/test/resources/groupAssertion.csv"] , delimiter = '|')
    fun `return correct definition for all group assertion expression in list when it is at beginning group`(expression: String, definition: String) {
        val actual = groupAssertionExpressionProcessor.firstMatchedExpression("($expression)")

        Assertions.assertEquals(MatchedElement(1, 1+expression.length, definition), actual)
    }

    @ParameterizedTest
    @CsvFileSource(files = ["src/test/resources/groupAssertion.csv"] , delimiter = '|')
    fun `return correct definition for all group assertion expression in list when it is at last group`(expression: String, definition: String) {
        val actual = groupAssertionExpressionProcessor.firstMatchedExpression("abc($expression)")

        Assertions.assertEquals(MatchedElement(4, 4+expression.length, definition), actual)
    }
}