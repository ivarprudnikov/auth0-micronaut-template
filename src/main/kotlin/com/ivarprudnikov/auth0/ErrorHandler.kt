package com.ivarprudnikov.auth0

import io.micronaut.context.annotation.Requires
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.inject.Singleton

@Produces
@Singleton
@Requires(classes = [RuntimeException::class, ExceptionHandler::class])
class ErrorHandler : ExceptionHandler<RuntimeException, HttpResponse<Any?>> {
    var logger: Logger = LoggerFactory.getLogger(ErrorHandler::class.java)
    override fun handle(request: HttpRequest<Any?>, exception: RuntimeException): HttpResponse<Any?> {
        logger.error(exception.message)
        return HttpResponse.ok(0)
    }
}
