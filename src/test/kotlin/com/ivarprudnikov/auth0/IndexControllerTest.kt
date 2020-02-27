package com.ivarprudnikov.auth0

import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.kotlintest.specs.StringSpec
import io.micronaut.context.ApplicationContext
import io.micronaut.runtime.server.EmbeddedServer
import io.micronaut.http.client.RxHttpClient
import io.micronaut.test.annotation.MicronautTest
import io.kotlintest.shouldBe

@MicronautTest
class IndexControllerTest(ctx: ApplicationContext): StringSpec({

    "test the server is running" {
        assert(ctx.getBean(EmbeddedServer::class.java).isRunning())
    }

    "IndexController server" {
        var embeddedServer: EmbeddedServer = ctx.getBean(EmbeddedServer::class.java)
        val client: RxHttpClient = embeddedServer.applicationContext.createBean(RxHttpClient::class.java, embeddedServer.url)

        // "a request is made to index"
        val response = client.toBlocking().exchange(HttpRequest.GET<String>("/"), String::class.java)

        // "the response is succesful"
        response.status shouldBe HttpStatus.OK
    }
})
