package com.regex.regexapp.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("regex.config-file")
open class RegexTypeFinderConfig(var anchorConfig: String = "", var rangeConfig: String = "", var quantifierConfig: String = "") {

    @Bean("anchorConfig")
    open fun anchorConfigFile(): String {
        return anchorConfig
    }

    @Bean("rangeConfig")
    open fun rangeConfigFile(): String {
        return rangeConfig
    }

    @Bean("quantifierConfig")
    open fun quantifierConfigFile(): String {
        return quantifierConfig
    }
}



