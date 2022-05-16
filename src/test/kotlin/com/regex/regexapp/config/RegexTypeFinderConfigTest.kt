package com.regex.regexapp.config

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RegexTypeFinderConfigTest {

    private lateinit var regexTypeFinderConfig: RegexTypeFinderConfig

    @BeforeEach
    internal fun setUp() {
        regexTypeFinderConfig = RegexTypeFinderConfig("/src/test/resources/anchor.csv",
            "/src/test/resources/range.csv",
            "/src/test/resources/quantifier.csv")
    }

    @Test
    fun `should return correct anchor config file name with the given filename config`() {
        regexTypeFinderConfig.anchorConfigFile().compareTo("/src/test/resources/anchor.csv")
    }

    @Test
    fun `should return correct range config file name with the given filename config`() {
        regexTypeFinderConfig.rangeConfigFile().compareTo("/src/test/resources/range.csv")
    }

    @Test
    fun `should return correct quantifier config file name with the given filename config`() {
        regexTypeFinderConfig.quantifierConfigFile().compareTo("/src/test/resources/quantifier.csv")
    }
}