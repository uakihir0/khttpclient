> [日本語](./docs/README_ja.md)

# kHttpClient

![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.repsy.io%2Fmvn%2Fuakihir0%2Fpublic%2Fwork%2Fsocialhub%2Fkhttpclient%2Fmaven-metadata.xml&label=repsy%20(maven))

**This library is designed for making simple Http requests in [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html).**
It relies on Ktor Client and is implemented as a wrapper for it. Therefore, 
this library is available for use in Kotlin Multiplatform as long as the platform is supported by Ktor Client.

## Engine Selection

Ktor Client provides several implementations of HttpClient engines for each platform. 
In this library, the following engines are selected for each platform. 
If you want to switch the engine, remove it from the dependencies and add another one. 
However, the behavior in that case cannot be guaranteed.

* Apple: Darwin
* JVM: OkHttp

## Usage

```kotlin:build.gradle.kts
repositories {
    mavenCentral()
+   maven { url = uri("https://repo.repsy.io/mvn/uakihir0/public") }
}

dependencies {
+   implementation("work.socialhub:khttpclient:0.0.1-SNAPSHOT")
}
```

### GET

To perform a GET request with query parameters, you can write it as follows:

```kotlin
 val response = HttpRequest()
    .host("https://httpbin.org/")
    .path("get")
    .query("key1", "value1")
    .query("key2", "value2")
    .get()
```

### POST

To perform a POST request with form data and files, you can write it as follows:

```kotlin
val response = HttpRequest()
    .host("https://httpbin.org/")
    .path("post")
    .param("key", "value")
    .file("file", "test.txt", "content".toByteArray())
    .post()
```

To perform a POST request with a JSON string as the body, you can write it as follows:

```kotlin
val response = HttpRequest()
    .host("https://httpbin.org/")
    .path("post")
    .json("""{"key": "value"}""")
    .post()
```

For detailed usage, refer to the test code.

## License

MIT License

## Author

[Akihiro Urushihara](https://github.com/uakihir0)