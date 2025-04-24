# kHttpClient

![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Frepo.repsy.io%2Fmvn%2Fuakihir0%2Fpublic%2Fwork%2Fsocialhub%2Fkhttpclient%2Fmaven-metadata.xml&link=https%3A%2F%2Frepo.repsy.io%2Fmvn%2Fuakihir0%2Fpublic%2Fwork%2Fsocialhub%2Fkhttpclient%2F)

![badge][badge-js]
![badge][badge-jvm]
![badge][badge-ios]
![badge][badge-mac]
![badge][badge-windows]
![badge][badge-linux]

**このライブラリは [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) でシンプルに Http リクエストを行うためのライブラリです。**
Ktor Client を依存関係に持っており、それをラップする形で実装されています。そのため、本ライブラリは、
Kotlin Multiplatform かつ Ktor Client がサポートしているプラットフォームであれば利用可能です。

## エンジン選択

Ktor Client では、各プラットフォーム向けに幾つかの HttpClient の実装であるエンジンを提供していますが、
本ライブラリでは各プラットフォーム向けに以下のエンジンを選択しています。エンジンを切り替えたい場合は、
依存関係から削除した上で、別のものを追加してください。ただし、その場合の動作については保証できません。

- Apple: Darwin
- JVM: OkHttp
- Windows: WinHttp
- Linux: Curl

Linux 環境下では Engine の仕様上 Websocket を使用することができません。Websocket を使用したい場合は、
依存関係を変更して、 curl を消して cio を加えてください。その場合一部 TLS に対応しておらず、アクセスできないサーバーが存在する可能性があります。
詳しいエンジンの詳細については、[Ktor 公式ページ](https://ktor.io/docs/client-engines.html)を参照してください。

## 使い方

### 安定版

```kotlin:build.gradle.kts
repositories {
    mavenCentral()
}

dependencies {
+   implementation("work.socialhub:khttpclient:0.0.4")
}
```

### 開発版

```kotlin:build.gradle.kts
repositories {
    mavenCentral()
+   maven { url = uri("https://repo.repsy.io/mvn/uakihir0/public") }
}

dependencies {
+   implementation("work.socialhub:khttpclient:0.0.5-SNAPSHOT")
}
```

### GET

クエリパラメータを付与して GET リクエストを行う場合は、以下のように記述します。

```kotlin
 val response = HttpRequest()
    .url("https://httpbin.org/get")
    .query("key1", "value1")
    .query("key2", "value2")
    .get()
```

### POST

From データとファイルを付与して POST リクエストを行う場合は、以下のように記述します。

```kotlin
val response = HttpRequest()
    .url("https://httpbin.org/post")
    .param("key", "value")
    .file("file", "test.txt", "content".toByteArray())
    .post()
```

JSON 文字列をボディーとして付与して POST リクエストを行う場合は、以下のように記述します。

```kotlin
val response = HttpRequest()
    .url("https://httpbin.org/post")
    .json("""{"key": "value"}""")
    .post()
```

細かい使用方法についてはテストコードを参照して下さい。

## ライセンス

MIT License

## 作者

[Akihiro Urushihara](https://github.com/uakihir0)


[badge-android]: http://img.shields.io/badge/-android-6EDB8D.svg
[badge-android-native]: http://img.shields.io/badge/support-[AndroidNative]-6EDB8D.svg
[badge-wearos]: http://img.shields.io/badge/-wearos-8ECDA0.svg
[badge-jvm]: http://img.shields.io/badge/-jvm-DB413D.svg
[badge-js]: http://img.shields.io/badge/-js-F8DB5D.svg
[badge-js-ir]: https://img.shields.io/badge/support-[IR]-AAC4E0.svg
[badge-nodejs]: https://img.shields.io/badge/-nodejs-68a063.svg
[badge-linux]: http://img.shields.io/badge/-linux-2D3F6C.svg
[badge-windows]: http://img.shields.io/badge/-windows-4D76CD.svg
[badge-wasm]: https://img.shields.io/badge/-wasm-624FE8.svg
[badge-apple-silicon]: http://img.shields.io/badge/support-[AppleSilicon]-43BBFF.svg
[badge-ios]: http://img.shields.io/badge/-ios-CDCDCD.svg
[badge-mac]: http://img.shields.io/badge/-macos-111111.svg
[badge-watchos]: http://img.shields.io/badge/-watchos-C0C0C0.svg
[badge-tvos]: http://img.shields.io/badge/-tvos-808080.svg