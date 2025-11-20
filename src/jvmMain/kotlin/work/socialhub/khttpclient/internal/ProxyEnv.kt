package work.socialhub.khttpclient.internal

import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttpConfig
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

internal actual fun proxyUrlFromEnv(): String? {
    val httpsProxy = System.getenv("HTTPS_PROXY") ?: System.getenv("https_proxy")
    val httpProxy = System.getenv("HTTP_PROXY") ?: System.getenv("http_proxy")
    return httpsProxy ?: httpProxy
}

internal actual fun HttpClientConfig<*>.applySkipSSLValidation() {
    engine {
        if (this is OkHttpConfig) {
            config {
                val trustAllCerts = object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                }

                val sslContext = SSLContext.getInstance("SSL")
                sslContext.init(null, arrayOf(trustAllCerts), java.security.SecureRandom())

                sslSocketFactory(sslContext.socketFactory, trustAllCerts)
                hostnameVerifier { _, _ -> true }
            }
        }
    }
}
