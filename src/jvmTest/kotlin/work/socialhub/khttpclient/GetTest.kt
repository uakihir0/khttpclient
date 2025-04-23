package work.socialhub.khttpclient

import kotlinx.coroutines.runBlocking
import work.socialhub.khttpclient.httpbin.GetResponse
import kotlin.test.Test

class GetTest {

    @Test
    fun testSimpleGetSimple() = runBlocking {
        val response = HttpRequest()
            .host("httpbin.org")
            .path("get")
            .get()

        println(response.stringBody)
    }

    @Test
    fun testSimpleGet() = runBlocking {
        val response = HttpRequest()
            .url("https://httpbin.org/get")
            .get()

        println(response.stringBody)
    }

    @Test
    fun testGetWithQuest() = runBlocking {
        val response = HttpRequest()
            .url("https://httpbin.org/get")
            .query("key1", "value1")
            .query("key2", "value2")
            .get()

        println(response.stringBody)

        val bin = response.typedBody<GetResponse>()
        assert(bin.args["key1"] == "value1")
        assert(bin.args["key2"] == "value2")
    }

    @Test
    fun testGetWithHeader() = runBlocking {
        val response = HttpRequest()
            .url("https://httpbin.org/get")
            .header("Header1", "value1")
            .header("Header2", "value2")
            .get()

        println(response.stringBody)

        val bin = response.typedBody<GetResponse>()
        assert(bin.headers["Header1"] == "value1")
        assert(bin.headers["Header2"] == "value2")
    }
}