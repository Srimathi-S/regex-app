package com.regex.regexapp.utility

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import com.regex.regexapp.model.MatchedElement
import com.regex.regexapp.model.RegexDefinition
import com.regex.regexapp.model.Regex
import java.io.FileReader

interface ExpressionProcessor {
    val regexDefinitionList: List<RegexDefinition>
    fun firstMatchedExpression(regex: Regex): MatchedElement?

    fun matchExpressionWithDefinition(
        regexDefinition: RegexDefinition,
        regex: Regex,
        usesAnyCharacterMatcher: Boolean = true,
        usesAnyStringMatcher: Boolean = true,
    ): MatchedElement? {
        val expressionToCompare = regexDefinition.expression
        val expressionToCompareLength = expressionToCompare.length
        val regexExpression = regex.expression
        val foundIndex = regexExpression.toCharArray().indexOf(expressionToCompare[0])
        if (foundIndex == -1) return null
        val firstMatch = foundIndex + 1
        var regexIndex = firstMatch
        var expressionToCompareIndex = 1
        val regexLength = regexExpression.length

        while (expressionToCompareIndex < expressionToCompareLength && regexIndex < regexLength) {
            when {
                expressionToCompare[expressionToCompareIndex] == regexExpression[regexIndex] -> {
                    regexIndex++
                    expressionToCompareIndex++
                }
                usesAnyStringMatcher && expressionToCompare[expressionToCompareIndex] == anyStringMatcher -> {
                    val index = handleMatchAnyCharacter(expressionToCompareIndex,
                        expressionToCompareLength,
                        expressionToCompare,
                        regexExpression,
                        regexIndex)
                    expressionToCompareIndex = index.first
                    regexIndex = index.second
                }
                usesAnyCharacterMatcher && expressionToCompare[expressionToCompareIndex] == anyCharacterMatcher -> {
                    regexIndex++
                    expressionToCompareIndex++
                }
                else -> {
                    break
                }
            }
        }

        if (expressionToCompareIndex == expressionToCompareLength) {
            return if (regexDefinition.description.contains("_"))
                matchedElementWithReplacedDescription(regexExpression, firstMatch, regexIndex, regexDefinition)
            else
                MatchedElement(firstMatch - 1, regexIndex, regexDefinition.description)
        }

        return null
    }


    private fun handleMatchAnyCharacter(
        expressionToCompareIndex: Int,
        expressionToCompareLength: Int,
        expressionToCompare: String,
        regexExpression: String,
        regexIndex: Int,
    ): Pair<Int, Int> {
        if (expressionToCompareIndex + 1 < expressionToCompareLength && expressionToCompare[expressionToCompareIndex + 1] == regexExpression[regexIndex]) {
            return Pair(expressionToCompareIndex + 2, regexIndex + 1)
        } else {
            if (!nonMatchers.contains(regexExpression[regexIndex])) {
                return Pair(expressionToCompareIndex, regexIndex + 1)
            }
        }
        return Pair(expressionToCompareIndex + 1, regexIndex + 1)
    }

    private fun matchedElementWithReplacedDescription(
        regexExpression: String,
        firstMatch: Int,
        regexIndex: Int,
        regexDefinition: RegexDefinition,
    ): MatchedElement {
        val replacementString = regexExpression.substring(firstMatch, regexIndex - 1)
            .replace(inversionCharacter.toString(), "")
            .trim(',')

        return MatchedElement(firstMatch - 1,
            regexIndex,
            regexDefinition.description.replace(stringToBeReplaced, replacementString))
    }


    companion object {
        const val anyStringMatcher = '*'
        const val anyCharacterMatcher = '.'
        const val inversionCharacter = '^'
        val nonMatchers = listOf(inversionCharacter, '-')
        const val stringToBeReplaced = "_"
    }
}


fun readCsv(fileName: String): List<RegexDefinition> {
    val currentDirectory = System.getProperty("user.dir")
    return CSVReaderBuilder(FileReader(currentDirectory + fileName))
        .withCSVParser(
            CSVParserBuilder()
                .withSeparator('|')
                .withEscapeChar('#')
                .build()
        )
        .build()
        .map { line ->
            RegexDefinition(line[0], line[1])
        }
}

