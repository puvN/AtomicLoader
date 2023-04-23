package com.puvn.atomicloader.runner.docker

import com.puvn.atomicloader.config.application_target.ApplicationTargetConfig
import com.puvn.atomicloader.logging.Logger
import com.puvn.atomicloader.runner.command.CommandResolver
import org.springframework.stereotype.Component

@Component
class DockerRunner(
    private val commandResolver: CommandResolver,
    private val applicationTargetConfig: ApplicationTargetConfig
) {

    fun startTargetContainer() {
        log.info("starting docker container")
        val command = commandResolver.getSystemCommand()
        val process = Runtime.getRuntime().exec(command)
        process.waitFor()
        log.info("waiting for application to start")
        Thread.sleep(applicationTargetConfig.waitForStartSeconds.toLong() * 1000)
    }

    companion object {
        val log by Logger()
    }

}