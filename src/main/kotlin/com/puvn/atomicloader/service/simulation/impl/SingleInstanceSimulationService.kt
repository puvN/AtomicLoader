package com.puvn.atomicloader.service.simulation.impl

import com.puvn.atomicloader.config.application_target.ApplicationTargetConfig
import com.puvn.atomicloader.config.loader.LoaderConfig
import com.puvn.atomicloader.logging.Logger
import com.puvn.atomicloader.service.load.LoadService
import com.puvn.atomicloader.service.simulation.SimulationService
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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
        val totalRequestsForEndpoint = loaderConfig.requestsPerSecond * loaderConfig.testingPeriodSeconds
        runBlocking {
            for (url in targetConfig.urls) {
                log.info("sending requests to $url")
                for (endpoint in targetConfig.endpoints) {
                    log.info("endpoint is $endpoint")
                    val target = url + endpoint
                    val startTime = LocalDateTime.now()
                    val requests = List(totalRequestsForEndpoint) {
                        async {
                            val response = makeRequest(target)
                            handleResponse(response)
                        }
                    }
                    requests.awaitAll()
                    val endTime = LocalDateTime.now()
                    log.info("duration: ${Duration.between(startTime, endTime).seconds}")
                }
            }
        }
    }

    private suspend fun makeRequest(target: String): String {
        return loadService.makeRequest(target)
    }

    private fun handleResponse(response: String) {
        loadService.handleResponse(response)
    }

    companion object {
        val log by Logger()
    }

}