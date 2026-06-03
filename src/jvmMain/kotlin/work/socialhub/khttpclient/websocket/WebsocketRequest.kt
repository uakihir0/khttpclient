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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import work.socialhub.khttpclient.internal.applySystemProxy
import kotlin.time.Duration.Companion.milliseconds

actual class WebsocketRequest {

    actual var schema: String = "ws"
    actual var host: String? = null
    actual var path: String? = null
    actual var port: Int? = null
    actual var url: String? = null

    actual var accept: String? = null
    actual var userAgent: String? = "kHttpClient/1.0"
    actual val header = mutableMapOf<String, String>()

    actual var textListener: suspend (String) -> Unit = {}
    actual var bytesListener: suspend (ByteArray) -> Unit = {}

    actual var onOpenListener: (WebsocketRequest) -> Unit = {}
    actual var onCloseListener: (WebsocketRequest) -> Unit = {}
    actual var onErrorListener: (Exception) -> Unit = {}

    // Basic
    actual fun schema(schema: String) = also { it.schema = schema }
    actual fun host(host: String) = also { it.host = host }
    actual fun path(path: String) = also { it.path = path }
    actual fun port(port: Int?) = also { it.port = port }
    actual fun url(url: String?) = also { it.url = url }

    // Listener
    actual fun textListener(listener: suspend (String) -> Unit) = also { it.textListener = listener }
    actual fun bytesListener(listener: suspend (ByteArray) -> Unit) = also { it.bytesListener = listener }
    actual fun onOpenListener(listener: (WebsocketRequest) -> Unit) = also { it.onOpenListener = listener }
    actual fun onCloseListener(listener: (WebsocketRequest) -> Unit) = also { it.onCloseListener = listener }
    actual fun onErrorListener(listener: (Exception) -> Unit) = also { it.onErrorListener = listener }

    // Headers
    actual fun accept(accept: String) = also { it.accept = accept }
    actual fun userAgent(userAgent: String) = also { it.userAgent = userAgent }
    actual fun header(key: String, value: String) = also { it.header[key] = value }

    private val client = HttpClient {
        applySystemProxy()
        install(WebSockets) {
            pingInterval = 20_000.milliseconds
        }
    }

    private var session: DefaultClientWebSocketSession? = null

    // Start
    actual suspend fun open() = start(HttpMethod.Get)
    actual suspend fun openPost() = start(HttpMethod.Post)

    private suspend fun start(method: HttpMethod) = also {
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
            } catch (e: Exception) {
                req.onErrorListener(e)
            } finally {
                req.onCloseListener(req)
            }
        }
    }

    actual fun close() {
        onCloseListener(this)
        client.coroutineContext.cancel()
        client.close()
        session = null
    }

    actual suspend fun sendText(text: String) {
        checkNotNull(session).let {
            if (it.isActive) {
                it.send(text)
            }
        }
    }

    actual suspend fun sendBinary(content: ByteArray) {
        checkNotNull(session).let {
            if (it.isActive) {
                it.send(content)
            }
        }
    }
}
