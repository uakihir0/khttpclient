package work.socialhub.khttpclient

import kotlinx.coroutines.test.runTest
import work.socialhub.khttpclient.httpbin.GetResponse
import kotlin.test.Test
import kotlin.test.assertTrue

class GetTest {

    @Test
    fun testSimpleGetSimple() = runTest {
        val response = HttpRequest()
            .host("httpbin.org")
            .path("get")
            .get()

        println(response.stringBody)
    }

    @Test
    fun testSimpleGet() = runTest {
        val response = HttpRequest()
            .url("https://httpbin.org/get")
            .get()

        println(response.stringBody)
    }

    @Test
    fun testGetWithQuest() = runTest {
        val response = HttpRequest()
            .url("https://httpbin.org/get")
            .query("key1", "value1")
            .query("key2", "value2")
            .get()

        println(response.stringBody)

        val bin = response.typedBody<GetResponse>()
        assertTrue(bin.args["key1"] == "value1")
        assertTrue(bin.args["key2"] == "value2")
    }

    @Test
    fun testGetWithHeader() = runTest {
        val response = HttpRequest()
            .url("https://httpbin.org/get")
            .header("Header1", "value1")
            .header("Header2", "value2")
            .get()

        println(response.stringBody)

        val bin = response.typedBody<GetResponse>()
        assertTrue(bin.headers["Header1"] == "value1")
        assertTrue(bin.headers["Header2"] == "value2")
    }
}