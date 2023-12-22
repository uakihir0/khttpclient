package work.socialhub.khttpclient

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.content.*
import work.socialhub.khttpclient.HttpParameter.Type

class HttpRequest {

    var host: String? = null
    var path: String? = null
    var port: Int? = null
    var accept: String? = null
    var userAgent: String? = "kHttpClient/1.0"

    val params = mutableListOf<HttpParameter>()
    val header = mutableMapOf<String, String>()

    // Basic
    fun host(host: String) = also { it.host = host }
    fun path(path: String) = also { it.path = path }
    fun port(port: Int?) = also { it.port = port }

    // Headers
    fun accept(accept: String) = also { it.accept = accept }
    fun userAgent(userAgent: String) = also { it.userAgent = userAgent }
    fun header(key: String, value: String) = also { it.header[key] = value }

    // Parameters
    fun query(key: String, value: Any) = also {
        params.add(HttpParameter.query(key, value.toString()))
    }

    fun queries(queries: Map<String, Any>) = also {
        queries.forEach { (k, v) -> query(k, v) }
    }

    fun param(key: String, value: Any) = also {
        params.add(HttpParameter.param(key, value.toString()))
    }

    fun params(params: Map<String, Any>) = also {
        params.forEach { (k, v) -> param(k, v) }
    }

    fun file(key: String, fileName: String, fileBody: ByteArray) = also {
        params.add(HttpParameter.file(key, fileName, fileBody))
    }

    fun json(json: String) = also {
        params.add(HttpParameter.json(json))
    }

    fun pathValue(key: String, value: String) = also {
        it.path = it.path!!.replace("{$key}".toRegex(), value)
    }

    // Methods
    suspend fun get() = proceed(HttpMethod.Get)
    suspend fun post() = proceed(HttpMethod.Post)
    suspend fun put() = proceed(HttpMethod.Put)
    suspend fun delete() = proceed(HttpMethod.Delete)
    suspend fun patch() = proceed(HttpMethod.Patch)

    // Request
    private suspend fun proceed(method: HttpMethod): HttpResponse {
        val req = this
        val client = HttpClient()

        accept?.let { header["Accept"] = it }
        userAgent?.let { header["User-Agent"] = it }

        return HttpResponse.from(
            client.request {
                this.method = method
                this.url.takeFrom(req.url()!!)
                req.port?.let { this.url.port = it }

                this.headers {
                    req.header.forEach { (k, v) ->
                        append(k, v)
                    }
                }

                if ((req.params.size == 1) &&
                    (req.params.first().type == Type.JSON)
                ) {
                    // json
                    setBody(
                        ByteArrayContent(
                            bytes = req.params.first().fileBody!!,
                            contentType = ContentType.Application.Json
                        )
                    )

                } else {

                    when (method) {
                        HttpMethod.Get -> {
                            req.params.forEach { p ->
                                when (p.type) {
                                    Type.QUERY -> {
                                        url.parameters.append(p.key, p.value!!)
                                    }

                                    else -> {
                                        // TODO: error
                                    }
                                }
                            }
                        }

                        else -> {
                            val queries = req.params.filter { it.type == Type.QUERY }
                            val params = req.params.filter { it.type == Type.PARAM }
                            val files = req.params.filter { it.type == Type.FILE }

                            // queries
                            if (queries.isNotEmpty()) {
                                queries.forEach { p ->
                                    url.parameters.append(p.key, p.value!!)
                                }
                            }

                            if (params.isNotEmpty() || files.isNotEmpty()) {
                                contentType(ContentType.MultiPart.FormData)
                                setBody(
                                    MultiPartFormDataContent(
                                        formData {

                                            // params
                                            params.forEach { p ->
                                                append(p.key, p.value!!)
                                            }

                                            // files
                                            files.forEach { p ->
                                                append(
                                                    p.key,
                                                    p.fileBody!!,
                                                    Headers.build {
                                                        append(
                                                            HttpHeaders.ContentType,
                                                            p.fileContentType()
                                                        )
                                                        append(
                                                            HttpHeaders.ContentDisposition,
                                                            "filename=${p.fileName}"
                                                        )
                                                    })
                                            }
                                        }
                                    )
                                )
                            }
                        }
                    }
                }
            }
        )
    }

    private fun url(): String? {
        var url = host
        if (path != null) {
            url += path
        }
        return url
    }
}
