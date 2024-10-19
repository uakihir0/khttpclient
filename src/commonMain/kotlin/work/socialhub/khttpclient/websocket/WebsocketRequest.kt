package work.socialhub.khttpclient.websocket

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.pingInterval
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.headers
import io.ktor.client.request.url
import io.ktor.http.HttpMethod
import io.ktor.http.URLBuilder
import io.ktor.http.takeFrom
import io.ktor.websocket.Frame
import io.ktor.websocket.readBytes
import io.ktor.websocket.readText
import io.ktor.websocket.send
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.forEach
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.time.Duration.Companion.milliseconds

class WebsocketRequest {

    var schema: String = "ws"
    var host: String? = null
    var path: String? = null
    var port: Int? = null
    var url: String? = null

    var accept: String? = null
    var userAgent: String? = "kHttpClient/1.0"
    val header = mutableMapOf<String, String>()

    var textListener: suspend (String) -> Unit = {}
    var bytesListener: suspend (ByteArray) -> Unit = {}

    var onOpenListener: (WebsocketRequest) -> Unit = {}
    var onCloseListener: (WebsocketRequest) -> Unit = {}

    // Basic
    fun schema(schema: String) = also { it.schema = schema }
    fun host(host: String) = also { it.host = host }
    fun path(path: String) = also { it.path = path }
    fun port(port: Int?) = also { it.port = port }
    fun url(url: String?) = also { it.url = url }

    // Listener
    fun textListener(listener: suspend (String) -> Unit) = also { it.textListener = listener }
    fun bytesListener(listener: suspend (ByteArray) -> Unit) = also { it.bytesListener = listener }
    fun onOpenListener(listener: (WebsocketRequest) -> Unit) = also { it.onOpenListener = listener }
    fun onCloseListener(listener: (WebsocketRequest) -> Unit) = also { it.onCloseListener = listener }

    // Headers
    fun accept(accept: String) = also { it.accept = accept }
    fun userAgent(userAgent: String) = also { it.userAgent = userAgent }
    fun header(key: String, value: String) = also { it.header[key] = value }

    private val client = HttpClient {
        install(WebSockets) {
            pingInterval = 20_000.milliseconds
        }
    }

    private var session: DefaultClientWebSocketSession? = null

    // Start
    suspend fun open() = start(HttpMethod.Get)
    suspend fun openPost() = start(HttpMethod.Post)

    suspend fun start(method: HttpMethod) = also {
        val req = this
        accept?.let { header["Accept"] = it }
        userAgent?.let { header["User-Agent"] = it }

        client.webSocket({
            this.method = method
            if (req.url != null) {
                val tmp = checkNotNull(req.url)
                this.url.takeFrom(URLBuilder(tmp))
            } else {
                this.url(
                    req.schema,
                    req.host,
                    req.port,
                    req.path,
                )
            }
            this.headers {
                req.header.forEach { (k, v) ->
                    append(k, v)
                }
            }
        }) {
            try {
                session = this
                req.onOpenListener(req)

                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            launch { textListener(frame.readText()) }
                        }

                        is Frame.Binary -> {
                            launch { bytesListener(frame.readBytes()) }
                        }

                        else -> {}
                    }
                }
            } finally {
                // call when close
                req.onCloseListener(req)
            }
        }
    }

    fun close() {
        onCloseListener(this)
        client.close()
        session = null
    }

    suspend fun sendText(text: String) {
        checkNotNull(session).let {
            if (it.isActive) {
                it.send(text)
            }
        }
    }

    suspend fun sendBinary(content: ByteArray) {
        checkNotNull(session).let {
            if (it.isActive) {
                it.send(content)
            }
        }
    }
}