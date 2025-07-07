package moe.nikky.vrcobs.cli.input

import com.github.ajalt.clikt.core.subcommands
import moe.nikky.vrcobs.cli.CommandGroup
import moe.nikky.vrcobs.cli.bitrate.OBSBitrateCommand
import moe.nikky.vrcobs.cli.bitrate.VRCDNBitrateCommand
import moe.nikky.vrcobs.cli.input.VRCDNInputCommand

object InputGroup: CommandGroup("input") {
    init {
        subcommands(
            VRCDNInputCommand,
            UrlInputCommand,
            FileInputCommand,
        )
    }
}