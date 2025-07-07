package moe.nikky.vrcobs.cli

import com.github.ajalt.clikt.completion.completionOption
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.subcommands
import moe.nikky.vrcobs.cli.bitrate.BitrateGroup
import moe.nikky.vrcobs.cli.input.InputGroup

class RootCommand : BaseCommand(
    "obsremote"
){
    override fun help(context: Context) = """
        cli for interacting with obs via websocket api
    """.trimIndent()

    override val invokeWithoutSubcommand: Boolean = false

    init {

        completionOption()

        subcommands(
            InputGroup,
            BitrateGroup,
            GraphCommand,
            ViewersCommand,
            ConfigCommand
        )
    }

    override suspend fun run() {
//        println("running obs-cli")
    }
}