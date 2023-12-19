package work.socialhub.khttpclient

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import work.socialhub.khttpclient.httpbin.GetResponse

class GetTest {

    @Test
    fun testSimpleGet() = runBlocking {
        val response = HttpRequest()
            .host("https://httpbin.org/")
            .path("get")
            .get()

        println(response.stringBody())
    }

    @Test
    fun testGetWithQuest() = runBlocking {
        val response = HttpRequest()
            .host("https://httpbin.org/")
            .path("get")
            .query("key1", "value1")
            .query("key2", "value2")
            .get()

        println(response.stringBody())

        val bin = response.typedBody<GetResponse>()
        assert(bin.args["key1"] == "value1")
        assert(bin.args["key2"] == "value2")
    }

    @Test
    fun testGetWithHeader() = runBlocking {
        val response = HttpRequest()
            .host("https://httpbin.org/")
            .path("get")
            .header("Header1", "value1")
            .header("Header2", "value2")
            .get()

        println(response.stringBody())

        val bin = response.typedBody<GetResponse>()
        assert(bin.headers["Header1"] == "value1")
        assert(bin.headers["Header2"] == "value2")
    }
}