package moe.nikky.vrcobs

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import com.rejeq.ktobs.ObsAuthException
import com.rejeq.ktobs.ObsEventSubs
import com.rejeq.ktobs.ObsRequestException
import com.rejeq.ktobs.ObsSession
import com.rejeq.ktobs.event.general.ExitStartedEvent
import com.rejeq.ktobs.event.inputs.InputCreatedEvent
import com.rejeq.ktobs.event.inputs.InputCreatedEventData
import com.rejeq.ktobs.ktor.ObsSessionBuilder
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import kotlinx.serialization.json.Json

class OBSConnectionOptionGroup : OptionGroup(
    "OBS websocket settings"
) {
    val host by option("--obs-host", envvar = "OBS_HOST", helpTags = mapOf("env" to "OBS_HOST"))
        .default("127.0.0.1")

    val port by option("--obs-port", envvar = "OBS_PORT", helpTags = mapOf("env" to "OBS_PORT"))
        .int()
        .default(4455)
        .check("port must in 1..65535") {
            it in 1..65535
        }

    val password by option("--obs-password", envvar = "OBS_PASSWORD", helpTags = mapOf("env" to "OBS_PASSWORD"))

    val properties get() = OBSConnectionProperties(
        host = host,
        port = port,
        password = password
    )
}

data class OBSConnectionProperties(
    val host: String = "127.0.0.1",
    val port: Int = 4455,
    val password: String? = null
)

suspend fun obsSession(
    connectionProps: OBSConnectionProperties, // = OBSConnectionProperties(),
    connectBlock: suspend ObsSession.() -> Unit
) {
//    println("connecting using $connectionProps")

    HttpClient(CIO) {
        install(WebSockets.Plugin) {
            pingIntervalMillis = 20_000
            contentConverter = KotlinxWebsocketSerializationConverter(Json.Default)
        }
    }.use { client ->
        val session = ObsSessionBuilder(client).apply {
            host = connectionProps.host
            port = connectionProps.port
            if(!connectionProps.password.isNullOrBlank()) {
                password = connectionProps.password
            }

            eventSubs = ObsEventSubs.General +  ObsEventSubs.MediaInputs
//            eventSubs = ObsEventSubs.General + ObsEventSubs.Inputs
            // or listen all events
            // eventsSubs = ObsEventSubs.All

            onEvent = { event ->
                when (event.eventType) {
                    ExitStartedEvent -> println("OBS is shutting down")
                    InputCreatedEvent -> {
                        val data = event.get<InputCreatedEventData>()
                        println("Input was created: ${data.inputName}")
                    }
                }
            }
        }

        try {
            session.connect(connectBlock)
        } catch (e: ObsAuthException) {
            println("Failed to authenticate: $e")
        } catch (e: ObsRequestException) {
            println("Failed to execute request: $e")
        }
    }
}