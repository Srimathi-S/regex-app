package com.regex.regexapp.utility

import com.regex.regexapp.model.MatchedElement
import com.regex.regexapp.model.Regex
import com.regex.regexapp.utility.ExpressionProcessor.Companion.inversionCharacter
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

    private fun separateGroup(regex: Regex): List<String> {
        val expression = regex.expression
        var start = 0
        var unprocessedStart = 0
        val groupList = mutableListOf<String>()

        val length = expression.length

        while (start < length) {
            if (expression[start] == groupExpressionStarter) {
                unprocessedGroup(expression, unprocessedStart, start)?.let { groupList.add(it) }

                val groupEndIndex = expression.indexOf(groupExpressionEnd, start)
                groupList.add(createSubstring(expression, start + 1, groupEndIndex))

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
    ): String? {
        if (end > start) return createSubstring(expression, start, end)
        return null
    }

    private fun individualGroupProcessor(expression: String): MutableList<String> {
        val descriptionList = mutableListOf<String>()
        val length = expression.length

        var processedIndex = 0
        var currentIndex = 0

        groupAssertionExpressionProcessor.firstMatchExpression(expression)
            ?.let { (_, matchedTill, matchedExpressionDescription) ->
                descriptionList.add(matchedExpressionDescription)
                processedIndex = matchedTill
                currentIndex = matchedTill + 1
            }

        while (currentIndex <= length) {
            var incrementor = 1

            matchedElement(expression, processedIndex, currentIndex)
                ?.let { matchedElement ->

                    descriptionList.addAll(processMatchedElementForDescription(matchedElement,
                        processedIndex,
                        matchedElement.endIndex + processedIndex,
                        expression))

                    indexChanger(processedIndex, matchedElement.endIndex).let {
                        incrementor = it.first - currentIndex
                        processedIndex = it.second
                    }
                }

            currentIndex += incrementor
        }

        descriptionList.addAll(unprocessedElements(processedIndex, length, expression))

        return descriptionList
    }

    private fun processMatchedElementForDescription(
        matchedElement: MatchedElement,
        unprocessedElementStart: Int,
        currentIndex: Int,
        expression: String,
    ): MutableList<String> {
        val descriptionList = mutableListOf<String>()
        val (matchedStart, matchedTill, matchedExpressionDescription) = matchedElement
        val unprocessedElementsEnd = currentIndex - matchedTill + matchedStart
        descriptionList
            .addAll(unprocessedElementsWithoutInversionCharacter(unprocessedElementStart,
                unprocessedElementsEnd,
                expression))


        descriptionList.add(matchedExpressionDescription)
        return descriptionList
    }

    private fun matchedElement(expression: String, processedIndex: Int, currentIndex: Int): MatchedElement? {
        val currentExpression = expression.substring(processedIndex, currentIndex)
        return anchorExpressionProcessor.firstMatchExpression(currentExpression)
            .switchIfNull {
                processForPosixAndRangeExpression(currentIndex, processedIndex, expression)
            }
            .switchIfNull { quantifierExpressionProcessor.firstMatchExpression(currentExpression) }
    }

    private fun processForPosixAndRangeExpression(
        currentIndex: Int,
        processedIndex: Int,
        expression: String,
    ): MatchedElement? {
        val rangeList = mutableListOf<String>()
        val length = expression.length

        val startIndex = startIndexForRangeProcessing(processedIndex, length, expression, currentIndex)

        if (startIndex != null) {
            var presentIndex = startIndex + 1
            var processedTill = startIndex + 1
            var totalUnclosed = 1

            while (presentIndex < length && totalUnclosed > 0) {
                var incrementor = 1

                totalUnclosed = changeTotalUnclosed(expression, presentIndex, totalUnclosed)

                rangePosixProcessor(expression, processedTill, presentIndex)
                    ?.let { matchedElement ->
                        rangeList.addAll(processMatchedElementForDescription(matchedElement,
                            processedTill,
                            presentIndex,
                            expression))
                        indexChanger(processedTill, matchedElement.endIndex).let {
                            incrementor = it.first - presentIndex
                            processedTill = it.second
                        }
                    }

                presentIndex += incrementor
            }

            rangeList.addAll(unprocessedElementsWithoutInversionCharacter(processedTill,
                presentIndex - 1,
                expression))

            val description = rangeDescription(processedIndex, length, expression, rangeList)
            return MatchedElement(startIndex - processedIndex, presentIndex - processedIndex, description)
        }
        return null
    }

    private fun startIndexForRangeProcessing(
        processedIndex: Int,
        length: Int,
        expression: String,
        currentIndex: Int,
    ) = if (processedIndex < length && expression[processedIndex] == rangeExpressionStarter) processedIndex
    else if (currentIndex < length && expression[currentIndex] == rangeExpressionStarter) currentIndex
    else null

    private fun changeTotalUnclosed(
        expression: String,
        presentIndex: Int,
        totalUnclosed: Int,
    ) = when (expression[presentIndex]) {
        rangeExpressionEnd -> totalUnclosed - 1
        rangeExpressionStarter -> totalUnclosed + 1
        else -> totalUnclosed
    }

    private fun rangeDescription(
        processedIndex: Int,
        length: Int,
        expression: String,
        rangeList: MutableList<String>,
    ): String {
        val initial = initialDescription(processedIndex, length, expression)
        val combinerString = rangeDescriptionCombine(processedIndex, length, expression)

        val description = rangeList.fold(initial) { acc, description ->
            if (acc.isEmpty()) description else if (acc == NOT) "$acc $description" else "$acc $combinerString $description"
        }
        return description
    }

    private fun rangeDescriptionCombine(processedIndex: Int, length: Int, expression: String): Any {
        val isNonMatcher = processedIndex + 1 < length && expression[processedIndex + 1] == inversionCharacter
        if (isNonMatcher) {
            return "$AND $NOT"
        }
        return OR
    }

    private fun unprocessedElementsWithoutInversionCharacter(
        processedTill: Int,
        currentIndex: Int,
        expression: String,
    ): MutableList<String> {
        val elements = unprocessedElements(processedTill, currentIndex, expression)
        elements.remove(inversionCharacter.toString())
        return elements
    }

    private fun initialDescription(processedIndex: Int, length: Int, expression: String): String {
        val isNonMatcher = processedIndex + 1 < length && expression[processedIndex + 1] == inversionCharacter
        if (isNonMatcher) {
            return NOT
        }
        return ""
    }

    private fun rangePosixProcessor(expression: String, processedIndex: Int, currentIndex: Int): MatchedElement? {
        val currentExpression = expression.substring(processedIndex, currentIndex)
        return rangeExpressionProcessor.firstMatchExpression(currentExpression)
            .switchIfNull {
                posixExpressionProcessor.firstMatchExpression(currentExpression)
            }
    }

    private fun indexChanger(
        processedIndex: Int,
        totalProcessedLength: Int,
    ): Pair<Int, Int> {
        return Pair(processedIndex + totalProcessedLength + 1, processedIndex + totalProcessedLength)
    }

    private fun unprocessedElements(
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

    private fun createSubstring(expression: String, startIndex: Int, endIndex: Int) =
        expression.substring(startIndex, endIndex)

    companion object {
        const val AND = "and"
        const val OR = "or"
        const val NOT = "not"
        const val rangeExpressionStarter = '['
        const val rangeExpressionEnd = ']'
        const val groupExpressionStarter = '('
        const val groupExpressionEnd = ')'
    }
}

private fun MatchedElement?.switchIfNull(function: () -> MatchedElement?): MatchedElement? {
    if (this == null) {
        return function()
    }
    return this
}

