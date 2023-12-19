package work.socialhub.khttpclient.httpbin

import kotlinx.serialization.Serializable

@Serializable
data class GetResponse(
    val args: Map<String, String>,
    val headers: Map<String, String>,
    val origin: String,
    val url: String,
)