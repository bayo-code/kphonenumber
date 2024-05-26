import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework
import java.util.*

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    `maven-publish`
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
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
    ).forEach {
        it.binaries.framework {
            baseName = "kphonenumber"
            xcf.add(this)
            isStatic = true
        }
    }

    listOf(
        mingwX64(),
        linuxX64(),
        linuxArm64()
    ).forEach {
        it.binaries.staticLib {
            baseName = "kphonenumber"
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.bayocode.kphonenumber"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

fun loadEnv() {
    val properties = Properties()
    rootProject.file("local.properties").inputStream().use {
        properties.load(it)
    }

    properties.forEach { (key, value) ->
        ext[key.toString()] = value
    }
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

        authentication {
            create<HttpHeaderAuthentication>("header")
        }
    }
}

group = "com.bayocode"
version = "0.0.1-SNAPSHOT"
