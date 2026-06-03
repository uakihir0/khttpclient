package work.socialhub.khttpclient.websocket

expect class WebsocketRequest() {

    var schema: String
    var host: String?
    var path: String?
    var port: Int?
    var url: String?

    var accept: String?
    var userAgent: String?
    val header: MutableMap<String, String>

    var textListener: suspend (String) -> Unit
    var bytesListener: suspend (ByteArray) -> Unit

    var onOpenListener: (WebsocketRequest) -> Unit
    var onCloseListener: (WebsocketRequest) -> Unit
    var onErrorListener: (Exception) -> Unit

    // Basic
    fun schema(schema: String): WebsocketRequest
    fun host(host: String): WebsocketRequest
    fun path(path: String): WebsocketRequest
    fun port(port: Int?): WebsocketRequest
    fun url(url: String?): WebsocketRequest

    // Listener
    fun textListener(listener: suspend (String) -> Unit): WebsocketRequest
    fun bytesListener(listener: suspend (ByteArray) -> Unit): WebsocketRequest
    fun onOpenListener(listener: (WebsocketRequest) -> Unit): WebsocketRequest
    fun onCloseListener(listener: (WebsocketRequest) -> Unit): WebsocketRequest
    fun onErrorListener(listener: (Exception) -> Unit): WebsocketRequest

    // Headers
    fun accept(accept: String): WebsocketRequest
    fun userAgent(userAgent: String): WebsocketRequest
    fun header(key: String, value: String): WebsocketRequest

    // Start
    suspend fun open(): WebsocketRequest
    suspend fun openPost(): WebsocketRequest

    fun close()

    suspend fun sendText(text: String)
    suspend fun sendBinary(content: ByteArray)
}
