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
import io.mockk.verify
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
        every { mock.validateToken("foobar") } answers {
            Publisher { s -> s.onNext(DefaultAuthentication("user", emptyMap())) }
        }

        val response: HttpResponse<String> = client.toBlocking().exchange(HttpRequest.GET<String>("/me").bearerAuth("foobar"), String::class.java)
        response.status shouldBe HttpStatus.OK
        response.body() shouldBe """{"name":"user"}"""

        verify { mock.validateToken("foobar") }
    }

    "me path OPTIONS request responds" {
        val response: HttpResponse<String> = client.toBlocking().exchange(
                HttpRequest.OPTIONS<String>("/me").headers(mutableMapOf<CharSequence, CharSequence>(
                        "Host" to "te60oj36jd.execute-api.eu-west-1.amazonaws.com",
                        "User-Agent" to "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:75.0) Gecko/20100101 Firefox/75.0",
                        "Accept" to "*/*",
                        "Accept-Language" to "en-US,en;q=0.5",
                        "Accept-Encoding" to "gzip, deflate, br",
                        "Access-Control-Request-Method" to "GET",
                        "Access-Control-Request-Headers" to "authorization",
                        "Referer" to "https://ivarprudnikov.github.io/react-auth0-template/profile",
                        "Origin" to "https://ivarprudnikov.github.io",
                        "Connection" to "keep-alive",
                        "Pragma" to "no-cache",
                        "Cache-Control" to "no-cache",
                        "TE" to "Trailers"
                )), String::class.java)
        response.status shouldBe HttpStatus.OK
        response.body() shouldBe null
    }
}) {
    @MockBean(TokenValidator::class)
    fun tokenValidator(): TokenValidator {
        return mockk()
    }
}
