package com.ivarprudnikov.auth0

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.security.authentication.DefaultAuthentication
import io.micronaut.security.token.validator.TokenValidator
import io.micronaut.test.annotation.MicronautTest
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.kotlintest.MicronautKotlinTestExtension.getMock
import io.mockk.every
import io.mockk.mockk
import org.reactivestreams.Publisher

@MicronautTest
class IndexControllerTest(@Client("/") client: RxHttpClient, tokenValidator: TokenValidator) : StringSpec({

    "root path responds with text" {
        val response: HttpResponse<String> = client.toBlocking().exchange(HttpRequest.GET<String>("/"), String::class.java)
        response.status shouldBe HttpStatus.OK
        response.body() shouldBe "UP"
    }

    "me path responds with 401" {
        val e = shouldThrow<HttpClientResponseException> {
            client.toBlocking().exchange(HttpRequest.GET<String>("/me"), String::class.java)
        }
        e.status shouldBe HttpStatus.UNAUTHORIZED
    }

    "me path authenticates and responds with 200" {

        val mock = getMock(tokenValidator)
        every { mock.validateToken(any()) } answers {
            Publisher { s -> s.onNext(DefaultAuthentication("user", emptyMap())) }
        }

        val response: HttpResponse<String> = client.toBlocking().exchange(HttpRequest.GET<String>("/me").bearerAuth("foobar"), String::class.java)
        response.status shouldBe HttpStatus.OK
        response.body() shouldBe """{"name":"user"}"""
    }
}) {
    @MockBean(TokenValidator::class)
    fun tokenValidator(): TokenValidator {
        return mockk()
    }
}
