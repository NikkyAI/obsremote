package moe.nikky.vrcobs.cli.bitrate

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.help
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.mordant.animation.animation
import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.terminal.Terminal
import com.rejeq.ktobs.request.stream.getStreamStatus
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import moe.nikky.vrcobs.OBSConnectionOptionGroup
import moe.nikky.vrcobs.graph.BitrateFrame
import moe.nikky.vrcobs.graph.GraphWidget
import moe.nikky.vrcobs.obsSession
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.roundToLong
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalEncodingApi::class)
object OBSBitrateCommand : SuspendingCliktCommand(
    "obs"
) {
    override fun help(context: Context): String =
        "displays OBS outgoing bitrate graph"

    val pollFrequency by option("--poll", "-p")
        .int()
        .convert { it.milliseconds }
        .default(5000.milliseconds)
        .help("frequency to poll OBS stats/status")
        .check("must be higher than 100ms") { it > 100.milliseconds }

    val obsConnection by OBSConnectionOptionGroup()

    private val logger = KotlinLogging.logger {}

    @OptIn(ExperimentalTime::class)
    override suspend fun run() {
        val lines = 20
        val terminal = Terminal(
            height = lines + 3,
//            width = 120,
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

        var connectAttempts = 0
        do {
            connectAttempts++
            logger.info { "connecting: attempt ${connectAttempts}" }
            val waitTime = pollFrequency
            try {
                obsSession(obsConnection.properties) {
                    var lastTimestamp = Clock.System.now()
                    var lastBytes = getStreamStatus().bytes
                    while (true) {
                        delay(waitTime)
                        val now = Clock.System.now()
                        val bytes = getStreamStatus().bytes
                        val timeSince = now - lastTimestamp
//                        println(1.seconds / timeSince)
//                        println(bytes - lastBytes)
                        val bitrate = (bytes - lastBytes) * (1.seconds / timeSince) * 8

                        process(
                            BitrateFrame(
                                bitrate = bitrate.roundToLong(),
                                timestamp = now.toEpochMilliseconds()
                            )
                        )
                        lastTimestamp = now
                        lastBytes = bytes
                    }
                }
            } catch (e: Exception) {
                val waitFor = 1000L
//                e.printStackTrace()
                if (connectAttempts <= 3) {
                    logger.warn(e) { "trying to connect again" }
                    delay(waitFor)
                } else {
                    logger.error(e) {"stop trying after too many attempts" }
                }
            }
        } while (connectAttempts <= 3)
    }

    val format = LocalDateTime.Companion.Format {
        year()
        char('-')
        monthNumber()
        char('-')
        day(padding = Padding.ZERO)
        char(' ')
        hour();
        char(':')
        minute()
        char(':')
        second()
    }

//    fun process(frame: BitrateFrame) {
//        val localDateTime = frame.instant.toLocalDateTime(TimeZone.currentSystemDefault())
//
//        print(
//            listOf(
//                localDateTime.format(format),
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