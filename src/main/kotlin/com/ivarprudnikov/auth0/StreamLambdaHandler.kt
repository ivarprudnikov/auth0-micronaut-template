package com.ivarprudnikov.auth0

import com.amazonaws.serverless.exceptions.ContainerInitializationException
import com.amazonaws.services.lambda.runtime.*
import io.micronaut.function.aws.proxy.MicronautLambdaContainerHandler
import java.io.*

class StreamLambdaHandler: RequestStreamHandler {
    private var handler: MicronautLambdaContainerHandler
    init {
        handler = try {
            MicronautLambdaContainerHandler()
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
