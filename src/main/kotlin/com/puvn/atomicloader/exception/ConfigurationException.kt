package com.puvn.atomicloader.exception

class ConfigurationException(exceptionEnum: ExceptionEnum): RuntimeException(exceptionEnum.exceptionString)