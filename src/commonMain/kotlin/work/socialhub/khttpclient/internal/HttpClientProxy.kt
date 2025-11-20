package work.socialhub.khttpclient.internal

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.ProxyBuilder
import io.ktor.http.Url

internal expect fun proxyUrlFromEnv(): String?

internal fun HttpClientConfig<*>.applySystemProxy() {
    val proxyUrl = proxyUrlFromEnv()
    if (!proxyUrl.isNullOrBlank()) {
        engine {
            proxy = ProxyBuilder.http(Url(proxyUrl))
        }
    }
}

internal expect fun HttpClientConfig<*>.applySkipSSLValidation()