package com.puvn.atomicloader.runner

import com.puvn.atomicloader.logging.Logger
import com.puvn.atomicloader.service.simulation.SimulationService
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class CommandLineAppStartupRunner(
    private val simulationService: SimulationService
): CommandLineRunner {

    override fun run(vararg args: String?) {
        log.info("command line runner started, starting simulation service...")
        simulationService.simulateLoad()
        log.info("simulation service finished, bye")
    }

    companion object {
        val log by Logger()
    }

}