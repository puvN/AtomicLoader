package com.puvn.atomicloader.service.simulation.impl

import com.puvn.atomicloader.config.application_target.ApplicationTargetConfig
import com.puvn.atomicloader.config.loader.LoaderConfig
import com.puvn.atomicloader.config.loader.LoaderProfile
import com.puvn.atomicloader.service.load.LoadService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.time.Duration
import java.time.LocalDateTime

class SingleInstanceSimulationServiceTest {

    private val loadService = mock(LoadService::class.java)

    @Test
    fun should_generate_correct_number_of_requests_for_one_target() {
        val testingPeriodSeconds = 3
        val requestsPerSecond = 10
        val service = SingleInstanceSimulationService(
            LoaderConfig(testingPeriodSeconds, requestsPerSecond, LoaderProfile.LOCAL, false),
            loadService,
            ApplicationTargetConfig(setOf("url1"), setOf("target1"))
        )
        `when`(loadService.makeRequest(anyString())).thenReturn("success")

        service.simulateLoad()

        verify(
            loadService, times(testingPeriodSeconds * requestsPerSecond)
        ).makeRequest(anyString())
    }

    @Test
    fun should_generate_correct_number_of_requests_for_several_targets() {
        val testingPeriodSeconds = 2
        val requestsPerSecond = 100
        val service = SingleInstanceSimulationService(
            LoaderConfig(testingPeriodSeconds, requestsPerSecond, LoaderProfile.LOCAL, false),
            loadService,
            ApplicationTargetConfig(setOf("url1", "url2"), setOf("target1", "target2"))
        )
        `when`(loadService.makeRequest(anyString())).thenReturn("success")

        service.simulateLoad()

        verify(
            loadService, times(testingPeriodSeconds * requestsPerSecond * 4)
        ).makeRequest(anyString())
    }

    @Test
    fun should_generate_requests_for_correct_period() {
        val testingPeriodSeconds = 5
        val requestsPerSecond = 5
        val service = SingleInstanceSimulationService(
            LoaderConfig(testingPeriodSeconds, requestsPerSecond, LoaderProfile.LOCAL, false),
            loadService,
            ApplicationTargetConfig(setOf("url1"), setOf("target1"))
        )
        `when`(loadService.makeRequest(anyString())).thenReturn("success")

        val startTime = LocalDateTime.now()
        service.simulateLoad()
        val endTime = LocalDateTime.now()

        assertEquals(testingPeriodSeconds, Duration.between(startTime, endTime).seconds.toInt())
    }

}