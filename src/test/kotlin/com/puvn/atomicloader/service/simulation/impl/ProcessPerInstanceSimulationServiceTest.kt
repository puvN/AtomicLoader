package com.puvn.atomicloader.service.simulation.impl

import com.puvn.atomicloader.config.application_target.ApplicationTargetConfig
import com.puvn.atomicloader.config.loader.LoaderConfig
import com.puvn.atomicloader.config.loader.LoaderProfile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap

class ProcessPerInstanceSimulationServiceTest {

    private val loaderConfig =  LoaderConfig(
        5, 10, LoaderProfile.LOCAL, true
    )

    private val targetConfig = ApplicationTargetConfig(setOf("https://www.google.com", "apple.com", "microsoft.com"),
        setOf("endpoint1")
    )

    @Test
    fun testSimulateLoad() {
        // Arrange
        val simulationService = ProcessPerInstanceSimulationService(loaderConfig, targetConfig)
        val outputStream = ByteArrayOutputStream()
        val printStream = PrintStream(outputStream, true, StandardCharsets.UTF_8.name())
        System.setOut(printStream)

        // Act
        simulationService.simulateLoad()

        // Assert
        assertEquals(3, simulationService.processMap.size)

        // Cleanup
        simulationService.shutDown()
        System.setOut(System.out)
    }

    @Test
    fun `shutDown should call stop for all child processes`() {
        val processMap = ConcurrentHashMap<Long, Process>()
        for (url in targetConfig.urls) {
            val process = mock(Process::class.java)
            processMap[targetConfig.urls.indexOf(url).toLong()] = process
        }

        val service = spy(ProcessPerInstanceSimulationService(loaderConfig, targetConfig)).apply {
            this.processMap.putAll(processMap)
        }

        service.shutDown()
        service.processMap.values.forEach {
            verify(it).destroy()
        }
    }

}