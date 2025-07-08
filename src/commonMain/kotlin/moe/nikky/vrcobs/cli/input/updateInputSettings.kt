package moe.nikky.vrcobs.cli.input

import com.rejeq.ktobs.model.MediaAction
import com.rejeq.ktobs.request.inputs.getInputSettings
import com.rejeq.ktobs.request.inputs.setInputSettings
import com.rejeq.ktobs.request.mediainputs.triggerMediaInputAction
import com.rejeq.ktobs.request.ui.openSourceProjector
import kotlinx.coroutines.delay
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import moe.nikky.vrcobs.OBSConnectionProperties
import moe.nikky.vrcobs.obsSession

suspend fun updateInputSettings(
    obsConnectionProperties: OBSConnectionProperties,
    source: String = "vrcdn",
    inputSettings: JsonObject,
    openProjector: Boolean
) {
    obsSession(obsConnectionProperties) {
        val previousSettings = getInputSettings(
            inputName = source,
        ).settings
        println("getInputSettings: $previousSettings")
        val needsUpdate = inputSettings.any { (key, value) ->
            previousSettings.jsonObject[key] != value
        }
        if (needsUpdate) {
            println("previous input was: $previousSettings")
            println("updating input to $inputSettings")
            setInputSettings(
                name = source,
                settings = inputSettings
            )
            println("updated $source")
            delay(50)
        } else {
            println("input already set to $inputSettings")
        }
        println("setting media to play")
        triggerMediaInputAction(source, mediaAction = MediaAction.Play)
        delay(50)
        if (openProjector) {
            openSourceProjector(name = source, uuid = "${source}_preview", monitorIndex = -1, geometry = "1600x900")
        }

        delay(100)

        getInputSettings(
            inputName = source,
        ).settings.also {
            println("media source: $it")
        }
    }
}