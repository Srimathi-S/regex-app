package com.regex.regexapp.utility

import com.regex.regexapp.model.MatchedElement
import com.regex.regexapp.model.Regex
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class RegexTypeFinder(
    @Autowired
    private val anchorExpressionProcessor: AnchorExpressionProcessor,
    @Autowired
    private val rangeExpressionProcessor: RangeExpressionProcessor,
    @Autowired
    private val quantifierExpressionProcessor: QuantifierExpressionProcessor,
    @Autowired
    private val groupAssertionExpressionProcessor: GroupAssertionExpressionProcessor,
    @Autowired
    private val posixExpressionProcessor: PosixExpressionProcessor,
) {

    fun describe(regex: Regex): List<MutableList<String>> {
        return separateGroup(regex).map {
            individualGroupProcessor(it)
        }
    }

    private fun separateGroup(regex: Regex): List<Regex> {
        val groupExpressionStarter = '('
        val groupExpressionEnd = ')'
        val expression = regex.expression
        var start = 0
        var unprocessedStart = 0
        val groupList = mutableListOf<Regex>()

        val length = expression.length

        while (start < length) {
            if (expression[start] == groupExpressionStarter) {
                unprocessedGroup(expression, unprocessedStart, start)?.let { groupList.add(it) }

                val groupEndIndex = expression.indexOf(groupExpressionEnd, start)
                groupList.add(createRegex(expression, start + 1, groupEndIndex))

                start = groupEndIndex + 1
                unprocessedStart = groupEndIndex + 1
            } else {
                start++
            }
        }

        unprocessedGroup(expression, unprocessedStart, length)?.let { groupList.add(it) }

        return groupList
    }

    private fun unprocessedGroup(
        expression: String,
        start: Int,
        end: Int,
    ): Regex? {
        if (end > start) return createRegex(expression, start, end)
        return null
    }

    private fun individualGroupProcessor(regex: Regex): MutableList<String> {
        val descriptionList = mutableListOf<String>()
        val expression = regex.expression
        val length = expression.length

        var processedIndex = 0
        var currentIndex = 1

        groupAssertionExpressionProcessor.firstMatchedExpression(regex)
            ?.let { (_, matchedTill, matchedExpressionDescription) ->
                descriptionList.add(matchedExpressionDescription)
                processedIndex = matchedTill
                currentIndex = matchedTill + 1
            }

        while (currentIndex <= length) {
            var incrementor = 1

            matchedElement(expression, processedIndex, currentIndex)
                ?.let { (matchedStart, matchedTill, matchedExpressionDescription) ->
                    val unprocessedElementsStart = processedIndex
                    val unprocessedElementsEnd = currentIndex - matchedTill + matchedStart
                    descriptionList
                        .addAll(returnUnprocessedElements(unprocessedElementsStart, unprocessedElementsEnd, expression))

                    indexChanger(processedIndex, matchedTill).let {
                        incrementor = it.first - currentIndex
                        processedIndex = it.second
                    }

                    descriptionList.add(matchedExpressionDescription)
                }

            currentIndex += incrementor
        }

        descriptionList.addAll(returnUnprocessedElements(processedIndex, length, expression))

        return descriptionList
    }

    private fun matchedElement(expression: String, processedIndex: Int, currentIndex: Int): MatchedElement? {
        val currentExpression = expression.substring(processedIndex, currentIndex)
        val regex = Regex(currentExpression)
        return anchorExpressionProcessor.firstMatchedExpression(regex)
            .switchIfNull {
                processForRangeExpression(processedIndex, expression)
            }
            .switchIfNull { quantifierExpressionProcessor.firstMatchedExpression(regex) }
    }

    private fun processForRangeExpression(
        processedIndex: Int,
        expression: String,
    ): MatchedElement? {
        val rangeExpressionStarter = '['
        val rangeExpressionEnd = ']'
        if (processedIndex < expression.length && expression[processedIndex] == rangeExpressionStarter) {
            val rangeEndIndex = expression.indexOf(rangeExpressionEnd, processedIndex)
            if (rangeEndIndex != -1) {
                return posixExpressionProcessor.firstMatchedExpression(createRegex(expression,
                    processedIndex + 1,
                    rangeEndIndex + 1))
                    ?.let {
                        MatchedElement(processedIndex + 1, rangeEndIndex + 2, it.description)
                    }.switchIfNull {
                        rangeExpressionProcessor.firstMatchedExpression(createRegex(expression,
                            processedIndex,
                            rangeEndIndex + 1))
                    }
            }
        }
        return null
    }

    private fun indexChanger(
        processedIndex: Int,
        totalProcessedLength: Int,
    ): Pair<Int, Int> {
        return Pair(processedIndex + totalProcessedLength + 1, processedIndex + totalProcessedLength)
    }

    private fun returnUnprocessedElements(
        unprocessedElementsStart: Int,
        unprocessedElementsEnd: Int,
        expression: String,
    ): MutableList<String> {
        val list = mutableListOf<String>()
        return (unprocessedElementsStart until unprocessedElementsEnd)
            .asSequence()
            .takeWhile { it < expression.length }
            .mapTo(list) { expression[it].toString() }
    }

    private fun createRegex(expression: String, startIndex: Int, endIndex: Int) =
        Regex(expression.substring(startIndex, endIndex))
}

private fun MatchedElement?.switchIfNull(function: () -> MatchedElement?): MatchedElement? {
    if (this == null) {
        return function()
    }
    return this
}

