package com.regex.regexapp.utility

import com.regex.regexapp.model.MatchedElement
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import com.regex.regexapp.model.Regex

@Component
class RegexTypeFinder(
    @Autowired
    private val anchorExpressionProcessor: AnchorExpressionProcessor,
    @Autowired
    private val rangeExpressionProcessor: RangeExpressionProcessor,
    @Autowired
    private val quantifierExpressionProcessor: QuantifierExpressionProcessor,
) {

    fun describe(regex: Regex): List<String> {
        val descriptionList = mutableListOf<String>()
        val expression = regex.expression
        val length = expression.length

        var processedIndex = 0
        var currentIndex = 1

        while(currentIndex <= length) {
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

            currentIndex +=incrementor
        }

        descriptionList.addAll(returnUnprocessedElements(processedIndex, length, expression))

        return descriptionList
    }

    private fun matchedElement(expression: String, processedIndex: Int, currentIndex: Int): MatchedElement? {
        val currentExpression = expression.substring(processedIndex, currentIndex)
        val regex = Regex(currentExpression)
        return anchorExpressionProcessor.firstMatchedExpression(regex)
            .switchIfNull {
                shouldProcessAsRangeExpression(expression, processedIndex, expression)
            }
            .switchIfNull { quantifierExpressionProcessor.firstMatchedExpression(regex) }
    }

    private fun shouldProcessAsRangeExpression(
        currentExpression: String,
        processedIndex: Int,
        expression: String,
    ): MatchedElement? {
        val rangeExpressionStarter = '['
        val rangeExpressionEnd = ']'
        if (processedIndex<expression.length && currentExpression[processedIndex] == rangeExpressionStarter) {
            val find = currentExpression.indexOf(rangeExpressionEnd,processedIndex)
            if (find != -1) {
                return rangeExpressionProcessor.firstMatchedExpression(Regex(expression.substring(processedIndex, find + 1)))
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


}

private fun MatchedElement?.switchIfNull(function: () -> MatchedElement?): MatchedElement? {
    if (this == null) {
        return function()
    }
    return this
}

