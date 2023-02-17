package com.puvn.atomicloader.config.application_target

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("application-target")
data class ApplicationTargetConfig(
    val urls: Set<String>,
    val endpoints: Set<String>
)