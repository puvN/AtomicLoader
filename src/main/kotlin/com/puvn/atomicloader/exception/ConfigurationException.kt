package com.puvn.atomicloader.exception

class ConfigurationException(
    parameterName: String = "",
    exceptionEnum: ExceptionEnum
) : RuntimeException(buildExceptionMessage(exceptionEnum.exceptionString, parameterName))

fun buildExceptionMessage(baseMessage: String, parameterName: String): String {
    return if (parameterName.isNotEmpty()) {
        "$baseMessage (parameter: $parameterName)"
    } else {
        baseMessage
    }
}