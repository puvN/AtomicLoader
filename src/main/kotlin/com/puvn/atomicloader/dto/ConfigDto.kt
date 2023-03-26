package com.puvn.atomicloader.dto

import com.puvn.atomicloader.config.application_target.ApplicationTargetConfig
import com.puvn.atomicloader.config.loader.LoaderConfig

data class ConfigDto(val loaderConfig: LoaderConfig, val applicationTargetConfig: ApplicationTargetConfig)
