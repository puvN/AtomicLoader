package com.puvn.atomicloader.process

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.puvn.atomicloader.config.application_target.ApplicationTargetConfig
import com.puvn.atomicloader.config.loader.LoaderConfig
import com.puvn.atomicloader.logging.Logger
import com.puvn.atomicloader.service.load.impl.RestApiLoadService
import com.puvn.atomicloader.service.simulation.impl.SingleInstanceSimulationService
import com.puvn.atomicloader.util.APPLICATION_TARGET_CONFIG_ARG
import com.puvn.atomicloader.util.LOADER_CONFIG_ARG
import java.lang.management.ManagementFactory

class SingleInstanceSimulationProcess {
    companion object {

        private val mapper = jacksonObjectMapper()

        private val log by Logger()

        @JvmStatic
        fun main(args: Array<String>) {
            val loaderConfigJsonString = args[LOADER_CONFIG_ARG]
            val targetConfigJsonString = args[APPLICATION_TARGET_CONFIG_ARG]
            val loaderConfig: LoaderConfig
            val targetConfig: ApplicationTargetConfig
            try {
                loaderConfig = mapper.readValue(loaderConfigJsonString)
                targetConfig = mapper.readValue(targetConfigJsonString)
            } catch (ex: Exception) {
                log.error(
                    "can't parse config values: $loaderConfigJsonString, $targetConfigJsonString"
                )
                throw ex
            }
            val service = SingleInstanceSimulationService(
                loaderConfig, RestApiLoadService(), targetConfig
            )
            log.info(
                "starting load service in a separate process ${ManagementFactory.getRuntimeMXBean().pid} " +
                        "with configs: LoaderConfig: $loaderConfigJsonString, TargetConfig: $targetConfigJsonString"
            )
            service.simulateLoad()
        }
    }
}
