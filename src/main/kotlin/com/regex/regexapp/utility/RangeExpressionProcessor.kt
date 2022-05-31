package com.regex.regexapp.utility

import com.regex.regexapp.model.MatchedElement
import com.regex.regexapp.model.RegexDefinition
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class RangeExpressionProcessor(@Autowired rangeConfig: String) : ExpressionProcessor {
    override val regexDefinitionList: List<RegexDefinition> = readCsv(rangeConfig)

    override fun firstMatchExpression(regex: String): MatchedElement? {
        return regexDefinitionList.mapNotNull { matchExpressionWithDefinition(it, regex) }.firstOrNull()
    }

    override fun matchedElementWithReplacedDescription(
        regexExpression: String,
        firstMatch: Int,
        regexIndex: Int,
        regexDefinition: RegexDefinition,
    ): MatchedElement {
        val replacementString = regexExpression.substring(firstMatch - 1, regexIndex)
            .replace(ExpressionProcessor.inversionCharacter.toString(), "")
            .trim(',')

        return MatchedElement(firstMatch - 1,
            regexIndex,
            regexDefinition.description.replace(ExpressionProcessor.stringToBeReplaced, replacementString))
    }
}