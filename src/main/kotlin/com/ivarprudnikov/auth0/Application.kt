package com.ivarprudnikov.auth0

import io.micronaut.runtime.Micronaut
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object Application {

    private var logger: Logger = LoggerFactory.getLogger(Application.javaClass)

    @JvmStatic
    fun main(args: Array<String>) {
        logger.info("Application.main {}", args.joinToString("; "))
        Micronaut.run(Application.javaClass)
    }
}
