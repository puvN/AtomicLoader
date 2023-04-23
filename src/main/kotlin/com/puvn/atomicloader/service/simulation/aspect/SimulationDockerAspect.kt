package com.puvn.atomicloader.service.simulation.aspect

import com.puvn.atomicloader.config.loader.LoaderConfig
import com.puvn.atomicloader.config.loader.LoaderProfile
import com.puvn.atomicloader.logging.Logger
import com.puvn.atomicloader.runner.docker.DockerRunner
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

@Aspect
@Component
class SimulationDockerAspect(
    private val loaderConfig: LoaderConfig,
    private val dockerRunner: DockerRunner
) {

    @Around("execution(* com.puvn.atomicloader.service.simulation.SimulationService.simulateLoad())")
    fun aroundSimulateLoad(joinPoint: ProceedingJoinPoint) {
        if (loaderConfig.profile == LoaderProfile.LOCAL) {
            log.info("profile is local, will start docker container")
            dockerRunner.startTargetContainer()
            joinPoint.proceed()
        }
    }

    companion object {
        val log by Logger()
    }

}