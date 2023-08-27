package com.puvn.atomicloader.config

import com.puvn.atomicloader.config.application_target.ApplicationTargetConfig
import com.puvn.atomicloader.config.loader.LoaderConfig
import com.puvn.atomicloader.config.loader.LoaderProfile
import com.puvn.atomicloader.exception.ConfigurationException
import com.puvn.atomicloader.exception.ExceptionEnum.INVALID_URLS_IN_LOCAL_PROFILE
import com.puvn.atomicloader.exception.ExceptionEnum.INVALID_VALUE
import com.puvn.atomicloader.exception.ExceptionEnum.NO_ENDPOINTS
import com.puvn.atomicloader.exception.ExceptionEnum.NO_URLS
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
        if (applicationTargetConfig.urls.isEmpty()) {
            throw ConfigurationException(exceptionEnum = NO_URLS)
        }
        if (applicationTargetConfig.endpoints.isEmpty()) {
            throw ConfigurationException(exceptionEnum = NO_ENDPOINTS)
        }
        if (applicationTargetConfig.waitForStartSeconds < 0) {
            throw ConfigurationException(
                parameterName = WAIT_FOR_START_SECONDS_PARAM_NAME,
                exceptionEnum = INVALID_VALUE
            )
        }
        log.debug("application target config loaded and validated")
    }

    private fun validateLoaderConfig() {
        if (loaderConfig.requestsPerSecond <= 0) {
            throw ConfigurationException(REQUESTS_PER_SECOND_PARAM_NAME, INVALID_VALUE)
        }
        if (loaderConfig.testingPeriodSeconds <= 0) {
            throw ConfigurationException(TESTING_PERIOD_SECONDS_PARAM_NAME, INVALID_VALUE)
        }
        log.debug("loader config loaded and validated")
    }

    private fun validateRelatedParams() {
        if (loaderConfig.profile == LoaderProfile.LOCAL) {
            applicationTargetConfig.urls.forEach {
                val itLowerVal = it.lowercase()
                if (!itLowerVal.contains(LOCALHOST.lowercase()) && !it.contains(LOCALHOST_IPV4)) {
                    throw ConfigurationException(exceptionEnum = INVALID_URLS_IN_LOCAL_PROFILE)
                }
            }
        }
        log.debug("related (cross) params validated")
    }

    companion object {
        val log by Logger()
        const val LOCALHOST = "localhost"
        const val LOCALHOST_IPV4 = "127.0.0.1"

        const val REQUESTS_PER_SECOND_PARAM_NAME = "requestsPerSecond"
        const val TESTING_PERIOD_SECONDS_PARAM_NAME = "testingPeriodSeconds"
        const val WAIT_FOR_START_SECONDS_PARAM_NAME = "waitForStartSeconds"
    }

}