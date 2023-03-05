package com.puvn.atomicloader.service.simulation.impl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.puvn.atomicloader.config.application_target.ApplicationTargetConfig
import com.puvn.atomicloader.config.loader.LoaderConfig
import com.puvn.atomicloader.logging.Logger
import com.puvn.atomicloader.model.ProcessTaskLambda
import com.puvn.atomicloader.service.load.LoadService
import com.puvn.atomicloader.service.simulation.SimulationService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
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
        val currentPid = ManagementFactory.getRuntimeMXBean().pid;
        log.info("parent pid is $currentPid, I am preparing child processes")
        //TODO start processes together, not each after each
        for (url in targetConfig.urls) {
            val process = prepareProcess(ProcessTaskLambda(42) ).start()
            log.info("process with pid ${process.pid()} has started")
        }

        /*val javaHome = System.getProperty("java.home")
        val javaBin = "$javaHome/bin/java"
        val classpath = System.getProperty("java.class.path")
        val className = "MyNewJvmClass"

        val builder = ProcessBuilder(
            javaBin, "-cp", classpath, className)

        try {
            val process = builder.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }*/

        /*// Serialize the lambda expression and write it to the process's input stream
        val outputStream = process.outputStream
        val writer = BufferedWriter(OutputStreamWriter(outputStream))
        writer.write(serializeLambda(lambda))
        writer.flush()

// In YourMainClass, read the serialized lambda expression from the input stream
        val inputStream = System.`in`
        val reader = BufferedReader(InputStreamReader(inputStream))
        val serializedLambda = reader.readLine()
        val lambda = deserializeLambda(serializedLambda)

// Use the lambda expression
        lambda.invoke()*/
    }

    private fun prepareProcess(task: ProcessTaskLambda): ProcessBuilder {
        val javaHome = System.getProperty("java.home")
        val javaBin = "$javaHome/bin/java"
        val classpath = System.getProperty("java.class.path")
        val className = "ProcessService"

        val taskString = mapper.writeValueAsString(task)
        return ProcessBuilder(
            javaBin, "-cp", classpath, className, "--task=$taskString"
        )
    }

    companion object {
        val log by Logger()
    }

}