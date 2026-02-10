package moe.nikky.vrcobs.cli.bitrate

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.help
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.mordant.animation.animation
import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.terminal.prompt
import com.rejeq.ktobs.request.config.getStreamServiceSettings
import com.rejeq.ktobs.request.stream.getStreamStatus
import getHttpClient
import io.github.oshai.kotlinlogging.KotlinLogging
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.utils.io.core.toByteArray
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import moe.nikky.vrcobs.OBSConnectionOptionGroup
import moe.nikky.vrcobs.cli.BaseCommand
import moe.nikky.vrcobs.graph.BitrateFrame
import moe.nikky.vrcobs.graph.GraphWidget
import moe.nikky.vrcobs.obsSession
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalEncodingApi::class)
object VRCDNBitrateCommand : BaseCommand(
    "vrcdn"
) {
    override fun help(context: Context): String =
        "displays VRCDN bitrate graph via wss://ws.vrcdn.live/bitrate"

    val streamKey by argument("key")
//        .required()
        .help("optional: will use current streamkey from OBS by default")
//    val ignoreStreamStatus by option("--ignore-obs-status", "-f")
//        .flag(default = false)
//        .help("will warn and stop if you are not currently streaming")

//    val obsConnectionGroup by OBSConnectionOptionGroup()
    private val logger = KotlinLogging.logger {}

    override suspend fun run() {

        val lines = 20
        val terminal = Terminal(
            height = lines + 3,
            width = 150,
            ansiLevel = AnsiLevel.TRUECOLOR
        )

        val animation = terminal.animation<List<BitrateFrame>> { frames ->
            GraphWidget(
                frames = frames,
                lines = lines,
            )
        }

        terminal.cursor.hide(showOnExit = true)
        val frameQueue = ArrayDeque<BitrateFrame>(240)

        fun process(bitrateFrame: BitrateFrame) {
            frameQueue.addLast(bitrateFrame)
            if(frameQueue.size > 240) {
                frameQueue.removeFirst()
            }
            animation.update(frameQueue.takeLast(240))
        }

        val httpClient = getHttpClient()

        var key: String = streamKey
        var connectAttempts = 0
        do {
            connectAttempts++
            logger.info { "connecting: attempt ${connectAttempts}" }
            try {
                httpClient.webSocket("wss://ws.vrcdn.live/bitrate") {
                    run {
                        val streamKey = key // ?: error("need to provide vrcdn stream key")
                        val xorKey = 42 // The meaning of life
                        val xoredString = streamKey.map { (it.code xor xorKey).toChar() }.joinToString("")
                        val newKey = Base64.Default.encode(xoredString.toByteArray())
                        logger.info { "sending: {\"subscribe\": \"$newKey\"}" }
                        send("""{"subscribe": "$newKey"}""")
                    }

                    suspend fun receiveBitrateFrame(): BitrateFrame {
                        val frame = incoming.receive() as Frame.Text
                        return Json.Default.decodeFromString(
                            BitrateFrame.serializer(),
                            frame.readText()
                        )
                    }

                    logger.info { "waiting for first websocket frame, make sure the stream is live" }
                    try {
                        val bitrateFrame = withTimeout(5.seconds) {
                            receiveBitrateFrame()
                        }
//                        print("\n\n\n")
                        process(bitrateFrame)
//                        frameQueue.addLast(bitrateFrame)
//                        if(frameQueue.size > 100) {
//                            frameQueue.removeFirst()
//                        }
//                        animation.update(frameQueue.takeLast(100))
                        connectAttempts = 0
                    } catch (e: TimeoutCancellationException) {
                        logger.warn { "are you sure you are streaming to this key ? no response in time" }
                        return@webSocket
                    }

                    while (true) {
                        val bitrateFrame = try {
                            withTimeout(15.seconds) {
                                receiveBitrateFrame()
                            }
                        } catch (e: TimeoutCancellationException) {
                            throw IllegalStateException("websocket stopped sending data")
                        }
                        process(bitrateFrame)
//                        frameQueue.addLast(bitrateFrame)
//                        if(frameQueue.size > 100) {
//                            frameQueue.removeFirst()
//                        }
//                        animation.update(frameQueue.takeLast(100))
                    }
                }
            } catch (e: Exception) {
                val waitFor = 1000L
//                e.printStackTrace()
                if (connectAttempts <= 3) {
                    logger.warn(e) { "trying to connect again" }
                    delay(waitFor)
                } else {
                    logger.error(e) { "stop trying after too many attempts" }
                }
            }
        } while (connectAttempts <= 3)
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