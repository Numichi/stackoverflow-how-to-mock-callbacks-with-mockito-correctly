package com.example.example

import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

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
        val response = client.post()
            .uri(param)
            .bodyValue(Data(param))
            .retrieve()
            .toEntity(Data::class.java)
            .awaitSingle() // These await methods not blocking and similar like block()

        if (response.statusCode.is2xxSuccessful) {
            onSuccess(param)
        } else {
            onError(param)
        }
    }
}