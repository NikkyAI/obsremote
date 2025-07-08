package moe.nikky.vrcobs.cli.input

import FILESYSTEM
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.boolean
import com.saveourtool.okio.safeToRealPath
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import moe.nikky.vrcobs.OBSConnectionOptionGroup
import moe.nikky.vrcobs.cli.BaseCommand
import okio.Path.Companion.toPath

object FileInputCommand: BaseCommand(
    "file"
) {
    override fun help(context: Context): String =
        "updates file in media source `vrcdn`"
    private val file by argument("file")
    private val source by option("--source")
        .default("vrcdn")
    private val openProjector by option("--projector", "-p")
        .flag(default = false, defaultForHelp = "false")
    private val obsConnection by OBSConnectionOptionGroup()

    override suspend fun run() {
        val path = file.toPath().safeToRealPath()
        val exists = FILESYSTEM.exists(path)
        require(exists) {
            "path $path does not exist"
        }
        val inputSettings = buildJsonObject {
            put("is_local_file", true)
            put("local_file", path.toString())
        }
//        val pathString = path.toString()
        updateInputSettings(
            obsConnectionProperties = obsConnection.properties,
            source = source,
            inputSettings = inputSettings,
            openProjector = openProjector,
        )
    }
}
