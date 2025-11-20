package work.socialhub.khttpclient

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class SSLTest {

    @Test
    fun testSkipSSL() = runTest {
        val response = HttpRequest()
            .url("https://bsky.social/xrpc/com.atproto.identity.resolveHandle?handle=uakihir0.com")
            .skipSSLValidation(true)
            .get()

        println(response.stringBody)
        assertTrue(response.stringBody.contains("did:plc:bwdof2anluuf5wmfy2upgulw"))
    }
}

