# kHttpClient

![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.repsy.io%2Fmvn%2Fuakihir0%2Fpublic%2Fwork%2Fsocialhub%2Fkhttpclient%2Fmaven-metadata.xml&label=repsy%20(maven))

**このライブラリは [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) でシンプルに Http リクエストを行うためのライブラリです。**
Ktor Client を依存関係に持っており、それをラップする形で実装されています。そのため、本ライブラリは、
Kotlin Multiplatform かつ Ktor Client がサポートしているプラットフォームであれば利用可能です。

## エンジン選択

Ktor Client では、各プラットフォーム向けに幾つかの HttpClient の実装であるエンジンを提供していますが、
本ライブラリでは各プラットフォーム向けに以下のエンジンを選択しています。エンジンを切り替えたい場合は、
依存関係から削除した上で、別のものを追加してください。ただし、その場合の動作については保証できません。

* Apple: Darwin
* JVM: OkHttp

## 使い方

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

クエリパラメータを付与して GET リクエストを行う場合は、以下のように記述します。

```kotlin
 val response = HttpRequest()
    .host("https://httpbin.org/")
    .path("get")
    .query("key1", "value1")
    .query("key2", "value2")
    .get()
```

### POST

From データとファイルを付与して POST リクエストを行う場合は、以下のように記述します。

```kotlin
val response = HttpRequest()
    .host("https://httpbin.org/")
    .path("post")
    .param("key", "value")
    .file("file", "test.txt", "content".toByteArray())
    .post()
```

JSON 文字列をボディーとして付与して POST リクエストを行う場合は、以下のように記述します。

```kotlin
val response = HttpRequest()
    .host("https://httpbin.org/")
    .path("post")
    .json("""{"key": "value"}""")
    .post()
```

細かい使用方法についてはテストコードを参照して下さい。

## ライセンス

MIT License

## 作者

[Akihiro Urushihara](https://github.com/uakihir0)