package com.regex.regexapp.model

import com.regex.regexapp.annotations.RegularExpression


data class Regex(@field:RegularExpression val expression: String) {

    fun and(regex: Regex): Regex {
        if (expression == "" && regex.expression == "") return Regex("")
        if (expression == "") return regex
        if (regex.expression == "") return this
        return Regex(createGroup() + regex.createGroup())
    }

    private fun createGroup(): String {
        return "(${expression})"
    }

}
