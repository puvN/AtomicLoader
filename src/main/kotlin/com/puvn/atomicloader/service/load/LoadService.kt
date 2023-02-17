package com.puvn.atomicloader.service.load

interface LoadService {
    fun makeRequest(target: String): String
    fun handleResponse(response: String): String
}