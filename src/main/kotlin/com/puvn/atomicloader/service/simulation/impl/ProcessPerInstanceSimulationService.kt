package com.puvn.atomicloader.service.simulation.impl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.puvn.atomicloader.config.application_target.ApplicationTargetConfig
import com.puvn.atomicloader.config.loader.LoaderConfig
import com.puvn.atomicloader.logging.Logger
import com.puvn.atomicloader.process.SingleInstanceSimulationProcess
import com.puvn.atomicloader.service.simulation.SimulationService
import jakarta.annotation.PreDestroy
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.lang.management.ManagementFactory
import java.util.concurrent.Callable
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Service
@ConditionalOnProperty(value = ["loader-config.process-per-instance"], havingValue = "true")
class ProcessPerInstanceSimulationService(
    private val loaderConfig: LoaderConfig,
    private val targetConfig: ApplicationTargetConfig,
) : SimulationService {

    private val mapper = jacksonObjectMapper()

    private val executorService: ExecutorService = Executors.newCachedThreadPool()

    internal val processMap: ConcurrentMap<Long, Process> = ConcurrentHashMap()

    override fun simulateLoad() {
        log.info("parent pid is ${ManagementFactory.getRuntimeMXBean().pid}, I am preparing child processes")
        val tasks = mutableListOf<Callable<Process>>()
        for (url in targetConfig.urls) {
            val builder = createProcessBuilder(
                SingleInstanceSimulationProcess::class.java, listOf(
                    getJsonStringRepresentation(loaderConfig),
                    getJsonStringRepresentation(targetConfig)
                ), listOf("-Xmx200m")
            )
            tasks.add {
                val process = builder.inheritIO().start()
                this.processMap[process.pid()] = process
                process.waitFor()
                process
            }
        }
        executorService.invokeAll(tasks)
    }

    @Throws(IOException::class, InterruptedException::class)
    private fun createProcessBuilder(
        clazz: Class<*>,
        args: List<String> = emptyList(),
        jvmArgs: List<String> = emptyList()
    ): ProcessBuilder {
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

        return ProcessBuilder(command)
    }

    @PreDestroy
    internal fun shutDown() {
        log.info("killing child processes")
        this.processMap.values.forEach { it.destroy() }
        log.info("killing executor service")
        this.executorService.shutdown()
        try {
            if (!this.executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                this.executorService.shutdownNow()
            }
        } catch (ex: InterruptedException) {
            this.executorService.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }

    //https://stackoverflow.com/questions/45260447/adding-double-quotes-symbol-in-processbuilder
    private fun getJsonStringRepresentation(value: Any) =
        mapper.writeValueAsString(value).replace("\"", "\\\"")

    companion object {
        val log by Logger()
    }

}