import io.github.oshai.kotlinlogging.KotlinLoggingConfiguration
import io.github.oshai.kotlinlogging.Level

fun main(args: Array<String>) {
    KotlinLoggingConfiguration.formatter = DefaultMessageFormatter(includePrefix = true)
    KotlinLoggingConfiguration.logLevel = Level.DEBUG

    moe.nikky.vrcobs.main(args)
}