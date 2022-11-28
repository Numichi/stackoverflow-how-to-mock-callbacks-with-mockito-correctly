package com.example.example

import com.ninjasquad.springmockk.MockkBean
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@ExperimentalCoroutinesApi
class MethodMockExampleTest {

    @MockkBean // testImplementation("com.ninja-squad:springmockk:3.1.2")
    lateinit var exampleService: ExampleService

    @BeforeEach
    fun beforeEach() {
        // Clear and reset all mocks in this object every time before all test
        MockKAnnotations.init(this)
    }

    @Test
    fun test1() {
        runTest {
            val anonymousFn = fun(str: String) = str
            coEvery { exampleService.exampleMethod(any(), any(), any()) } returns anonymousFn("success") // anonymousFn("success") => "success" will result value

            val result = exampleService.exampleMethod("asdasd", anonymousFn, anonymousFn)

            assertEquals("success", result)
        }
    }

    @Test
    fun test2() {
        runTest {
            val anonymousFn = fun(str: String) = str
            coEvery { exampleService.exampleMethod(any(), any(), any()) } returns "success" // exampleMethod return value is "success" like above

            val result = exampleService.exampleMethod("asdasd", anonymousFn, anonymousFn)

            assertEquals("success", result)
        }
    }

}