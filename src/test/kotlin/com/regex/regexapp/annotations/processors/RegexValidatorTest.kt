package com.regex.regexapp.annotations.processors

import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import javax.validation.ConstraintValidatorContext

class RegexValidatorTest{

    private val regexValidator = RegexValidator()
    private val constraintValidatorContext = mockk<ConstraintValidatorContext>()


    @Test
    fun `should return false when invalid regex is passed`() {
        assertFalse(regexValidator.isValid("abc[",constraintValidatorContext))
    }

    @Test
    fun `should return true when valid regex is passed`() {
        assertTrue(regexValidator.isValid("abc[1]",constraintValidatorContext))
    }

    @Test
    fun `should return false when null is passed`() {
        assertFalse(regexValidator.isValid("abc[",constraintValidatorContext))
    }
}