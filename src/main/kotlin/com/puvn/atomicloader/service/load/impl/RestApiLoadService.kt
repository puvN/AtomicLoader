package com.puvn.atomicloader.service.load.impl

import com.puvn.atomicloader.logging.Logger
import com.puvn.atomicloader.service.load.LoadService
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
@Primary
class RestApiLoadService : LoadService {

    val client: HttpClient = HttpClient.newBuilder().build()
    var cachedTargetValue: String? = null
    var request: HttpRequest? = null

    override fun makeRequest(target: String): String {
        if (cachedTargetValue == null || cachedTargetValue != target) {
            request = HttpRequest.newBuilder()
                .uri(URI.create(target))
                .build()
        }
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }

    override fun handleResponse(response: String): String {
        log.info("got response: $response")
        return ""
    }

    companion object {
        val log by Logger()
    }

}