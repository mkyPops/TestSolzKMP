package com.testsolz.core.network

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * HttpClientProvider
 * Provides a pre-configured Ktor HttpClient for KMP
 */
object HttpClientProvider {
    fun create(): HttpClient {
        return HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }
            
            install(HttpTimeout) {
                requestTimeoutMillis = 15000
                connectTimeoutMillis = 15000
            }
            
            defaultRequest {
                // Change this to your actual backend URL
                url("https://api.testsolz.com/v1/")
                // Add common headers here, like API keys
            }
        }
    }
}
