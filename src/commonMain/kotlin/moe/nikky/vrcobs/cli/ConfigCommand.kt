package moe.nikky.vrcobs.cli

import FILESYSTEM
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.mordant.animation.animation
import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.terminal.Terminal
import dotenv.getEnv
import exec
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import moe.nikky.vrcobs.graph.BitrateFrame
import moe.nikky.vrcobs.graph.GraphWidget
import okio.Path.Companion.toPath
import kotlin.math.sin
import kotlin.time.Duration.Companion.milliseconds

object ConfigCommand : BaseCommand(
    "config"
) {
    private val logger = KotlinLogging.logger {}
    override fun help(context: Context): String {
        return """
            opens the .env file
        """.trimIndent()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun run(): Unit = coroutineScope {
        val homedir = getEnv("HOME")?.toPath() ?: getEnv("USERPROFILE")?.toPath()
        ?: error("cannot find home directory from envionment")
        val dotenvFile = homedir / ".config/obsremote/.env".toPath(normalize = true)
        if (!FILESYSTEM.exists(dotenvFile)) {
            FILESYSTEM.createDirectories(homedir / ".config/obsremote/".toPath(normalize = true))
            logger.info { "creating $dotenvFile" }
            FILESYSTEM.write(dotenvFile) {
                writeUtf8(
                    """
                        # dotenv
                        OBS_HOST=127.0.0.1
                        OBS_PORT=4455
                        OBS_PASSWORD=
                    """.trimIndent()
                )
            }
        }
        logger.info { "opening $dotenvFile" }
        //TODO: check if we are on unix and use xdg-open
        try {
            exec("explorer.exe", dotenvFile.toString())
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            println("failed to open file")
        }
    }
}
