package work.socialhub.khttpclient.httpbin

data class GetResponse(
    val args: Map<String, String>,
    val headers: Map<String, String>,
    val origin: String,
    val url: String,
)