package work.socialhub.khttpclient.httpbin

data class PostResponse(
    val args: Map<String, String>,
    val data: String,
    val files: Map<String, String>,
    val form: Map<String, String>,
    val headers: Map<String, String>,
    val json: Map<String, String>?,
    val origin: String,
    val url: String,
)