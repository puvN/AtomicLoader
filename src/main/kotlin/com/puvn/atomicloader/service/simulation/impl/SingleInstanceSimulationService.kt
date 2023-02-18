package com.puvn.atomicloader.service.simulation.impl

import com.puvn.atomicloader.config.application_target.ApplicationTargetConfig
import com.puvn.atomicloader.config.loader.LoaderConfig
import com.puvn.atomicloader.logging.Logger
import com.puvn.atomicloader.service.load.LoadService
import com.puvn.atomicloader.service.simulation.SimulationService
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime

/**
 * Simulates specified number of requests in a specific period of time consequently for each endpoint for each host
 */
@Service
@ConditionalOnProperty(value = ["loader-config.process-per-instance"], havingValue = "false")
class SingleInstanceSimulationService(
    private val loaderConfig: LoaderConfig,
    private val loadService: LoadService,
    private val targetConfig: ApplicationTargetConfig
) : SimulationService {

    override fun simulateLoad() {
        runBlocking {
            for (url in targetConfig.urls) {
                log.info("sending requests to $url")
                for (endpoint in targetConfig.endpoints) {
                    log.info("endpoint is $endpoint")
                    val target = url + endpoint
                    val startTime = LocalDateTime.now()
                    sendRequestsPerSecond(target, loaderConfig.requestsPerSecond, loaderConfig.testingPeriodSeconds)
                    val endTime = LocalDateTime.now()
                    log.info("duration: ${Duration.between(startTime, endTime).seconds} seconds" )
                }
            }
        }
    }

    private suspend fun sendRequestsPerSecond(target: String, requestsPerSecond: Int, durationSeconds: Int) {
        val delayMillis = 1000 / requestsPerSecond.toLong() // calculate delay in milliseconds
        repeat(requestsPerSecond * durationSeconds) {
            val response = makeRequest(target)
            handleResponse(response) //TODO handle response in asynchronous manner
            delay(delayMillis)
        }
    }

    private suspend fun makeRequest(target: String): String {
        return loadService.makeRequest(target)
    }

    private suspend fun handleResponse(response: String) {
        loadService.handleResponse(response)
    }

    companion object {
        val log by Logger()
    }

}