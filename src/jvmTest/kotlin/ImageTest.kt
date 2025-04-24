import java.util.Base64
import kotlin.test.Test

class ImageTest {

    @Test
    fun testImageString()  {
        val stream = javaClass.getResourceAsStream("/image/icon.png")!!
        val base64String = Base64.getEncoder().encodeToString(stream.readBytes())

        println("Base64 encoded string:")
        println(base64String)
    }
}