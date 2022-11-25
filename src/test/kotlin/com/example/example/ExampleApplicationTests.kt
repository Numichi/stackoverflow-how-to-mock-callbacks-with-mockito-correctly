package com.example.example

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType


@SpringBootTest
@ExperimentalCoroutinesApi // for runTest because it is Experimental currently (https://github.com/Kotlin/kotlinx.coroutines)
@WireMockTest(httpPort = 10000) // https://wiremock.org/
class ExampleApplicationTests {

    @Autowired
    lateinit var exampleService: ExampleService

    @Test
    fun `response OK`() {
        runTest {
            val param = "/"

            // Fake remote endpoint
            stubFor(
                post(param)
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                    .willReturn(
                        aResponse()
                            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .withBody("{\"data\":\"$param\"}")
                            .withStatus(200)
                    )
            );

            val onSuccess = mockk<(String) -> Unit>()
            val onError = mockk<(String) -> Unit>()
            justRun { onSuccess(any()) }
            justRun { onError(any()) }

            exampleService.exampleMethod(param, onSuccess, onError)

            verify(exactly = 1) { onSuccess(param) } // it will be run
            verify(exactly = 0) { onError("400") } // it won't be run
        }
    }

    @Test
    fun `response BAD_REQUEST`() {
        runTest {
            val param = "/"

            // Fake remote endpoint
            stubFor(
                post(param)
                    .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
                    .willReturn(
                        aResponse()
                            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                            .withBody("{\"data\":\"$param\"}")
                            .withStatus(400)
                    )
            );

            val onSuccess = mockk<(String) -> Unit>()
            val onError = mockk<(String) -> Unit>()
            justRun { onSuccess(any()) }
            justRun { onError(any()) }

            exampleService.exampleMethod(param, onSuccess, onError)

            verify(exactly = 0) { onSuccess(param) } // it won't be run
            verify(exactly = 1) { onError("400") } // it will be run
        }
    }
}
