package com.regex.regexapp.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("regex.config-file")
open class RegexTypeFinderConfig(private var anchorConfig: String = "") {

    @Bean("anchorConfig")
    open fun anchorConfigFile(): String {
        return anchorConfig
    }
}



