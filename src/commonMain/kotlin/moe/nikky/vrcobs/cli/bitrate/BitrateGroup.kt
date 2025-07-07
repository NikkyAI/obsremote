package moe.nikky.vrcobs.cli.bitrate

import com.github.ajalt.clikt.core.subcommands
import moe.nikky.vrcobs.cli.CommandGroup

object BitrateGroup: CommandGroup("bitrate") {
    init {
        subcommands(
            VRCDNBitrateCommand,
            OBSBitrateCommand,
        )
    }
}