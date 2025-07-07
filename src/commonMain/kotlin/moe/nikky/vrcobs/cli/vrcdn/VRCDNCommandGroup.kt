package moe.nikky.vrcobs.cli.vrcdn

import com.github.ajalt.clikt.core.subcommands
import moe.nikky.vrcobs.cli.CommandGroup
import moe.nikky.vrcobs.cli.ViewersCommand
import moe.nikky.vrcobs.cli.bitrate.VRCDNBitrateCommand
import moe.nikky.vrcobs.cli.input.VRCDNInputCommand

object VRCDNCommandGroup: CommandGroup(
    "vrcdn"
) {
    init {
        subcommands(
            VRCDNBitrateCommand,
            ViewersCommand,
            VRCDNInputCommand,
        )
    }
}