package com.ivarprudnikov.auth0

import com.amazonaws.serverless.exceptions.ContainerInitializationException
import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.RequestStreamHandler
import io.micronaut.function.aws.proxy.MicronautLambdaContainerHandler
import io.micronaut.security.filters.SecurityFilter
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

@Suppress("unused")
class StreamLambdaHandler : RequestStreamHandler {
    private var handler: MicronautLambdaContainerHandler

    init {
        try {
            handler = MicronautLambdaContainerHandler()
            // Slow coldstart on AWS Lambda when Security is enabled
            // https://github.com/micronaut-projects/micronaut-aws/issues/205
            handler.applicationContext.getBean(SecurityFilter::class.java)
        } catch (e: ContainerInitializationException) { // if we fail here. We re-throw the exception to force another cold start
            e.printStackTrace()
            throw RuntimeException("Could not initialize Micronaut", e)
        }
    }

    @Throws(IOException::class)
    override fun handleRequest(inputStream: InputStream?, outputStream: OutputStream?, context: Context?) {
        handler.proxyStream(inputStream, outputStream, context)
    }
}
