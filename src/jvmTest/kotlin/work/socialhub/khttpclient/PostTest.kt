package work.socialhub.khttpclient

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import work.socialhub.khttpclient.httpbin.PostResponse

class PostTest {

    @Test
    fun testSimplePost() = runBlocking {
        val response = HttpRequest()
            .url("https://httpbin.org/post")
            .post()

        println(response.stringBody)
    }

    @Test
    fun testPostWithQuery() = runBlocking {
        val response = HttpRequest()
            .url("https://httpbin.org/post")
            .query("key1", "value1")
            .query("key2", "value2")
            .post()

        println(response.stringBody)

        val bin = response.typedBody<PostResponse>()
        assert(bin.args["key1"] == "value1")
        assert(bin.args["key2"] == "value2")
    }

    @Test
    fun testPostWithHeader() = runBlocking {
        val response = HttpRequest()
            .url("https://httpbin.org/post")
            .header("Header1", "value1")
            .header("Header2", "value2")
            .post()

        println(response.stringBody)

        val bin = response.typedBody<PostResponse>()
        assert(bin.headers["Header1"] == "value1")
        assert(bin.headers["Header2"] == "value2")
    }

    @Test
    fun testPostWithParams() = runBlocking {
        val response = HttpRequest()
            .url("https://httpbin.org/post")
            .param("key1", "value1")
            .param("key2", "value2")
            .post()

        println(response.stringBody)

        val bin = response.typedBody<PostResponse>()
        assert(bin.form["key1"] == "value1")
        assert(bin.form["key2"] == "value2")
    }

    @Test
    fun testPostWithJson() = runBlocking {
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
        assert(bin.json?.get("key1") == "value1")
        assert(bin.json?.get("key2") == "value2")
    }

    @Test
    fun testPostWithFile() = runBlocking {
        val response = HttpRequest()
            .url("https://httpbin.org/post")
            .file("file", "test.txt", "content".toByteArray())
            .post()

        println(response.stringBody)

        val bin = response.typedBody<PostResponse>()
        assert(bin.files["file"] == "content")
    }

    @Test
    fun testPostWithImage() = runBlocking {
        val stream = javaClass.getResourceAsStream("/image/icon.png")

        val response = HttpRequest()
            .url("https://httpbin.org/post")
            .file("file", "icon.png", stream.readBytes())
            .post()

        println(response.stringBody)
    }

    @Test
    fun testPostWithFileAndParams() = runBlocking {
        val response = HttpRequest()
            .url("https://httpbin.org/post")
            .param("key", "value")
            .file("file", "test.txt", "content".toByteArray())
            .post()

        println(response.stringBody)

        val bin = response.typedBody<PostResponse>()
        assert(bin.form["key"] == "value")
        assert(bin.files["file"] == "content")
    }
}