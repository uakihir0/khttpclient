package work.socialhub.khttpclient

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.http.ContentType.*
import io.ktor.http.content.*
import work.socialhub.khttpclient.HttpParameter.Type

class HttpRequest {

    var schema: String = "https"
    var host: String? = null
    var path: String? = null
    var port: Int? = null
    var url: String? = null

    var accept: String? = null
    var userAgent: String? = "kHttpClient/1.0"

    val params = mutableListOf<HttpParameter>()
    val header = mutableMapOf<String, String>()

    var forceMultipartFormData: Boolean = false
    var forceApplicationFormUrlEncoded: Boolean = false
    var followRedirect: Boolean = true

    // Basic
    fun schema(schema: String) = also { it.schema = schema }
    fun host(host: String) = also { it.host = host }
    fun path(path: String) = also { it.path = path }
    fun port(port: Int?) = also { it.port = port }
    fun url(url: String) = also { it.url = url }

    // Headers
    fun accept(accept: String) = also { it.accept = accept }
    fun userAgent(userAgent: String) = also { it.userAgent = userAgent }
    fun header(key: String, value: String) = also { it.header[key] = value }

    // Options
    fun forceMultipartFormData(forceMultipartFormData: Boolean) =
        also { it.forceMultipartFormData = forceMultipartFormData }

    fun forceApplicationFormUrlEncoded(forceApplicationFormUrlEncoded: Boolean) =
        also { it.forceApplicationFormUrlEncoded = forceApplicationFormUrlEncoded }

    fun followRedirect(followRedirect: Boolean) = also { it.followRedirect = followRedirect }

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
        it.path = it.path?.replace("{$key}".toRegex(), value)
        it.url = it.url?.replace("{$key}".toRegex(), value)
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
        val client = HttpClient {
            this.followRedirects = req.followRedirect
        }

        accept?.let { header["Accept"] = it }
        userAgent?.let { header["User-Agent"] = it }

        return HttpResponse.from(
            client.request {
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

                if (!forceMultipartFormData &&
                    !forceApplicationFormUrlEncoded &&
                    (req.params.size == 1) &&
                    (canSendOnly(req.params.first()))
                ) {
                    val param = req.params.first()
                    setBody(
                        ByteArrayContent(
                            bytes = param.fileBody!!,
                            contentType = param.fileContentType()
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
                                        throw IllegalStateException(
                                            "Request Body is not supported in the GET method."
                                        )
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

                                if (forceApplicationFormUrlEncoded) {
                                    if (files.isNotEmpty()) {
                                        throw IllegalStateException(
                                            "ApplicationFormUrlEncoded cannot send files."
                                        )
                                    }

                                    // Content-Type: application/x-www-form-urlencoded
                                    contentType(ContentType.Application.FormUrlEncoded)
                                    setBody(
                                        FormDataContent(
                                            Parameters.build {
                                                params.forEach { p ->
                                                    append(p.key, p.value!!)
                                                }
                                            }
                                        )
                                    )

                                } else {

                                    // Content-Type: multipart/form-data
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
            }
        )
    }

    private fun canSendOnly(param: HttpParameter): Boolean {
        if (param.type == Type.JSON) return true

        return when (param.fileContentType()) {
            Application.Json -> true
            Image.JPEG -> true
            Image.PNG -> true
            Image.GIF -> true
            Video.MPEG -> true
            Video.MP4 -> true
            Video.QuickTime -> true
            ContentType("video", "webm") -> true

            else -> false
        }
    }

    @Deprecated(
        "migrate to forceMultipartFormData()",
        ReplaceWith("forceMultipartFormData(value)")
    )
    fun forceMultipart(forceMultipart: Boolean) = also {
        it.forceMultipartFormData = forceMultipart
    }
}
