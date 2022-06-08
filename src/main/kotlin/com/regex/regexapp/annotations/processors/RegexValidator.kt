package com.regex.regexapp.annotations.processors

import com.regex.regexapp.annotations.RegularExpression
import java.util.regex.Pattern
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext

class RegexValidator : ConstraintValidator<RegularExpression, String> {
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        return if (value != null) {
            try {
                Pattern.compile(value)
                true
            } catch (e: Exception) {
                false
            }
        } else false
    }

    override fun initialize(constraintAnnotation: RegularExpression?) {
    }
}
