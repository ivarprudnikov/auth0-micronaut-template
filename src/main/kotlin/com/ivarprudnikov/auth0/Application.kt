package com.ivarprudnikov.auth0

import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(args: Array<String>) {
        Micronaut.build()
                .packages("com.ivarprudnikov.auth0")
                .mainClass(Application.javaClass)
                .start()
    }
}
