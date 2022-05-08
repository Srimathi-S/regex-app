package com.regex.regexapp.utility

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import com.regex.regexapp.model.MatchedElement
import com.regex.regexapp.model.RegexDefinition
import com.regex.regexapp.model.Regex
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.FileReader

sealed interface ExpressionProcessor {
    val regexDefinitionList: List<RegexDefinition>
    fun firstMatchedExpression(regex: Regex): MatchedElement?
}

@Component
class AnchorExpressionProcessor(@Autowired anchorConfig: String) : ExpressionProcessor {
    override val regexDefinitionList: List<RegexDefinition> = readCsv(anchorConfig)


    override fun firstMatchedExpression(regex: Regex): MatchedElement? {
        val anchorExpressionsList: List<String> =
            regexDefinitionList.map { anchorExpression -> anchorExpression.expression }
        return regex.expression
            .findAnyOf(anchorExpressionsList)?.let { anchorExpression ->
                val description = regexDefinitionList.find {
                    it.expression == anchorExpression.second
                }?.description ?: ""
                MatchedElement(
                    anchorExpression.first,
                    anchorExpression.first + anchorExpression.second.length,
                    description
                )
            }
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

@Component
class RangeExpressionProcessor(@Autowired rangeConfig: String) : ExpressionProcessor {
    override val regexDefinitionList: List<RegexDefinition> = readCsv(rangeConfig)

    override fun firstMatchedExpression(regex: Regex): MatchedElement? {
        val expression = regex.expression
        return regexDefinitionList.mapNotNull { matchingRange(it, expression) }.firstOrNull()
    }

    private fun matchingRange(regexDefinition: RegexDefinition, regexExpression: String): MatchedElement? {
        val expressionToCompare = regexDefinition.expression
        val expressionToCompareLength = expressionToCompare.length
        val firstMatch = regexExpression.toCharArray().indexOf(expressionToCompare[0]) + 1
        var regexIndex = firstMatch
        var expressionToCompareIndex = 1
        val regexLength = regexExpression.length

        while (expressionToCompareIndex < expressionToCompareLength && regexIndex < regexLength) {
            when {
                expressionToCompare[expressionToCompareIndex] == regexExpression[regexIndex] -> {
                    regexIndex++
                    expressionToCompareIndex++
                }
                expressionToCompare[expressionToCompareIndex] == anyStringMatcher -> {
                    val index = handleMatchAnyCharacter(expressionToCompareIndex,
                        expressionToCompareLength,
                        expressionToCompare,
                        regexExpression,
                        regexIndex)
                    expressionToCompareIndex = index.first
                    regexIndex = index.second
                }
                expressionToCompare[expressionToCompareIndex] == anyCharacterMatcher -> {
                    regexIndex++
                    expressionToCompareIndex++
                }
                else -> {
                    break
                }
            }
        }

        if (expressionToCompareIndex == expressionToCompareLength) {
            val stringToReplace = regexExpression.substring(firstMatch, regexIndex - 1)
                    .replace(inversionCharacter.toString(), "")

            return MatchedElement(firstMatch - 1,
                regexIndex,
                descriptionMaker(regexDefinition, stringToReplace))
        }
        return null
    }

    private fun handleMatchAnyCharacter(
        definedIndex: Int,
        definedLength: Int,
        definedExpression: String,
        regexExpression: String,
        regexIndex: Int,
    ): Pair<Int, Int> {
        if (definedIndex + 1 < definedLength && definedExpression[definedIndex + 1] == regexExpression[regexIndex]) {
            return Pair(definedIndex + 2, regexIndex + 1)
        } else {
            if (!nonMatchers.contains(regexExpression[regexIndex])) {
                return Pair(definedIndex, regexIndex + 1)
            }
        }
        return Pair(definedIndex + 1, regexIndex + 1)
    }

    private fun descriptionMaker(regexDefinition: RegexDefinition, stringToReplace: String) =
        regexDefinition.description.replace("1", stringToReplace)

    companion object {
        const val anyStringMatcher = '*'
        const val anyCharacterMatcher = '.'
        const val inversionCharacter = '^'
        val nonMatchers = listOf(inversionCharacter, '-')
    }
}

class QuantifierExpressionProcessor(quantifierConfig: String) : ExpressionProcessor {
    override val regexDefinitionList: List<RegexDefinition> = readCsv(quantifierConfig)

    override fun firstMatchedExpression(regex: Regex): MatchedElement? {
       return MatchedElement(1, 5, "matches previous token between 3 and 6 times")
    }

}