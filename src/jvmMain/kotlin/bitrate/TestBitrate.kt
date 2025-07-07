package bitrate

import com.github.ajalt.clikt.command.main
import moe.nikky.vrcobs.cli.ViewersCommand

suspend fun main(args: Array<String>) {
    ViewersCommand.main(listOf("svenrichter02"))
//    GraphCommand.main(listOf())
//    BitrateCommand.echoFormattedHelp()
//    VRCDNBitrateCommand.echoFormattedHelp()
//    BitrateCommand.main(listOf())
//    VRCDNBitrateCommand.main(listOf("-f"))
//    VRCDNBitrateCommand.main(listOf())
}