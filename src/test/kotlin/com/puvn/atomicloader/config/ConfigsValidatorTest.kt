package com.puvn.atomicloader.config

import com.puvn.atomicloader.config.application_target.ApplicationTargetConfig
import com.puvn.atomicloader.config.loader.LoaderConfig
import com.puvn.atomicloader.config.loader.LoaderProfile
import com.puvn.atomicloader.exception.ConfigurationException
import com.puvn.atomicloader.exception.ExceptionEnum
import com.puvn.atomicloader.exception.buildExceptionMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class ConfigsValidatorTest {

    @Test
    fun check_valid_config_not_throw_exception() {
        val loaderConfig = LoaderConfig(
            10, 5, LoaderProfile.LOCAL, false
        )
        val applicationTargetConfig = ApplicationTargetConfig(
            setOf("localhost:8081", "127.0.0.1:8082"), setOf("/endpoint1", "/v2/endpoint2")
        )
        assertDoesNotThrow { ConfigsValidator(applicationTargetConfig, loaderConfig) }
    }

    @Test
    fun check_config_throws_no_urls() {
        val loaderConfig = LoaderConfig(
            10, 5, LoaderProfile.LOCAL, false
        )
        val applicationTargetConfig = ApplicationTargetConfig(
            emptySet(), setOf("/endpoint1", "/v2/endpoint2")
        )
        val result = assertThrows<ConfigurationException> {
            ConfigsValidator(applicationTargetConfig, loaderConfig).validateConfigs()
        }
        assertEquals(result.message, ExceptionEnum.NO_URLS.exceptionString)
    }

    @Test
    fun check_config_throws_no_endpoints() {
        val loaderConfig = LoaderConfig(
            10, 5, LoaderProfile.LOCAL, false
        )
        val applicationTargetConfig = ApplicationTargetConfig(
            setOf("localhost:8081", "127.0.0.1:8082"), emptySet()
        )
        val result = assertThrows<ConfigurationException> {
            ConfigsValidator(applicationTargetConfig, loaderConfig).validateConfigs()
        }
        assertEquals(result.message, ExceptionEnum.NO_ENDPOINTS.exceptionString)
    }

    @Test
    fun check_config_throws_invalid_value() {
        val invalidPeriodSeconds = -10
        val invalidRequestsPerSeconds = 0

        var loaderConfig = LoaderConfig(
            invalidPeriodSeconds, 5, LoaderProfile.LOCAL, false
        )
        val applicationTargetConfig = ApplicationTargetConfig(
            setOf("localhost:8081", "127.0.0.1:8082"), setOf("/endpoint1", "/v2/endpoint2")
        )
        var result = assertThrows<ConfigurationException> {
            ConfigsValidator(applicationTargetConfig, loaderConfig).validateConfigs()
        }
        assertEquals(
            result.message, buildExceptionMessage(
                ExceptionEnum.INVALID_VALUE.exceptionString,
                ConfigsValidator.TESTING_PERIOD_SECONDS_PARAM_NAME
            )
        )

        loaderConfig = LoaderConfig(
            10, invalidRequestsPerSeconds, LoaderProfile.LOCAL, false
        )
        result = assertThrows {
            ConfigsValidator(applicationTargetConfig, loaderConfig).validateConfigs()
        }
        assertEquals(
            result.message,
            buildExceptionMessage(
                ExceptionEnum.INVALID_VALUE.exceptionString,
                ConfigsValidator.REQUESTS_PER_SECOND_PARAM_NAME
            )
        )
    }

}