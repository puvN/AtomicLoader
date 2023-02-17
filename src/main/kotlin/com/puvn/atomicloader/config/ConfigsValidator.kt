package com.puvn.atomicloader.config

import com.puvn.atomicloader.config.application_target.ApplicationTargetConfig
import com.puvn.atomicloader.config.loader.LoaderConfig
import com.puvn.atomicloader.config.loader.LoaderProfile
import com.puvn.atomicloader.exception.ConfigurationException
import com.puvn.atomicloader.exception.ExceptionEnum.INVALID_URLS_IN_LOCAL_PROFILE
import com.puvn.atomicloader.exception.ExceptionEnum.INVALID_VALUE
import com.puvn.atomicloader.exception.ExceptionEnum.NO_URLS_OR_ENDPOINTS
import com.puvn.atomicloader.logging.Logger
import jakarta.annotation.PostConstruct
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(ApplicationTargetConfig::class, LoaderConfig::class)
class ConfigsValidator(
    private val applicationTargetConfig: ApplicationTargetConfig,
    private val loaderConfig: LoaderConfig
) {

    @PostConstruct
    fun validateConfigs() {
        validateApplicationTargetConfig()
        validateLoaderConfig()
        validateRelatedParams()
    }

    private fun validateApplicationTargetConfig() {
        if (applicationTargetConfig.urls.isEmpty() || applicationTargetConfig.endpoints.isEmpty()) {
            throw ConfigurationException(NO_URLS_OR_ENDPOINTS)
        }
        log.debug("application target config loaded and validated")
    }

    private fun validateLoaderConfig() {
        if (loaderConfig.requestsPerSecond <= 0 || loaderConfig.testingPeriodSeconds <= 0) {
            throw ConfigurationException(INVALID_VALUE)
        }
        log.debug("loader config loaded and validated")
    }

    private fun validateRelatedParams() {
        if (loaderConfig.profile == LoaderProfile.LOCAL) {
            applicationTargetConfig.urls.forEach {
                val itLowerVal = it.lowercase()
                if (!itLowerVal.contains(localhost.lowercase()) && !it.contains(localhostIpV4)) {
                    throw ConfigurationException(INVALID_URLS_IN_LOCAL_PROFILE)
                }
            }
        }
        log.debug("related (cross) params validated")
    }

    companion object {
        val log by Logger()
        const val localhost = "localhost"
        const val localhostIpV4 = "127.0.0.1"
    }

}