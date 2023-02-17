package com.puvn.atomicloader.exception

enum class ExceptionEnum(val exceptionString: String) {
    NO_URLS_OR_ENDPOINTS("url list or endpoints list is empty"),
    INVALID_VALUE("specified parameter has invalid value"),
    INVALID_URLS_IN_LOCAL_PROFILE("profile set local, but there is non-local url")
}