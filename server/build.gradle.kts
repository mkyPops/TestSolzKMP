plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvm()
    
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(libs.ktor.server.core)
                implementation(libs.ktor.server.netty)
                implementation(libs.ktor.server.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.ktor.server.cors)
                implementation(libs.ktor.server.auth)
                implementation(libs.ktor.server.logging)
                implementation(libs.logback)
            }
        }
    }
}
