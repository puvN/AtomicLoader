package com.puvn.atomicloader.service.simulation.impl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.puvn.atomicloader.config.application_target.ApplicationTargetConfig
import com.puvn.atomicloader.config.loader.LoaderConfig
import com.puvn.atomicloader.logging.Logger
import com.puvn.atomicloader.model.SingleInstanceSimulationProcess
import com.puvn.atomicloader.service.load.LoadService
import com.puvn.atomicloader.service.simulation.SimulationService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.lang.management.ManagementFactory

@Service
@ConditionalOnProperty(value = ["loader-config.process-per-instance"], havingValue = "true")
class ProcessPerInstanceSimulationService(
    private val loaderConfig: LoaderConfig,
    private val loadService: LoadService,
    private val targetConfig: ApplicationTargetConfig,
) : SimulationService {

    private val mapper = jacksonObjectMapper()
    override fun simulateLoad() {
        val currentPid = ManagementFactory.getRuntimeMXBean().pid
        log.info("parent pid is $currentPid, I am preparing child processes")
        //TODO start processes together, not each after each
        for (url in targetConfig.urls) {
            exec(SingleInstanceSimulationProcess::class.java, listOf(), listOf("-Xmx200m"))
            log.info("process for url $url has started")
        }
    }

    @Throws(IOException::class, InterruptedException::class)
    fun exec(clazz: Class<*>, args: List<String> = emptyList(), jvmArgs: List<String> = emptyList()): Int {
        val javaHome = System.getProperty("java.home")
        val javaBin = javaHome + File.separator + "bin" + File.separator + "java"
        val classpath = System.getProperty("java.class.path")
        val className = clazz.name

        val command = ArrayList<String>()
        command.add(javaBin)
        command.addAll(jvmArgs)
        command.add("-cp")
        command.add(classpath)
        command.add(className)
        command.addAll(args)

        val builder = ProcessBuilder(command)
        val process = builder.inheritIO().start()
        process.waitFor()
        return process.exitValue()
    }

    companion object {
        val log by Logger()
    }

}