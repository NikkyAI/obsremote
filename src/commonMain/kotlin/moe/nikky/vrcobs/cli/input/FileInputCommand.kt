package moe.nikky.vrcobs.cli.input

import FILESYSTEM
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.rejeq.ktobs.model.MediaAction
import com.rejeq.ktobs.request.inputs.getInputSettings
import com.rejeq.ktobs.request.inputs.setInputSettings
import com.rejeq.ktobs.request.mediainputs.triggerMediaInputAction
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put
import moe.nikky.vrcobs.OBSConnectionOptionGroup
import moe.nikky.vrcobs.cli.BaseCommand
import moe.nikky.vrcobs.obsSession
import okio.Path.Companion.toPath

object FileInputCommand: BaseCommand(
    "file"
) {
    override fun help(context: Context): String =
        "updates url in media source `vrcdn`"
    private val file by argument("file")
    val obsConnection by OBSConnectionOptionGroup()

    override suspend fun run() {
        val path = file.toPath().normalized()
        val exists = FILESYSTEM.exists(path)
        require(exists) {
            "path $path does not exist"
        }
        val inputSettings = buildJsonObject {
            put("is_local_file", true)
            put("local_file", path.toString())
        }
//        val pathString = path.toString()
        obsSession(obsConnection.properties) {
            val previousSettings = getInputSettings(
                inputName = "vrcdn",
            ).settings
            println("getInputSettings: $previousSettings")
//            val previousInput = previousSettings.jsonObject["local_file"]?.jsonPrimitive?.content
            val needsUpdate = inputSettings.any { (key, value) ->
                previousSettings.jsonObject[key] != value
            }
            if (needsUpdate) {
                println("previous input was: $previousSettings")
                println("updating input to $inputSettings")
                setInputSettings(
                    name = "vrcdn",
                    settings = inputSettings
                )
                println("updated vrcdn")
            } else {
                println("input already set to $inputSettings")
            }
            println("setting media to play")
            triggerMediaInputAction("vrcdn", mediaAction = MediaAction.Play)

            getInputSettings(
                inputName = "vrcdn",
            ).settings.also {
                println("media source: $it")
            }
        }
    }
}