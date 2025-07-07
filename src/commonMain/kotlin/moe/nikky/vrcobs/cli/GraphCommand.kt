package moe.nikky.vrcobs.cli

import com.github.ajalt.mordant.animation.animation
import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.terminal.Terminal
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import moe.nikky.vrcobs.graph.BitrateFrame
import moe.nikky.vrcobs.graph.GraphWidget
import kotlin.math.sin
import kotlin.time.Duration.Companion.milliseconds

object GraphCommand: BaseCommand(
    "testgraph"
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun run() = coroutineScope {
//        val fs = FILESYSTEM

//        val sink = fs.sink("./data.txt".toPath())

        val dataChannel = Channel<BitrateFrame>()

        val job = launch {
            var bitrate = 5_000_000L
            var tick = 0
            while (true) {
                val value = 5_500_000 +
                        (2_000_000 * sin(tick * 0.1) * sin(tick * 0.3) * sin(tick * 0.05))
//                        (sin(tick * 0.25) * 500_000 )
//                bitrate = (bitrate + Random.nextLong(-100_000, 100_000)).coerceIn(0, 7_500_000)
                dataChannel.send(
                    BitrateFrame(
                        timestamp = Clock.System.now().toEpochMilliseconds(),
                        bitrate = value.toLong()
                    )
                )
                tick++
                delay(100.milliseconds)
            }
        }
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
        val frameQueue = ArrayDeque<BitrateFrame>(120)
        dataChannel
            .consumeAsFlow()
            .collect { frame ->
//                terminal.println("received $frame")
                frameQueue.addLast(frame)
                if(frameQueue.size > 120) {
                    frameQueue.removeFirst()
                }
                animation.update(frameQueue.takeLast(120))
            }
//        while(true) {
//            a.update()
//            delay(25)
//        }
    }
}