package com.puvn.atomicloader.model

import com.puvn.atomicloader.config.application_target.ApplicationTargetConfig
import com.puvn.atomicloader.config.loader.LoaderConfig
import com.puvn.atomicloader.config.loader.LoaderProfile
import com.puvn.atomicloader.service.load.impl.RestApiLoadService
import com.puvn.atomicloader.service.simulation.impl.SingleInstanceSimulationService

class SingleInstanceSimulationProcess {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val service = SingleInstanceSimulationService(
                LoaderConfig(30, 100, LoaderProfile.LOCAL, false),
                loadService = RestApiLoadService(),
                targetConfig = ApplicationTargetConfig(
                    setOf(), setOf()
                )
            )
            println("starting load")
            service.simulateLoad()
        }
    }
}
