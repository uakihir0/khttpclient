package work.socialhub.khttpclient.internal

import io.ktor.client.HttpClientConfig

internal actual fun proxyUrlFromEnv(): String? = null

internal actual fun HttpClientConfig<*>.applySkipSSLValidation() {
    throw IllegalStateException("Skip SSL Validation is not supported on Native target.")
}
