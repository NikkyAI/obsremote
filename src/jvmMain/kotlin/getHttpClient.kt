import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*

actual fun getHttpClient() = HttpClient(CIO) {
    install(WebSockets)
    install(ContentNegotiation) {
        json()
    }
}