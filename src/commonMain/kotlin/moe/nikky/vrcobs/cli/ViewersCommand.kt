package moe.nikky.vrcobs.cli

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.mordant.animation.animation
import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import getHttpClient
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import moe.nikky.vrcobs.graph.ViewersResponse
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
object ViewersCommand : BaseCommand(
    "viewers"
) {
    override fun help(context: Context): String =
        "displays VRCDN viewers"

    val streamName: String by argument("streamName", help = "last part of the vrcdn url")

    private val logger = KotlinLogging.logger {}

    val timeFormat = LocalDateTime.Companion.Format {
        hour();
        char(':')
        minute()
        char(':')
        second()
    }

    override suspend fun run() {
        val terminal = Terminal(
            ansiLevel = AnsiLevel.TRUECOLOR
        )

        val animation = terminal.animation<List<Pair<Instant, ViewersResponse>>> { responses ->
            val regions = responses.flatMap { it.second.viewers.map { it.region } }.distinct().sorted()
            val viewersByRegion = responses.map {
                it.first to it.second.viewers.associate { region ->
                    region.region to region.total
                }
            }

//            val viewers = viewersResponmse.viewers
//            val total = viewers.sumOf { it.total }
            table {
                header {
                    row {
                        cell("Time")
                        viewersByRegion.forEach {
                            cell(it.first.toLocalDateTime(TimeZone.Companion.currentSystemDefault()).format(timeFormat))
                        }
                    }
                }
                body {
                    row {
                        cell("Global")
                        viewersByRegion.forEach {
                            cell(it.second.values.sum())
                        }
                    }
                    regions.forEach { region ->
                        row {
                            cell(region)
                            viewersByRegion.forEach {
                                cell(it.second[region]?.toString() ?: "")
                            }
                        }
                    }
                }
            }
        }

        val httpClient = getHttpClient()
        val responseQueue = ArrayDeque<Pair<Instant, ViewersResponse>>(20)

        while (true) {
            try {
                val response = httpClient.get("http://api.vrcdn.live/v1/viewers/$streamName")
                    .body<ViewersResponse>()
                responseQueue.addFirst(Clock.System.now() to response)
                if(responseQueue.size > 5) {
                    responseQueue.removeLast()
                }
                animation.update(responseQueue.takeLast(5))
            } catch (e: Exception) {
                logger.error(e) { "failed to get viewers" }
            }
            delay(6000)
        }
    }


//    val format = LocalDateTime.Format {
//        year()
//        char('-')
//        monthNumber()
//        char('-')
//        dayOfMonth()
//        char(' ')
//        hour();
//        char(':')
//        minute()
//        char(':')
//        second()
//    }

//    fun process(frame: BitrateFrame) {
//        if(raw) {
//            println()
//        }
//        val localDateTime = frame.instant.toLocalDateTime(TimeZone.currentSystemDefault())
//
//        print(
//            listOf(
//                localDateTime.format(BitrateCommand.format),
//                frame.bitrate.toString().reversed().chunked(3).joinToString("_").reversed()
//                    .padStart(12, ' '),
//                "bit/s"
//            ).joinToString(" ")
//        )
//        val scale = 50
//        val underLimit = (frame.bitrate.toDouble() * (scale * 3) / 4 / 6_000_000f)
//            .roundToInt().coerceAtMost((scale * 3) / 4)
//        val overLimit = ((frame.bitrate - 6_000_000f).toDouble() * scale * 0.25 / 6_000_000f)
//            .roundToInt().coerceAtLeast(0)
//        print("    ")
//        print("[")
//        repeat(underLimit) {
//            print("#")
//        }
//        repeat((scale * 3 / 4) - underLimit) {
//            print(" ")
//
//        }
//        if (overLimit > 0.99) {
//            print("x")
//        } else {
//            print("|")
//        }
//        repeat(overLimit) {
//            print("#")
//        }
//        repeat((scale / 4) - overLimit) {
//            print(" ")
//        }
//        print("]")
//        println()
//
//    }
}