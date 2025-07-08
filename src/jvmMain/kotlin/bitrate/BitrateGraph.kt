//package bitrate
//
//import io.ktor.client.*
//import io.ktor.client.engine.cio.*
//import io.ktor.client.plugins.websocket.*
//import io.ktor.utils.io.core.*
//import io.ktor.websocket.*
//import kotlin.time.Instant
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.json.Json
//import kotlin.io.encoding.Base64
//import kotlin.io.encoding.ExperimentalEncodingApi
//
//@Serializable
//data class BitrateFrame(
//    val timestamp: Long,
//    val bitrate: Int
//)
//
//@OptIn(ExperimentalEncodingApi::class)
//suspend fun main(args: Array<String>) {
//    val httpClient = HttpClient(CIO) {
//        install(WebSockets)
//    }
//
//    val streamKey = "vrcdn_69e94592-fb4d-4672-9959-eb3f03c15ba2p"
//
//    httpClient.webSocket("wss://ws.vrcdn.live/bitrate") {
//        run {
//            val xorKey = 42 // The meaning of life
//            val xoredString = streamKey.map { (it.code xor xorKey).toChar() }.joinToString("")
//            val newKey = Base64.Default.encode(xoredString.toByteArray())
//            send("""{"subscribe": "$newKey"}""")
//        }
//
//        while (true) {
//            val receivedFrame = incoming.receive() as? Frame.Text ?: continue
//            val bitrateFrame = Json.Default.decodeFromString(BitrateFrame.serializer(), receivedFrame.readText())
//            val datetime = Instant.fromEpochMilliseconds(bitrateFrame.timestamp)
////                val bitrate = bitrateFrame.bitrate / 1000f
//            println(
//                listOf(
//                    datetime.toString(),
//                    bitrateFrame.bitrate.toString().reversed().chunked(3).joinToString("_").reversed(),
//                    "bit/s"
//                ).joinToString(" ")
//            )
//        }
//    }
//}
//
////class WebSocketClient(private val url: String, private val key: String) {
////    private var socket: WebSocket? = null
////    private val reconnectTimeout = 5000L // 5 seconds
////    private val maxReconnections = 10
////    private var reconnectionAttempts = 0
////
////    init {
////        initWebSocket(key)
////    }
////
////    private fun initWebSocket(key: String) {
////        socket = WebSocket(url).apply {
////            onOpen = {
////                subscribe(key)
////            }
////
////            onMessage = { event ->
////                val msg = Json.decodeFromString<Map<String, Any>>(event.data)
////                val date = Date(msg["timestamp"] as Long)
////                addData("${date.hours}:${date.minutes}", (msg["bitrate"] as Double) / 1000)
////            }
////
////            onError = { event ->
////                println("Error with WebSocket: $event")
////            }
////
////            onClose = {
////                println("Connection closed")
////                reconnect()
////            }
////        }
////    }
////
////    fun send(message: String) {
////        socket?.send(message) ?: println("Socket is not initialized yet.")
////    }
////
////    private fun subscribe(streamKey: String) {
////        val xorKey = 42 // The meaning of life
////        val xoredString = streamKey.map { (it.toInt() xor xorKey).toChar() }.joinToString("")
////        val newKey = Base64.getEncoder().encodeToString(xoredString.toByteArray())
////        send("""{"subscribe": "$newKey"}""")
////    }
////
////    fun close() {
////        socket?.close()
////    }
////
////    private fun reconnect() {
////        if (reconnectionAttempts < maxReconnections) {
////            timer("ReconnectTimer", false, reconnectTimeout) {
////                println("Trying to reconnect... Attempt #${++reconnectionAttempts}")
////                initWebSocket(key)
////            }
////        } else {
////            println("Maximum reconnection attempts reached. Stopping.")
////        }
////    }
////
////    fun destroy() {
////        socket?.apply {
////            onMessage = null
////            onError = null
////            onClose = null
////            close()
////        }
////        socket = null
////    }
////}