package com.example.example

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

@SpringBootApplication
class ExampleApplication

fun main(args: Array<String>) {
    runApplication<ExampleApplication>(*args)
}

data class Data(var data: String)

@Service
class ExampleService {

    val client = WebClient.builder()
        .baseUrl("http://localhost:10000") // WireMock will catching this calling in the test
        .build()

    suspend fun exampleMethod(param: String, onSuccess: (str: String) -> Unit, onError: (str: String) -> Unit) {
        val response = runCatching {
            client.post()
                .uri(param)
                .bodyValue(Data(param))
                .retrieve()
                .bodyToMono(Data::class.java)
                .awaitSingle() // These await methods not blocking and similar like block()
        }

        if (response.isSuccess) {
            onSuccess(param)
        } else {
            val exception = response.exceptionOrNull() as WebClientResponseException?
            onError(exception?.rawStatusCode.toString())
        }
    }
}