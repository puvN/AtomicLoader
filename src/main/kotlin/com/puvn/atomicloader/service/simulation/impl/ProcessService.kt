package com.puvn.atomicloader.service.simulation.impl

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.puvn.atomicloader.model.ProcessTaskLambda

class ProcessService {
    private val mapper = jacksonObjectMapper()
    fun main(args: Array<String>) {
        val lambdaString = args.firstOrNull { it.startsWith("--task=") }?.substring(7)
        val lambda: ProcessTaskLambda = mapper.readValue(lambdaString ?: "")
        lambda.invoke()
    }
}