package work.socialhub.khttpclient

import io.ktor.http.*
import io.ktor.utils.io.core.*

class HttpParameter(
    val type: Type,
    val key: String,
    val value: String? = null,
    val fileName: String? = null,
    val fileBody: ByteArray? = null
) {

    enum class Type {
        QUERY, PARAM, FILE, JSON
    }

    companion object {

        fun query(key: String, value: String) =
            HttpParameter(Type.QUERY, key, value)

        fun param(key: String, value: String) =
            HttpParameter(Type.PARAM, key, value)

        fun file(key: String, fileName: String, fileBody: ByteArray) =
            HttpParameter(
                type = Type.FILE,
                key = key,
                value = null,
                fileName = fileName,
                fileBody = fileBody
            )

        fun json(json: String) =
            HttpParameter(
                type = Type.JSON,
                key = "json",
                value = null,
                fileName = "file.json",
                fileBody = json.toByteArray()
            )
    }

    fun fileExtension(): String? {
        return fileName?.split(".")?.lastOrNull()
    }

    fun fileContentType(): ContentType {
        when (fileExtension()) {

            // text
            "txt" -> return ContentType.Text.Plain
            "html" -> return ContentType.Text.Html
            "css" -> return ContentType.Text.CSS
            "csv" -> return ContentType.Text.CSV

            // image
            "jpg", "jpeg" -> return ContentType.Image.JPEG
            "png" -> return ContentType.Image.PNG
            "gif" -> return ContentType.Image.GIF
            "svg" -> return ContentType.Image.SVG

            // video
            "mpeg" -> return ContentType.Video.MPEG
            "mp4" -> return ContentType.Video.MP4

            // application
            "json" -> return ContentType.Application.Json
            "xml" -> return ContentType.Application.Xml
            "zip" -> return ContentType.Application.Zip
            "pdf" -> return ContentType.Application.Pdf
        }

        // TODO: other content type
        return ContentType.Application.OctetStream
    }
}