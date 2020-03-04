package com.ivarprudnikov.auth0

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Produces
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import javax.annotation.security.PermitAll

@PermitAll
@Controller("/")
class IndexController {

    var logger: Logger = LoggerFactory.getLogger(IndexController::class.java)

    @Get("/")
    @Produces(MediaType.TEXT_PLAIN)
    fun index(): String {
        logger.debug("status check")
        return "UP"
    }

    @Get("/me")
    @Produces(MediaType.APPLICATION_JSON)
    @Secured(SecurityRule.IS_AUTHENTICATED)
    fun me(authentication: Authentication?): Authentication? {
        logger.debug("authentication check")
        return authentication
    }
}
