package moe.nikky.vrcobs

import com.rejeq.ktobs.ObsAuthException
import com.rejeq.ktobs.ObsEventSubs
import com.rejeq.ktobs.ObsRequestException
import com.rejeq.ktobs.event.general.ExitStartedEvent
import com.rejeq.ktobs.event.inputs.InputCreatedEvent
import com.rejeq.ktobs.event.inputs.InputCreatedEventData
import com.rejeq.ktobs.ktor.ObsSessionBuilder
import com.rejeq.ktobs.model.MediaAction
import com.rejeq.ktobs.request.general.getStats
import com.rejeq.ktobs.request.inputs.getInputKindList
import com.rejeq.ktobs.request.inputs.getInputSettings
import com.rejeq.ktobs.request.inputs.setInputSettings
import com.rejeq.ktobs.request.mediainputs.triggerMediaInputAction
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import kotlinx.coroutines.runBlocking
import io.ktor.serialization.kotlinx.*
import kotlinx.serialization.json.*


fun main() = runBlocking {
    val client = HttpClient(CIO) {
        install(WebSockets) {
            pingIntervalMillis = 20_000
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
    }
    // Configure OBS session
    val session = ObsSessionBuilder(client).apply {
        host = "127.0.0.1"
        port = 4455
//        password = "your_password"
        eventSubs = ObsEventSubs.General + ObsEventSubs.Inputs
        // or listen all events
        // eventsSubs = ObsEventSubs.All
    }

    // Handle OBS events
    session.onEvent = { event ->
        when (event.eventType) {
            ExitStartedEvent -> println("OBS is shutting down")
            InputCreatedEvent -> {
                val data = event.get<InputCreatedEventData>()
                println("Input was created: ${data.inputName}")
            }
        }
    }

    val vrcdn = "heelix"
    val nextInput = "rtmp://stream.vrcdn.live/live/$vrcdn"

    // Connect and execute commands
    try {
        session.connect {
            // Get list of input kinds
            val kindList = getInputKindList()
            println("Available input kinds: $kindList")

            getStats()

            val previousSettings = getInputSettings(
                inputName = "vrcdn",
            ).settings
            println("getInputSettings: $previousSettings")
            val previousInput = previousSettings.jsonObject["input"]?.jsonPrimitive?.content

//            setInputSettings(
//                name = "vrcdn",
//                settings = buildJsonObject {
//                    put("is_local_file", "false")
//                    put("reconnect_delay_sec", 1)
//                    put("hw_decode", true)
////                    put("restart_on_activate", false)
//                    put("ffmpeg_options", "rtsp_transport=tcp")
//                }
//            )
            if (previousInput != nextInput) {
                println("previous input was: $previousInput")
                setInputSettings(
                    name = "vrcdn",
                    settings = buildJsonObject {
                        put("input", nextInput)
                    }
                )
                println("updated vrcdn")
            } else {
                println("input already set to $nextInput")
            }
            println("setting media to play")
            triggerMediaInputAction("vrcdn", mediaAction = MediaAction.Play)

            getInputSettings(
                inputName = "vrcdn",
            ).settings.also {
                println("media source: $it")
            }
        }
    } catch (e: ObsAuthException) {
        println("Failed to authenticate: $e")
    } catch (e: ObsRequestException) {
        println("Failed to execute request: $e")
    }

    client.close()
}