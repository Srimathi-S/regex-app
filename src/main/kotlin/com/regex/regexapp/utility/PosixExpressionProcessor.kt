package com.regex.regexapp.utility

import com.regex.regexapp.model.MatchedElement
import com.regex.regexapp.model.Regex
import com.regex.regexapp.model.RegexDefinition
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class PosixExpressionProcessor(@Autowired posixConfig: String) : ExpressionProcessor{
    override val regexDefinitionList: List<RegexDefinition> = readCsv(posixConfig)

    override fun firstMatchedExpression(regex: String): MatchedElement? {
        return regexDefinitionList.mapNotNull{
            matchExpressionWithDefinition(it, regex, usesAnyCharacterMatcher = false, usesAnyStringMatcher = false)
        }.firstOrNull()
    }

}
