package com.ivarprudnikov.auth0

import com.amazonaws.serverless.proxy.internal.testutils.AwsProxyRequestBuilder
import com.amazonaws.serverless.proxy.internal.testutils.MockLambdaContext
import io.micronaut.context.ApplicationContext
import io.micronaut.function.aws.proxy.MicronautLambdaContainerHandler
import io.micronaut.http.HttpStatus
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@MicronautTest
class AWSProxyTest {

    @Test
    fun proxy_OPTIONS_request_to_root_returns_ok() {
        val handler = MicronautLambdaContainerHandler(
                ApplicationContext.builder()
                        .deduceEnvironment(true)
        )
        val lambdaContext = MockLambdaContext()
        val req = AwsProxyRequestBuilder("/", "OPTIONS")
                .header("Host", "te60oj36jd.execute-api.eu-west-1.amazonaws.com")
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:75.0) Gecko/20100101 Firefox/75.0")
                .header("Accept", "*/*")
                .header("Accept-Language", "en-US,en;q=0.5")
                .header("Accept-Encoding", "gzip, deflate, br")
                .header("Access-Control-Request-Method", "GET")
                .header("Referer", "https://ivarprudnikov.github.io/react-auth0-template/profile")
                .header("Origin", "https://ivarprudnikov.github.io")
                .header("Connection", "keep-alive")
                .header("Pragma", "no-cache")
                .header("Cache-Control", "no-cache")
                .header("TE", "Trailers")
                .build()
        val resp = handler.proxy(req, lambdaContext)
        assertEquals(resp.statusCode, HttpStatus.OK.code)
    }
}
