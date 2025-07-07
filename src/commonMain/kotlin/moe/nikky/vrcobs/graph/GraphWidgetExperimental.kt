package moe.nikky.vrcobs.graph

import com.github.ajalt.mordant.rendering.AnsiLevel
import com.github.ajalt.mordant.rendering.Line
import com.github.ajalt.mordant.rendering.Lines
import com.github.ajalt.mordant.rendering.Span
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.Widget
import com.github.ajalt.mordant.rendering.WidthRange
import com.github.ajalt.mordant.terminal.Terminal

class GraphWidgetExperimental(
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
        val prefixLines: MutableList<MutableList<Span>> =
            MutableList(3) { mutableListOf() }
        val graphLines: MutableList<MutableList<Span>> =
            MutableList(lines) { mutableListOf() }
        val postFixLines: MutableList<MutableList<Span>> =
            MutableList(3) { mutableListOf() }

        val linesMultiplier = 4

        val unitsPerChar = (highEdge - lowEdge) / (lines * linesMultiplier)

        val lineHeights = frames.takeLast(width - 10).mapIndexed { i, frame ->
            val previous = frames.getOrNull(i - 1) ?: frame
            val next = frames.getOrNull(i + 1) ?: frame

            (frame.bitrate / unitsPerChar).toInt()
        }
        val currentValue = frames.last().bitrate
        val warnHeight = warningLine / unitsPerChar

        prefixLines[0] += Span.word("width:" + width)
        prefixLines[0] += Span.space(6)
        prefixLines[0] += Span.word("size:" + t.size)
        prefixLines[0] += Span.space(6)
        prefixLines[0] += Span.word("warn:")
        prefixLines[0] += Span.space(3)
        prefixLines[0] += Span.word(warnHeight.toString(), TextColors.black + TextColors.red.bg)
        prefixLines[0] += Span.space(6)
        prefixLines[0] += Span.word("frames:" + frames.size)
//        lineHeights.forEachIndexed() { frameIndex, lineHeight ->
//            val color = // colorFor(frameIndex)
//            if (frameIndex % 2 == 0){
//                TextColors.black + TextColors.brightWhite.bg
//            } else{
//                TextColors.brightWhite + TextColors.black.bg
//            }
//            prefixLines[1] += Span.word(lineHeight.toString(), color)
//            prefixLines[1] += Span.space(1)
//        }
//        prefixLines[0] += Span.word("max:")
//        prefixLines[0] += Span.space(9)
//        prefixLines[0] += Span.word(highEdge.formatNumber().padStart(9, '_'))
//        prefixLines[2] += Span.space(7)
//        prefixLines[2] += Span.word("current:")
//        prefixLines[2] += Span.space(3)
//        prefixLines[2] += Span.word(currentValue.formatNumber().padStart(9, '_'))


        fun colorForHeight(lineHeight : Int) = TextColors.hsv(((lines - lineHeight) * -12) % 360, 1, 1)
//        t.print("graph")
//        t.println()
//        t.print("max: " + highEdge)
//        t.println()
        (lines - 1 downTo 0).forEachIndexed() { i, line ->
//            val minValue = line * unitsPerChar * linesMultiplier
//            val maxValue = (line + 1) * unitsPerChar * linesMultiplier

            val color = colorForHeight(line)

//            graphLines[i] += Span.word("line")
//            graphLines[i] += Span.space(1)
//            graphLines[i] += Span.word("$i".padStart(2, '_'), TextColors.gray.bg)
//            graphLines[i] += Span.space(1)
//            graphLines[i] += Span.word("$line".padStart(2, '_'))
//            graphLines[i] += Span.space(1)
            val labelValue = ((line+1) * unitsPerChar * linesMultiplier).formatNumber()
            if (labelValue.length < 9) {
                graphLines[i] += Span.space(9 - labelValue.length)
            }
            graphLines[i] += Span.word(labelValue, color)
//            graphLines[i] += Span.word("..")
//            graphLines[i] += Span.word(maxValue.formatNumber().padStart(9, '_'))
            graphLines[i] += Span.space(1)

//            val row1Range = (((line * linesMultiplier)+3) * unitsPerChar)..(((line * linesMultiplier)+4) * unitsPerChar)
//            val row2Range = (((line * linesMultiplier)+2) * unitsPerChar)..(((line * linesMultiplier)+3) * unitsPerChar)
//            val row3Range = (((line * linesMultiplier)+1) * unitsPerChar)..(((line * linesMultiplier)+2) * unitsPerChar)
//            val row4Range = (((line * linesMultiplier)  ) * unitsPerChar)..(((line * linesMultiplier)+1) * unitsPerChar)
//            graphLines[i] += Span.word(row1Range.format().padStart(20, '_'), if(currentValue in row1Range) TextColors.green else TextColors.gray.bg)
//            graphLines[i] += Span.space(3)
//            graphLines[i] += Span.word(row2Range.format().padStart(20, '_'), if(currentValue in row2Range) TextColors.green else TextColors.black.bg)
//            graphLines[i] += Span.space(3)
//            graphLines[i] += Span.word(row3Range.format().padStart(20, '_'), if(currentValue in row3Range) TextColors.green else TextColors.gray.bg)
//            graphLines[i] += Span.space(3)
//            graphLines[i] += Span.word(row4Range.format().padStart(20, '_'), if(currentValue in row4Range) TextColors.green else TextColors.black.bg)
//            graphLines[i] += Span.space(3)

            val row1Height = ((line * linesMultiplier) + 3)
            val row2Height = ((line * linesMultiplier) + 2)
            val row3Height = ((line * linesMultiplier) + 1)
            val row4Height = ((line * linesMultiplier) + 0)

            val isWarnRow = when (warnHeight) {
                row1Height, row2Height, row3Height, row4Height -> true
                else -> false
            }

//            fun renderRowHeight(rowHeight: Int) {
//                graphLines[i] += Span.space(3)
//                graphLines[i] += Span.word(
//                    rowHeight.toString().padStart(2, '0'),
//                    if (isWarnRow) {
//                        if (lineHeights.last() == rowHeight)
//                            TextColors.black + TextColors.brightMagenta.bg
//                        else
//                            TextColors.black + TextColors.red.bg
//                    } else {
//                        if (lineHeights.last() == rowHeight)
//                            TextColors.brightWhite + TextColors.gray.bg
//                        else
//                            TextColors.black.bg
//                    }
//                )
//            }
//            renderRowHeight(row4Height)
//            renderRowHeight(row3Height)
//            renderRowHeight(row2Height)
//            renderRowHeight(row1Height)
//            graphLines[i] += Span.space(3)

            val spans = listOf(Span.space(1, TextColors.black + color.bg)) +
                    frames.takeLast(width - 11).flatMapIndexed { frameIndex, frame ->
                val heightCurrent = lineHeights[frameIndex]
                val heightBefore = lineHeights.getOrNull(frameIndex - 1) ?: heightCurrent
                val heightAfter = lineHeights.getOrNull(frameIndex + 1) ?: heightCurrent

//                val color = if(frameIndex%2 == 0) TextColors.white else TextColors.gray
                val style = when {
//                    frameIndex == 0 -> TextColors.black + color.bg
                    isWarnRow -> {
                        when(t.terminalInfo.ansiLevel) {
                            AnsiLevel.ANSI16 -> TextColors.black + TextColors.brightRed.bg
                            else -> TextColors.black + TextColors.hsv(0, .9, 1).bg
                        }
//                        TextColors.black + TextColors.brightRed.bg
                    }
//                    lineHeight in row1Height..row4Height && warnHeight in row1Height..row4Height -> TextColors.black + TextColors.red.bg
//                    warningLine in minValue..maxValue -> TextColors.black + TextColors.red.bg
                    row1Height > warnHeight -> color + TextColors.black.bg
                    row4Height < warnHeight -> color + TextColors.black.bg
                    row1Height > warnHeight -> TextColors.red + TextColors.black.bg
                    row4Height < warnHeight -> TextColors.brightWhite + TextColors.black.bg
//                    value > warningLine ->  if(frameIndex%2 == 0) TextColors.red else TextColors.brightRed
//                    value < warningLine ->  if(frameIndex%2 == 0) TextColors.white else TextColors.brightWhite
                    else -> {
                        error("")
//                        TextStyle()
//                        if (frameIndex % 2 == 0) TextColors.white else TextColors.gray
                    }
                }


                val spanBefore = run {

                    val glyph = when {
                        heightCurrent == row1Height -> when {
                            heightBefore >= row1Height -> "⠉"
                            heightBefore == row2Height -> "⠊"
                            heightBefore == row3Height -> "⠎"
                            heightBefore <= row4Height -> "⡎"
                            else -> error("")
                        }

                        heightCurrent == row2Height -> when {
                            heightBefore >= row1Height -> "⠑"
                            heightBefore == row2Height -> "⠒"
                            heightBefore == row3Height -> "⠔"
                            heightBefore <= row4Height -> "⡔"
                            else -> error("")
                        }

                        heightCurrent == row3Height -> when {
                            heightBefore >= row1Height -> "⠱"
                            heightBefore == row2Height -> "⠢"
                            heightBefore == row3Height -> "⠤"
                            heightBefore <= row4Height -> "⡠"
                            else -> error("")
                        }

                        heightCurrent == row4Height -> when {
                            heightBefore >= row1Height -> "⢱"
                            heightBefore == row2Height -> "⢢"
                            heightBefore == row3Height -> "⢄"
                            heightBefore <= row4Height -> "⣀"
                            else -> error("")
                        }
//                        heightCurrent in (heightBefore + 1)..<heightAfter && heightBefore <= row4Height && heightAfter >= row1Height -> {
//                            "/"
//                        }
//                        heightCurrent in (heightAfter + 1)..<heightBefore && heightBefore >= row4Height && heightAfter <= row1Height -> {
//                            "\\"
//                        }
                        // climbing
                        heightBefore < row4Height && heightAfter > row1Height -> when {
                            row4Height == heightBefore + 1 -> {
                                "⡜"
                            }

                            row4Height == heightBefore + 2 -> {
                                "⡇"
                            }

                            else -> " "
//                            else -> {
//                                "⡇"
//                            }
                        }
                        // falling
                        heightBefore > row1Height && heightAfter < row4Height -> when {
                            row1Height == heightBefore - 1 -> {
                                "⢣"
                            }

                            row1Height == heightBefore - 2 -> {
                                "⢸"
                            }

                            else -> " "
//                            else -> {
//                                "⢸"
//                            }
                        }

                        else -> " "
                    }
                    if (glyph == " ") {
                        Span.space(1, style)
                    } else {
                        Span.word(glyph, style)
                    }
                }
                val spanAfter = run {

                    val glyph = when {
                        heightCurrent == row1Height -> when {
                            row1Height <= heightAfter -> "⠉"
                            row2Height == heightAfter -> "⠑"
                            row3Height == heightAfter -> "⠱"
                            row4Height >= heightAfter -> "⢱"
                            else -> error("")
                        }

                        heightCurrent == row2Height -> when {
                            row1Height <= heightAfter -> "⠊"
                            row2Height == heightAfter -> "⠒"
                            row3Height == heightAfter -> "⠢"
                            row4Height >= heightAfter -> "⢢"
                            else -> error("")
                        }

                        heightCurrent == row3Height -> when {
                            row1Height <= heightAfter -> "⠎"
                            row2Height == heightAfter -> "⠔"
                            row3Height == heightAfter -> "⠤"
                            row4Height >= heightAfter -> "⢄"
                            else -> error("")
                        }

                        heightCurrent == row4Height -> when {
                            row1Height <= heightAfter -> "⡎"
                            row2Height == heightAfter -> "⡔"
                            row3Height == heightAfter -> "⡠"
                            row4Height >= heightAfter -> "⣀"
                            else -> error("")
                        }
                        // climbing
                        heightBefore < row3Height && heightAfter > row1Height -> when {
                            row1Height == heightAfter - 1 -> "⡜"
                            row1Height == heightAfter - 2 -> "⡇"
                            else -> " "
//                            else -> "⡇"
                        }
                        // falling
                        heightBefore > row1Height && heightAfter < row3Height -> when {
                            row4Height == heightAfter + 1 -> "⢣"
                            row4Height == heightAfter + 2 -> "⢸"
                            else -> " "
//                            else -> "⢸"
                        }

                        else -> " "
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
                    spanBefore,
                    spanAfter,
                )
//                spanBefore // ?: spanAfter
            }

            graphLines[i] += spans
        }

//        postFixLines[0] += Span.word("min:")
//        postFixLines[0] += Span.space(1)
//        postFixLines[0] += Span.word(lowEdge.toString())

        prefixLines[1] += Span.space(11)
        prefixLines[2] += Span.space(11)
        lineHeights.forEachIndexed() { frameIndex, lineHeight ->
//            val value = frames[frameIndex].bitrate
            val color = colorForHeight(lineHeight / linesMultiplier) + TextColors.black.bg
//                if (frameIndex % 2 == 0) {
//                    TextColors.black + TextColors.brightWhite.bg
//                } else {
//                    TextColors.brightWhite + TextColors.black.bg
//                }

            if (frameIndex % 2 == 0) {
                prefixLines[1] += Span.word(lineHeight.toString(), color)
                prefixLines[2] += Span.space(2, color)
            } else {
                prefixLines[1] += Span.space(2, color)
                prefixLines[2] += Span.word(lineHeight.toString(), color)
            }
        }

        return Lines(
            prefixLines.map { Line(it) } +
                    graphLines.map { Line(it) } +
                    postFixLines.map { Line(it) }
        )
    }
}