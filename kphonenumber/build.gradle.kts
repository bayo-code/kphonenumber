import com.vanniktech.maven.publish.SonatypeHost
import java.util.*
import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    `maven-publish`
    alias(libs.plugins.maven.publish)
}

kotlin {
    jvmToolchain(11)

    androidTarget {
        compilations.all { kotlinOptions { jvmTarget = "1.8" } }
        publishAllLibraryVariants()
    }

    jvm("jvm")

    val xcf = XCFramework()
    listOf(
                    iosX64(),
                    iosArm64(),
                    iosSimulatorArm64(),
                    macosArm64(),
                    macosX64(),
                    watchosX64(),
                    watchosArm64(),
                    tvosArm64(),
                    tvosX64(),
                    tvosSimulatorArm64(),
                    watchosSimulatorArm64(),
            )
            .forEach {
                it.binaries.framework {
                    baseName = "kphonenumber"
                    xcf.add(this)
                    isStatic = true
                }
            }

    listOf(mingwX64(), linuxX64(), linuxArm64()).forEach {
        it.binaries.staticLib { baseName = "kphonenumber" }
    }

    sourceSets {
        commonMain.dependencies { implementation(libs.kotlinx.coroutines.core) }
        commonTest.dependencies { implementation(libs.kotlin.test) }
    }
}

android {
    namespace = "com.bayocode.kphonenumber"
    compileSdk = 34
    defaultConfig { minSdk = 24 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

fun loadEnv() {
    val properties = Properties()
    rootProject.file("local.properties").inputStream().use { properties.load(it) }

    properties.forEach { (key, value) -> ext[key.toString()] = value }
}

fun getToken(): String {
    val systemToken: String = System.getenv("GIT_TOKEN") ?: ext["GIT_TOKEN"].toString()
    return systemToken
}

loadEnv()

publishing {
    repositories.maven {
        name = "Gitea"
        url = uri("https://git.braincrunchlabs.tech/api/packages/bayo-code/maven")

        credentials(HttpHeaderCredentials::class) {
            name = "Authorization"
            value = "token ${getToken()}"
        }

        authentication { create<HttpHeaderAuthentication>("header") }
    }
}

group = "com.bayo-code"

version = "0.9.0"

mavenPublishing {
    coordinates(
            groupId = "com.bayo-code",
            artifactId = "kphonenumber",
            version = version.toString()
    )

    // Configure POM metadata for the published artifact
    pom {
        name.set("KPhoneNumber")
        description.set("Phone number parsing library for Kotlin Multiplatform")
        inceptionYear.set("2024")
        url.set("https://github.com/bayo-code/kphonenumber")

        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        developers {
            developer {
                id.set("bayo-code")
                name.set("Adebayo Jagunmolu")
                email.set("hardebahyho@gmail.com")
            }
        }

        scm { url.set("https://github.com/bayo-code/kphonenumber") }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()
}
