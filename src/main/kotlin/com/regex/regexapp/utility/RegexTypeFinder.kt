package com.regex.regexapp.utility

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import com.regex.regexapp.model.Regex

@Component
class RegexTypeFinder(
    @Autowired
    private val anchorExpressionProcessor: AnchorExpressionProcessor,
) {

    fun describe(regex: Regex): List<String> {
        val descriptionList = mutableListOf<String>()
        val expression = regex.expression
        val length = expression.length

        var processedIndex = 0
        var incrementor = 1

        (0..length step incrementor).forEach { currentIndex ->
            val currentExpression = expression.substring(processedIndex, currentIndex)

            anchorExpressionProcessor.firstMatchedExpression(Regex(currentExpression))
                ?.let { (matchedTill, matchedExpression, matchedExpressionDescription) ->
                    val unprocessedElementsStart = if (processedIndex != 0) processedIndex + 1 else processedIndex
                    val unprocessedElementsEnd = currentIndex - matchedExpression.length
                    descriptionList
                        .addAll(returnUnprocessedElements(unprocessedElementsStart, unprocessedElementsEnd, expression))

                    indexChanger(processedIndex, matchedTill).let {
                        incrementor = it.first - currentIndex
                        processedIndex = it.second
                    }

                    descriptionList.add(matchedExpressionDescription)
                } ?: return@forEach
        }

        descriptionList.addAll(returnUnprocessedElements(processedIndex, length, expression))

        return descriptionList
    }

    private fun indexChanger(
        processedIndex: Int,
        totalProcessedLength: Int
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

