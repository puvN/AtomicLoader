package com.puvn.atomicloader.model
data class ProcessTaskLambda(val x: Int) {
    fun invoke() {
        println("Hello, I'm a lambda with x=$x")
    }

}
