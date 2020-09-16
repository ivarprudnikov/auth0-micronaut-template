package com.ivarprudnikov.auth0

import io.micronaut.http.*
import io.micronaut.http.client.RxHttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.security.authentication.DefaultAuthentication
import io.micronaut.security.token.validator.TokenValidator
import io.micronaut.test.annotation.MicronautTest
import io.micronaut.test.annotation.MockBean
import io.reactivex.Flowable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.*
import javax.inject.Inject

@MicronautTest
class IndexControllerTest {

    @field:Inject
    @field:Client("/")
    lateinit var client: RxHttpClient

    @field:Inject
    lateinit var tokenValidator: TokenValidator

    @MockBean(TokenValidator::class)
    fun tokenValidator(): TokenValidator {
        return mock(TokenValidator::class.java)
    }

    @Test
    fun root_path_responds_with_text() {
        val response: HttpResponse<String> = client.toBlocking().exchange(HttpRequest.GET<String>("/"), String::class.java)
        assertEquals(response.status, HttpStatus.OK)
        assertEquals(response.body(), "UP")
    }

    @Test
    fun me_path_responds_with_401() {
        val e = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(HttpRequest.GET<String>("/me"), String::class.java)
        }

        assertEquals(e.status, HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun me_path_authenticates_and_responds_with_200() {

        `when`(tokenValidator.validateToken(eq("token"), any())).thenReturn(Flowable.just(
                DefaultAuthentication("user", emptyMap())
        ))

        val response: HttpResponse<String> = assertDoesNotThrow { client.toBlocking().exchange(HttpRequest.GET<String>("/me").bearerAuth("token"), String::class.java) }

        assertEquals(response.body(), """{"name":"user"}""")

        verify(tokenValidator, times(1)).validateToken(anyString(), any())
    }

    @Test
    fun me_path_OPTIONS_request_responds() {
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
        assertEquals(response.status, HttpStatus.OK)
        assertEquals(response.body(), null)
    }
}
