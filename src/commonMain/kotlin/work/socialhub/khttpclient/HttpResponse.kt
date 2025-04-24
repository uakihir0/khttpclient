package work.socialhub.khttpclient

import io.ktor.client.call.body
import io.ktor.util.toMap
import kotlinx.serialization.json.Json
import io.ktor.client.statement.HttpResponse as KtorHttpResponse

class HttpResponse(
    val status: Int,
    val headers: Map<String, List<String>>,
    val body: ByteArray,
) {

    companion object {

        suspend fun from(
            response: KtorHttpResponse
        ): HttpResponse {
            return HttpResponse(
                status = response.status.value,
                headers = response.headers.toMap(),
                body = response.body<ByteArray>(),
            )
        }

        // mapper settings
        val mapper = Json {
            ignoreUnknownKeys = true
        }
    }

    val stringBody by lazy {
        body.decodeToString()
    }

    inline fun <reified T> typedBody(
        json: Json = mapper
    ): T {
        return json.decodeFromString<T>(stringBody)
    }
}