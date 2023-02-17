package com.puvn.atomicloader

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AtomicLoaderApplication

fun main(args: Array<String>) {
	runApplication<AtomicLoaderApplication>(*args)
}

