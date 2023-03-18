package com.puvn.atomicloader.service.simulation.impl

import com.puvn.atomicloader.config.application_target.ApplicationTargetConfig
import com.puvn.atomicloader.config.loader.LoaderConfig
import com.puvn.atomicloader.config.loader.LoaderProfile
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
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

    private val targetConfig = ApplicationTargetConfig(setOf("https://www.google.com"),
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
        assertTrue(outputStream.toString(StandardCharsets.UTF_8.name()).isNotBlank())
        assertTrue(simulationService.processMap.isNotEmpty())

        // Cleanup
        simulationService.shutDown()
        System.setOut(System.out)
    }

    @Test
    @Disabled
    //TODO fix executor service mock
    fun `shutDown should stop all child processes and shutdown the executor`() {
        val process = mock(Process::class.java)
        val processMap = ConcurrentHashMap<Long, Process>()
        processMap[1] = process
        val service = spy(ProcessPerInstanceSimulationService(loaderConfig, targetConfig)).apply {
            this.processMap.putAll(processMap)
        }

        val executorServiceMock = spy(service.executorService)

        service.shutDown()

        verify(process).destroy()
        verify(executorServiceMock).shutdown()
    }

}