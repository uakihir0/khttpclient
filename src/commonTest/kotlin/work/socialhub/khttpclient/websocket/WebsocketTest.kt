package work.socialhub.khttpclient.websocket

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class WebsocketTest {

    @Test
    fun testSimpleWebsocket() = runTest {
        var ended = false

        val request = WebsocketRequest()
            .url("wss://echo.websocket.org")
            .onCloseListener { println(">>> close") }
            .onOpenListener { req ->
                println(">>> open")
                launch {
                    repeat(10) {
                        req.sendText(">>> send: $it")
                        delay(1000) // no mean
                    }
                }
            }
            .textListener {
                println(">>> text: [$it]")
                println(it)
                if (it == ">>> send: 9") {
                    println("??")
                    ended = true
                }
            }

        launch { request.open() }
        // default timeout is 60s.
        while (!ended) {
            delay(1000) // no mean
        }
        request.close()
    }

    @Test
    fun testBlueskyWebsocket() = runTest {

        val request = WebsocketRequest()
            .url("wss://bsky.network/xrpc/com.atproto.sync.subscribeRepos")
            .onCloseListener { println(">>> close") }
            .onOpenListener { println(">>> open") }
            .bytesListener {
                println(">>> bytes comes. size: ${it.size}")
                delay(1000)
            }
            .textListener {
                println(">>> text comes. size: ${it.length}")
                delay(1000)
            }

        launch { request.open() }.let {
            delay(10000)
            it.cancel()
            request.close()
        }
    }
}