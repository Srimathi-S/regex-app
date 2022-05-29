package com.regex.regexapp.model

import java.lang.IllegalArgumentException
import java.util.regex.Pattern

data class Regex(val expression: String) {

    init{
        try {
            Pattern.compile(expression)
        }catch (e : Exception){
            println(e)
            throw IllegalArgumentException("Invalid regex pattern")
        }
    }

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
