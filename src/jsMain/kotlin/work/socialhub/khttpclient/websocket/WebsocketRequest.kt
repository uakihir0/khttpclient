package work.socialhub.khttpclient.websocket

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.w3c.dom.WebSocket
import org.w3c.dom.events.Event
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import kotlin.coroutines.coroutineContext
import kotlinx.coroutines.Job

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

    private var ws: WebSocket? = null
    private var closed = CompletableDeferred<Unit>()
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    actual suspend fun open(): WebsocketRequest = start()
    actual suspend fun openPost(): WebsocketRequest = start()

    private suspend fun start(): WebsocketRequest {
        val wsUrl = buildUrl()
        val opened = CompletableDeferred<Unit>()
        closed = CompletableDeferred()

        val socket = WebSocket(wsUrl)
        socket.binaryType = "arraybuffer".unsafeCast<org.w3c.dom.BinaryType>()
        ws = socket

        socket.onopen = {
            onOpenListener(this)
            opened.complete(Unit)
        }

        socket.onclose = {
            if (!opened.isCompleted) {
                opened.completeExceptionally(RuntimeException("WebSocket closed before open"))
            }
            onCloseListener(this)
            closed.complete(Unit)
        }

        socket.onerror = { _: Event ->
            val ex = RuntimeException("WebSocket error")
            onErrorListener(ex)
            if (!opened.isCompleted) {
                opened.completeExceptionally(ex)
            }
        }

        socket.onmessage = { event ->
            val data = event.data
            when (data) {
                is String -> {
                    scope.launch { textListener(data) }
                }
                is ArrayBuffer -> {
                    val bytes = Int8Array(data).unsafeCast<ByteArray>()
                    scope.launch { bytesListener(bytes) }
                }
            }
        }

        // Cancel WebSocket when parent coroutine is cancelled
        coroutineContext[Job]?.invokeOnCompletion {
            ws?.close()
            scope.cancel()
        }

        opened.await()
        closed.await()
        return this
    }

    private fun buildUrl(): String {
        url?.let { return it }

        val s = if (schema == "ws" || schema == "wss") schema else "wss"
        val h = checkNotNull(host) { "host must be set" }
        val p = port?.let { ":$it" } ?: ""
        val pathPart = path?.let { if (it.startsWith("/")) it else "/$it" } ?: ""
        return "$s://$h$p$pathPart"
    }

    actual fun close() {
        ws?.close()
        ws = null
        closed.complete(Unit)
        scope.cancel()
    }

    actual suspend fun sendText(text: String) {
        checkNotNull(ws) { "WebSocket is not connected" }.send(text)
    }

    actual suspend fun sendBinary(content: ByteArray) {
        checkNotNull(ws) { "WebSocket is not connected" }.send(content.unsafeCast<org.khronos.webgl.ArrayBufferView>())
    }
}
