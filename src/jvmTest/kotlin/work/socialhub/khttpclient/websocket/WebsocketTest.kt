package work.socialhub.khttpclient.websocket

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

        Thread { runBlocking { request.startGet() } }.start()
        Thread.sleep(10000)
        request.close()
    }
}