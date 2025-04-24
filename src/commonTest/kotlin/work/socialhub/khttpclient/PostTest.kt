package work.socialhub.khttpclient

import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import work.socialhub.khttpclient.httpbin.PostResponse
import work.socialhub.khttpclient.resource.GetImageString
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.test.Test
import kotlin.test.assertTrue

@OptIn(ExperimentalEncodingApi::class)
class PostTest {

    @Test
    fun testSimplePost() = runTest {
        val response = HttpRequest()
            .url("https://httpbin.org/post")
            .post()

        println(response.stringBody)

        val bin = response.typedBody<PostResponse>()
        assertTrue(bin.url == "https://httpbin.org/post")
    }

    @Test
    fun testPostWithQuery() = runTest {
        val response = HttpRequest()
            .url("https://httpbin.org/post")
            .query("key1", "value1")
            .query("key2", "value2")
            .post()

        println(response.stringBody)

        val bin = response.typedBody<PostResponse>()
        assertTrue(bin.args["key1"] == "value1")
        assertTrue(bin.args["key2"] == "value2")
    }

    @Test
    fun testPostWithHeader() = runTest {
        val response = HttpRequest()
            .url("https://httpbin.org/post")
            .header("Header1", "value1")
            .header("Header2", "value2")
            .post()

        println(response.stringBody)

        val bin = response.typedBody<PostResponse>()
        assertTrue(bin.headers["Header1"] == "value1")
        assertTrue(bin.headers["Header2"] == "value2")
    }

    @Test
    fun testPostWithParams() = runTest {
        val response = HttpRequest()
            .url("https://httpbin.org/post")
            .param("key1", "value1")
            .param("key2", "value2")
            .post()

        println(response.stringBody)

        val bin = response.typedBody<PostResponse>()
        assertTrue(bin.form["key1"] == "value1")
        assertTrue(bin.form["key2"] == "value2")
    }

    @Test
    fun testPostWithJson() = runTest {
        val json = mapOf(
            "key1" to "value1",
            "key2" to "value2"
        )

        val response = HttpRequest()
            .url("https://httpbin.org/post")
            .json(Json.encodeToString(json))
            .post()

        println(response.stringBody)

        val bin = response.typedBody<PostResponse>()
        assertTrue(bin.json?.get("key1") == "value1")
        assertTrue(bin.json?.get("key2") == "value2")
    }

    @Test
    fun testPostWithFile() = runTest {
        val response = HttpRequest()
            .url("https://httpbin.org/post")
            .file("file", "test.txt", "content".toByteArray())
            .post()

        println(response.stringBody)

        val bin = response.typedBody<PostResponse>()
        assertTrue(bin.files["file"] == "content")
    }

    @Test
    fun testPostWithImage() = runTest {
        val bytes = Base64.decode(GetImageString())
        val response = HttpRequest()
            .url("https://httpbin.org/post")
            .file("file", "icon.png", bytes)
            .post()

        println(response.stringBody)
    }

    @Test
    fun testPostWithFileAndParams() = runTest {
        val response = HttpRequest()
            .url("https://httpbin.org/post")
            .param("key", "value")
            .file("file", "test.txt", "content".toByteArray())
            .post()

        println(response.stringBody)

        val bin = response.typedBody<PostResponse>()
        assertTrue(bin.form["key"] == "value")
        assertTrue(bin.files["file"] == "content")
    }
}