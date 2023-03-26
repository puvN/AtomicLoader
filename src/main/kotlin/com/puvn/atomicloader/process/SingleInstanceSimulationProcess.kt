package com.puvn.atomicloader.process

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.puvn.atomicloader.dto.ConfigDto
import com.puvn.atomicloader.logging.Logger
import com.puvn.atomicloader.service.load.impl.RestApiLoadService
import com.puvn.atomicloader.service.simulation.impl.SingleInstanceSimulationService
import java.lang.management.ManagementFactory

class SingleInstanceSimulationProcess {
    companion object {

        private val mapper = jacksonObjectMapper()

        private val log by Logger()

        @JvmStatic
        fun main(args: Array<String>) {
            val configDto: ConfigDto
            try {
                configDto = mapper.readValue(args[0])
            } catch (ex: Exception) {
                log.error(
                    "can't parse config values: $args"
                )
                throw ex
            }
            val service = SingleInstanceSimulationService(
                configDto.loaderConfig, RestApiLoadService(), configDto.applicationTargetConfig
            )
            log.info(
                "starting load service in a separate process ${ManagementFactory.getRuntimeMXBean().pid} " +
                        "with configs: ${configDto.loaderConfig}, ${configDto.applicationTargetConfig}"
            )
            service.simulateLoad()
        }
    }
}
