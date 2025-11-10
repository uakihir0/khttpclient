package work.socialhub.khttpclient.internal

internal actual fun proxyUrlFromEnv(): String? {
    val httpsProxy = System.getenv("HTTPS_PROXY") ?: System.getenv("https_proxy")
    val httpProxy = System.getenv("HTTP_PROXY") ?: System.getenv("http_proxy")
    return httpsProxy ?: httpProxy
}
