package moe.nikky.vrcobs.cli

import DOTENV
import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.command.SuspendingNoOpCliktCommand
import com.github.ajalt.clikt.core.context
import com.github.ajalt.clikt.core.installMordantMarkdown
import com.github.ajalt.clikt.output.MordantMarkdownHelpFormatter
import io.ktor.util.*

abstract class BaseCommand(
    name: String
): SuspendingCliktCommand(
    name = name,
) {
    init {
        installMordantMarkdown()
        context {
            readEnvvar = { DOTENV[it] }
            transformToken = { it.lowercase() }
            helpFormatter = { ctx ->
                MordantMarkdownHelpFormatter(
                    context = ctx,
                    showDefaultValues = true,
                    showRequiredTag = true,
                )
            }
        }
    }
    override suspend fun run() {

    }
}


abstract class CommandGroup(
    name: String
) : SuspendingNoOpCliktCommand(
    name = name,
) {
    init {
        installMordantMarkdown()
        context {
            readEnvvar = { DOTENV[it] }
            transformToken = { it.lowercase() }
            helpFormatter = { ctx ->
                MordantMarkdownHelpFormatter(
                    context = ctx,
                    showDefaultValues = true,
                    showRequiredTag = true
                )
            }
        }
    }
}