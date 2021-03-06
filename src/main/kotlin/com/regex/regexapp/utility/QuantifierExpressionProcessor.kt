package com.regex.regexapp.utility

import com.regex.regexapp.model.MatchedElement
import com.regex.regexapp.model.RegexDefinition
import org.springframework.stereotype.Component

@Component
class QuantifierExpressionProcessor(quantifierConfig: String) : ExpressionProcessor {
    override val regexDefinitionList: List<RegexDefinition> = readCsv(quantifierConfig)

    override fun firstMatchExpression(regex: String): MatchedElement? {
       return regexDefinitionList.mapNotNull{
           matchExpressionWithDefinition(it,regex)
       }.firstOrNull()
    }

}