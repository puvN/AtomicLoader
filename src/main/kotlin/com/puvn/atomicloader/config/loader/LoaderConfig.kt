package com.puvn.atomicloader.config.loader

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("loader-config")
data class LoaderConfig(
    val testingPeriodSeconds: Int,
    val requestsPerSecond: Int,
    val profile: LoaderProfile,
    val processPerInstance: Boolean
)

enum class LoaderProfile {
    LOCAL, REMOTE
}