import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.konan.target.HostManager

plugins {
    kotlin("multiplatform") version "2.2.21"
    kotlin("plugin.serialization") version "2.2.21"

    id("maven-publish")
    id("signing")

    id("org.jetbrains.dokka") version "2.1.0"
    id("io.github.gradle-nexus.publish-plugin") version "2.0.0"
    id("com.vanniktech.maven.publish") version "0.34.0"
    id("me.qoomon.git-versioning") version "6.4.4"
}

group = "work.socialhub"
version = "0.0.6-SNAPSHOT"

gitVersioning.apply {
    refs {
        considerTagsOnBranches = true
        tag("v(?<version>.*)") {
            version = "\${ref.version}"
        }
    }
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(11)
    jvm { withJava() }
    js(IR) {
        binaries.library()
        browser()
    }

    if (HostManager.hostIsMac) {
        iosX64()
        iosArm64()
        iosSimulatorArm64()
        macosX64()
        macosArm64()
    }

    linuxX64()
    mingwX64()

    sourceSets {
        val ktorVersion = "3.3.2"

        commonMain.dependencies {
            implementation("io.ktor:ktor-client-core:$ktorVersion")
            implementation("io.ktor:ktor-client-websockets:$ktorVersion")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
        }

        // for Apple platform
        appleMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:$ktorVersion")
        }

        // for JVM
        jvmMain.dependencies {
            implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
        }

        // for JS
        jsMain.dependencies {
            implementation("io.ktor:ktor-client-js:$ktorVersion")
        }

        // for Linux
        linuxMain.dependencies {
            implementation("io.ktor:ktor-client-curl:${ktorVersion}")
        }

        // for Windows
        mingwMain.dependencies {
            implementation("io.ktor:ktor-client-winhttp:${ktorVersion}")
        }

        // for test
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
        }
    }
}

tasks.wrapper {
    gradleVersion = "8.10.2"
    distributionType = Wrapper.DistributionType.ALL
}

tasks.named<Test>("jvmTest") {
    useJUnitPlatform()
}

publishing {
    repositories {
        maven {
            url = uri("https://repo.repsy.io/mvn/uakihir0/public")
            credentials {
                username = System.getenv("USERNAME")
                password = System.getenv("PASSWORD")
            }
        }
    }

    // Configure all publications
    publications.withType<MavenPublication> {

        // Provide artifacts information required by Maven Central
        pom {
            name.set("khttpclient")
            description.set("Kotlin multiplatform simple http request library.")
            url.set("https://github.com/uakihir0/khttpclient")

            licenses {
                license {
                    name.set("MIT License")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }

            developers {
                developer {
                    id.set("uakihir0")
                    name.set("URUSHIHARA Akihiro")
                    email.set("a.urusihara@gmail.com")
                }
            }

            scm {
                url.set("https://github.com/uakihir0/khttpclient")
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            // only for users registered in Sonatype after 24 Feb 2021
            nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
            snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
        }
    }
}

mavenPublishing {
    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Dokka("dokkaHtml")
        )
    )

    publishToMavenCentral(
        host = SonatypeHost.CENTRAL_PORTAL,
        automaticRelease = true,
    )

    if (project.hasProperty("mavenCentralUsername") ||
        System.getenv("ORG_GRADLE_PROJECT_mavenCentralUsername") != null
    ) signAllPublications()
}

signing {
    if (project.hasProperty("mavenCentralUsername") ||
        System.getenv("ORG_GRADLE_PROJECT_mavenCentralUsername") != null
    ) useGpgCmd()
}

