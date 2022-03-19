package com.regex.regexapp.utility

import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import com.regex.regexapp.model.AnchorExpression
import com.regex.regexapp.model.Regex
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.FileReader

sealed interface ExpressionProcessor<T> {
    val expressionList: List<T>
    fun firstMatchedExpression(regex: Regex): Triple<Int, String, String>?
}

@Component
class AnchorExpressionProcessor(@Autowired anchorConfig: String) : ExpressionProcessor<AnchorExpression> {
    override val expressionList: List<AnchorExpression> = readCsv(anchorConfig)


    override fun firstMatchedExpression(regex: Regex): Triple<Int, String, String>? {
        val anchorExpressions: List<String> = expressionList.map { anchorExpression -> anchorExpression.expression }
        return regex.expression
            .findAnyOf(anchorExpressions)?.let { anchorExpressions ->
                val description = expressionList.find {
                    it.expression == anchorExpressions.second
                }?.description ?: ""
                Triple(anchorExpressions.first + anchorExpressions.second.length, anchorExpressions.second, description)
            }
    }

}

fun readCsv(fileName: String): List<AnchorExpression> {
    val currentDirectory = System.getProperty("user.dir")
    return CSVReaderBuilder(FileReader(currentDirectory + fileName))
        .withCSVParser(
            CSVParserBuilder()
                .withSeparator(',')
                .withEscapeChar('#')
                .build()
        )
        .build()
        .map { line ->
            AnchorExpression(line[0], line[1])
        }
}