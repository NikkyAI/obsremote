package moe.nikky.vrcobs.cli

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.mordant.animation.animation
import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.terminal.Terminal
import dotenv.getEnv
import exec
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

object ConfigCommand: BaseCommand(
    "config"
) {
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
        println("opening $dotenvFile")
        //TODO: check if we are on unix and use xdg-open
        try {
            exec("explorer.exe", dotenvFile.toString())
        }catch (e: IllegalStateException){
            e.printStackTrace()
            println("failed to open file")
        }
        }
}