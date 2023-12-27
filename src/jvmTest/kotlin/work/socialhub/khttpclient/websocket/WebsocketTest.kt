package work.socialhub.khttpclient.websocket

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class WebsocketTest {

    @Test
    fun testSimpleWebsocket() {

        val request = WebsocketRequest()
            .url("wss://echo.websocket.org")
            .textListener { println(">>> text: $it") }
            .onCloseListener { println(">>> close") }
            .onOpenListener { req ->
                println(">>> open")
                Thread {
                    repeat(10) {
                        req.sendText(">>> send: $it")
                        Thread.sleep(100)
                    }
                }.start()
            }

        Thread { runBlocking { request.open() } }.start()
        Thread.sleep(10000)
        request.close()
    }

    @Test
    fun testBlueskyWebsocket() = runBlocking {

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
