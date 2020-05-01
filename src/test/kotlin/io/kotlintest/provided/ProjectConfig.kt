package io.kotlintest.provided

import io.kotlintest.AbstractProjectConfig
import io.micronaut.test.extensions.kotlintest.MicronautKotlinTestExtension

/**
 * Before you can get started writing tests with KotlinTest,
 * it is necessary to inform KotlinTest of the Micronaut extensions.
 * The way to do that is by providing a ProjectConfig object
 * in a specific package.
 * https://github.com/micronaut-projects/micronaut-test/blob/master/src/main/docs/guide/kotlintest.adoc#before-you-begin
 */
object ProjectConfig : AbstractProjectConfig() {
    override fun listeners() = listOf(MicronautKotlinTestExtension)
    override fun extensions() = listOf(MicronautKotlinTestExtension)
}
