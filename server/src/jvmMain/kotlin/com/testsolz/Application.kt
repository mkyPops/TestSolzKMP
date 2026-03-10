package com.testsolz

import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }
    
    install(CORS) {
        anyHost()
    }

    routing {
        get("/") {
            call.respondText("TestSolz Backend is Running!")
        }

        route("/v1") {
            get("/attendance/status") {
                // Return status of all employees
                call.respond(emptyList<String>())
            }
        }
    }
}
