package moe.nikky.vrcobs.graph

import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.rendering.Line
import com.github.ajalt.mordant.rendering.Lines
import com.github.ajalt.mordant.rendering.Span
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.Widget
import com.github.ajalt.mordant.rendering.WidthRange
import com.github.ajalt.mordant.terminal.Terminal

class GraphWidget(
    val frames: List<BitrateFrame>,
    val lines: Int = 15,
    val lowEdge: Int = 0,
    val highEdge: Int = 8_000_000,
    val warningLine: Int = 6_000_000,
) : Widget {
    override fun measure(t: Terminal, width: Int): WidthRange {
        val min = 200
        return WidthRange(min, width.coerceAtLeast(min))
    }

    private fun Number.formatNumber(): String {
        return toString().reversed().chunked(3).joinToString("_").reversed()
    }

    private fun IntRange.format(): String {
        return start.formatNumber() + ".." + endInclusive.formatNumber()
    }


    override fun render(t: Terminal, width: Int): Lines {
        val infoLines: MutableList<MutableList<Span>> =
            MutableList(1) { mutableListOf() }
        val prefixLines: MutableList<MutableList<Span>> =
            MutableList(6) { mutableListOf() }
        val graphLines: MutableList<MutableList<Span>> =
            MutableList(lines) { mutableListOf() }
        val postFixLines: MutableList<MutableList<Span>> =
            MutableList(2) { mutableListOf() }

        val linesMultiplier = 4

        val unitsPerChar = (highEdge - lowEdge) / (lines * linesMultiplier)

//        fun bitrateToLineheight(bitrate: Long): Int = (bitrate / unitsPerChar).toInt()
        fun BitrateFrame.toLineHeight() = (bitrate / unitsPerChar).toInt()

//        val lineHeights = frames.takeLast((width - 10) * 2).mapIndexed { i, frame ->
//            (frame.bitrate / unitsPerChar).toInt()
//        }
//            .chunked(2).map {
//            it.getOrNull(0) to it.getOrNull(1)
//        }
//        val currentValue = frames.last().bitrate
        val warnHeight = warningLine / unitsPerChar

        fun colorForHeightWithWarning(lineHeight: Int) = if(lineHeight / linesMultiplier < warnHeight / linesMultiplier) {
            TextColors.hsv(((lines - lineHeight / linesMultiplier) * -12) % 360, 1, 1) + TextColors.black.bg
        } else {
            TextColors.black + TextColors.hsv(((lines - lineHeight / linesMultiplier) * -12) % 360, 1, 1).bg
        }
        fun colorForHeight(lineHeight: Int) =  TextColors.hsv(((lines - lineHeight) * -12) % 360, 1, 1)


        val frames = frames.takeLast(240)
        val indexedFrames = frames
            .withIndex()
//            .chunked(2).map {
//            it.getOrNull(0) to it.getOrNull(1)
//        }
        infoLines[0] += Span.word(
            frames.last().bitrate.formatNumber().padStart(9, '_'),
            colorForHeightWithWarning(frames.last().toLineHeight())
        )
        infoLines[0] += Span.space(3)
        infoLines[0] += Span.word("width:" + width)
        infoLines[0] += Span.space(6)
        infoLines[0] += Span.word("size:" + t.size)
        infoLines[0] += Span.space(6)
        infoLines[0] += Span.word("warn:")
        infoLines[0] += Span.space(3)
        infoLines[0] += Span.word(warnHeight.toString(), TextColors.black + TextColors.red.bg)
        infoLines[0] += Span.space(6)
        infoLines[0] += Span.word("frames:" + frames.size)
//        lineHeights.forEachIndexed() { frameIndex, lineHeight ->
//            val color = // colorFor(frameIndex)
//            if (frameIndex % 2 == 0){
//                TextColors.black + TextColors.brightWhite.bg
//            } else{
//                TextColors.brightWhite + TextColors.black.bg
//            }
//            infoLines[1] += Span.word(lineHeight.toString(), color)
//            infoLines[1] += Span.space(1)
//        }
//        infoLines0] += Span.word("max:")
//        infoLines0] += Span.space(9)
//        infoLines0] += Span.word(highEdge.formatNumber().padStart(9, '_'))
//        infoLines0] += Span.space(6)
//        infoLines0] += Span.word("current:")

//        t.print("graph")
//        t.println()
//        t.print("max: " + highEdge)
//        t.println()

        (lines - 1 downTo 0).forEachIndexed() { i, line ->
            val color = colorForHeight(line)

            val labelValue = ((line + 1) * unitsPerChar * linesMultiplier).formatNumber()
            if (labelValue.length < 9) {
                graphLines[i] += Span.space(9 - labelValue.length)
            }
            graphLines[i] += Span.word(labelValue, color)
//            graphLines[i] += Span.word("..")
//            graphLines[i] += Span.word(maxValue.formatNumber().padStart(9, '_'))
            graphLines[i] += Span.space(1)

            val row1 = ((line * linesMultiplier) + 3)
            val row2 = ((line * linesMultiplier) + 2)
            val row3 = ((line * linesMultiplier) + 1)
            val row4 = ((line * linesMultiplier) + 0)

            val isWarnRow = when (warnHeight) {
                row1, row2, row3, row4 -> true
                else -> false
            }

            val spans = listOf(Span.space(1, TextColors.black + color.bg)) +
                    indexedFrames.chunked(2).map {
                        it.getOrNull(0) to it.getOrNull(1)
                    }
                        .flatMap { (left, right) ->
                            val leftIndex = left?.index
                            val leftHeight = left?.value?.toLineHeight()
                            val rightIndex = right?.index
                            val rightHeight = right?.value?.toLineHeight()

//                        val heightCurrent = lineHeights[frameIndex]
//                        val heightBefore = lineHeights.getOrNull(frameIndex - 1) ?: heightCurrent
//                        val heightAfter = lineHeights.getOrNull(frameIndex + 1) ?: heightCurrent

//                val color = if(frameIndex%2 == 0) TextColors.white else TextColors.gray
                            val style = when {
//                    frameIndex == 0 -> TextColors.black + color.bg
                                isWarnRow -> {
                                    when (t.terminalInfo.ansiLevel) {
                                        AnsiLevel.ANSI16 -> TextColors.black + TextColors.brightRed.bg
                                        else -> TextColors.black + TextColors.hsv(0, .9, 1).bg
                                    }
//                        TextColors.black + TextColors.brightRed.bg
                                }
//                    lineHeight in row1Height..row4Height && warnHeight in row1Height..row4Height -> TextColors.black + TextColors.red.bg
//                    warningLine in minValue..maxValue -> TextColors.black + TextColors.red.bg
                                row1 > warnHeight -> color + TextColors.black.bg
                                row4 < warnHeight -> color + TextColors.black.bg
                                row1 > warnHeight -> TextColors.red + TextColors.black.bg
                                row4 < warnHeight -> TextColors.brightWhite + TextColors.black.bg
//                    value > warningLine ->  if(frameIndex%2 == 0) TextColors.red else TextColors.brightRed
//                    value < warningLine ->  if(frameIndex%2 == 0) TextColors.white else TextColors.brightWhite
                                else -> {
                                    error("")
//                        TextStyle()
//                        if (frameIndex % 2 == 0) TextColors.white else TextColors.gray
                                }
                            }

                            val span = run {

                                val glyph = when (leftHeight) {
                                    row1 -> when (rightHeight) {
                                        row1 -> "⠉"
                                        row2 -> "⠑"
                                        row3 -> "⠱"
                                        row4 -> "⢱"
                                        null, in 0..row1, in (row4 + 1)..Int.MAX_VALUE -> "⠁"
                                        else -> error("")
                                    }

                                    row2 -> when (rightHeight) {
                                        row1 -> "⠊"
                                        row2 -> "⠒"
                                        row3 -> "⠢"
                                        row4 -> "⢢"
                                        null, in 0..<row1, in (row4 + 1)..Int.MAX_VALUE -> "⠂"
                                        else -> error("")
                                    }

                                    row3 -> when (rightHeight) {
                                        row1 -> "⠎"
                                        row2 -> "⠔"
                                        row3 -> "⠤"
                                        row4 -> "⢄"
                                        null, in 0..<row1, in (row4 + 1)..Int.MAX_VALUE -> "⠄"
                                        else -> error("")
                                    }

                                    row4 -> when (rightHeight) {
                                        row1 -> "⡎"
                                        row2 -> "⡔"
                                        row3 -> "⡠"
                                        row4 -> "⣀"
                                        null, in 0..<row1, in (row4 + 1)..Int.MAX_VALUE -> "⡀"
                                        else -> error("")
                                    }

                                    null, in 0..<row1, in row4..Int.MAX_VALUE -> when (rightHeight) {
                                        row1 -> "⠈"
                                        row2 -> "⠐"
                                        row3 -> "⠠"
                                        row4 -> "⢀"
                                        null, in 0..<row1, in (row4 + 1)..Int.MAX_VALUE -> " "
                                        else -> error("")
                                    }

                                    else -> error("")
                                }
                                if (glyph == " ") {
                                    Span.space(1, style)
                                } else {
                                    Span.word(glyph, style)
                                }
                            }
//                if(value in minValue..maxValue) {
//                    Span.word("x", style)
//                } else {
//                    Span.space(1, style)
//                }

                            listOfNotNull(
                                span,
//                            spanAfter,
                            )
//                spanBefore // ?: spanAfter
                        }

            graphLines[i] += spans
        }

//        postFixLines[0] += Span.word("min:")
//        postFixLines[0] += Span.space(1)
//        postFixLines[0] += Span.word(lowEdge.toString())

        prefixLines[0] += Span.space(11)
        prefixLines[1] += Span.space(11)
        prefixLines[2] += Span.space(12)
        prefixLines[3] += Span.space(12)
        prefixLines[4] += Span.space(13)
        prefixLines[5] += Span.space(13)
        postFixLines[0] += Span.space(11)
        postFixLines[1] += Span.space(11)
        indexedFrames
            .chunked(6)
            .forEach { chunk ->
                repeat(6) { i ->
                    prefixLines[i] += chunk.getOrNull(i)?.let {
                        val height = it.value.toLineHeight()
                        val color = colorForHeightWithWarning(height) // + TextColors.black.bg
                        listOf(
                            Span.word(height.toString(), color),
                            Span.space(1)
                        )
                    } ?: run {
                        listOf(
                            Span.space(3)
                        )
                    }
                }
            }

        return Lines(
            infoLines.map { Line(it) } +
                    prefixLines.map { Line(it) } +
                    graphLines.map { Line(it) }
//                    + postFixLines.map { Line(it) }
        )
    }
}