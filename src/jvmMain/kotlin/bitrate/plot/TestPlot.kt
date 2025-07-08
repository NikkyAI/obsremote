package bitrate.plot

import com.github.ajalt.clikt.command.main
import com.github.ajalt.mordant.animation.textAnimation
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.Terminal
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Clock
import moe.nikky.vrcobs.cli.GraphCommand
import java.io.File
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.time.Duration.Companion.milliseconds


suspend fun main(args: Array<String>) {
//    val terminal = Terminal()
//    val a = terminal.textAnimation<Int> { frame ->
//        (1..50).joinToString("") {
//            val hue = (frame + it) * 3 % 360
//            TextColors.hsv(hue, 1, 1)("━")
//        }
//    }
//
//    terminal.cursor.hide(showOnExit = true)
//    repeat(120) {
//        a.update(it)
//        Thread.sleep(25)
//    }
//

    GraphCommand.main(listOf())


//    val data = File("data")
//    data.delete()
//    data.createNewFile()
////    val now = Clock.System.now()
////    repeat(200) { i ->
////        val timestamp = now + (10.seconds * i)
////        val value1 = (sin(i / 50.0) * 1000 + 5000).roundToInt()
////        val value2 = (cos(i / 50.0) * 1000 + 5000).roundToInt()
////        data.appendText("${timestamp.epochSeconds} $value1 $value2\n")
////    }
//
//    val plot = """
//            set yrange [0:7500]
//            set ylabel "Bitrate"
//            set timefmt "%s"
//            set xdata time
//            # set xtics "00:00", 900, "24:00" #guess it will mess up temporarily at 24:00
//            # set mxtics 3 #a small tic every five minute
//            # set format x "%H:%M" #set format x was needed to format the x axis as hh:mm, not hh:mm:ss
//
//            plot "./data" using 1:2 title 'Bitrate' with lines, \
//            "./data" using 1:3 title 'BitrateX2' with lines
//
//            while(1) {
//              pause 1
//              replot
//            }
//        """.trimIndent()
//
//    File("plot.gplot").writeText(
//        plot
//    )
//
////    val process = ProcessBuilder(
////        "gnuplot", "--persist", "plot.gplot"
////    ).inheritIO()
//////            .redirectInput(ProcessBuilder.Redirect)
////        .start()
//
////        process.outputWriter().write
//
//    // TODO: launch plot
//
////        while (true) {
////
////        }
//
////    process.waitFor()
//
////    val writer = process.outputWriter()
//
//    coroutineScope {
//        val dataWriter = launch {
//            var start = 0
//            while (true) {
//                println("appending line")
//                val now = Clock.System.now()
//                val value1 = (sin((start / 50.0).toDouble()) * 1000 + 5000).roundToInt()
//                val value2 = (cos((start / 50.0).toDouble()) * 1000 + 5000).roundToInt()
//                data.appendText("${now.epochSeconds} $value1 $value2\n")
////            data.delete()
//
////            writer.write("replot")
////            writer.newLine()
//
//                start++
//                delay(100.milliseconds)
//            }
//        }
//
////        delay(1000)
////        ProcessBuilder(
////            "gnuplot",
////            /*"--persist",*/
////            "plot.gplot"
////        )
////            .inheritIO()
////            .start()
//    }
}