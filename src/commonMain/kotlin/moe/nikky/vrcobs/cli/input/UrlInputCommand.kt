package moe.nikky.vrcobs.cli.input

import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.boolean
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import moe.nikky.vrcobs.OBSConnectionOptionGroup
import moe.nikky.vrcobs.cli.BaseCommand

object UrlInputCommand: BaseCommand(
    "url"
) {
    override fun help(context: Context): String =
        "updates url in media source `vrcdn`"
    private val url by argument("url")
    private val source by option("--source")
        .default("vrcdn")
    private val openProjector by option("--projector", "-p")
        .flag(default = false, defaultForHelp = "false")
    private val obsConnection by OBSConnectionOptionGroup()

    override suspend fun run() {
        val inputSettings = buildJsonObject {
            put("is_local_file", false)
            put("input", url)
        }
        updateInputSettings(
            obsConnectionProperties = obsConnection.properties,
            source = source,
            inputSettings = inputSettings,
            openProjector = openProjector
        )
    }
}