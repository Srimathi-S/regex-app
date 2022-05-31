package com.regex.regexapp.utility

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import com.regex.regexapp.model.MatchedElement
import com.regex.regexapp.model.RegexDefinition
import java.io.FileReader

interface ExpressionProcessor {
    val regexDefinitionList: List<RegexDefinition>
    fun firstMatchExpression(regex: String): MatchedElement?

    fun matchExpressionWithDefinition(
        regexDefinition: RegexDefinition,
        regexExpression: String,
        usesAnyCharacterMatcher: Boolean = true,
        usesAnyStringMatcher: Boolean = true,
    ): MatchedElement? {
        val expressionToCompare = regexDefinition.expression
        val expressionToCompareLength = expressionToCompare.length
        val foundIndex = firstMatch(regexExpression, expressionToCompare,usesAnyCharacterMatcher)
        if (foundIndex < 0) return null
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
            return if (regexDefinition.description.contains(stringToBeReplaced))
                matchedElementWithReplacedDescription(regexExpression, firstMatch, regexIndex, regexDefinition)
            else
                MatchedElement(firstMatch - 1, regexIndex, regexDefinition.description)
        }

        return null
    }

    fun firstMatch(regexExpression: String, expressionToCompare: String, usesAnyCharacterMatcher: Boolean): Int {
        val isFirstCharAnyCharacterMatcher =
            usesAnyCharacterMatcher && expressionToCompare[0] == anyCharacterMatcher && expressionToCompare.length > 1
        val regexArray = regexExpression.toCharArray()
        return if (isFirstCharAnyCharacterMatcher) regexArray.indexOf(expressionToCompare[1]) - 1 else regexArray.indexOf(
            expressionToCompare[0])
    }


    private fun handleMatchAnyCharacter(
        expressionToCompareIndex: Int,
        expressionToCompareLength: Int,
        expressionToCompare: String,
        regexExpression: String,
        regexIndex: Int,
    ): Pair<Int, Int> {
        val canStartMatchingNextCharacter =
            expressionToCompareIndex + 1 < expressionToCompareLength && expressionToCompare[expressionToCompareIndex + 1] == regexExpression[regexIndex]
        if (canStartMatchingNextCharacter) {
            return Pair(expressionToCompareIndex + 2, regexIndex + 1)
        } else {
            if (!nonMatchers.contains(regexExpression[regexIndex])) {
                return Pair(expressionToCompareIndex, regexIndex + 1)
            }
        }
        return Pair(expressionToCompareIndex + 1, regexIndex + 1)
    }

    fun matchedElementWithReplacedDescription(
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

