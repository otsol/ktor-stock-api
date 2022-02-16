package com.otso

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.otso.plugins.*
import com.otso.stockdata.load
import com.otso.stockdata.setStorage
import io.ktor.application.*

// remember to adjust heroku envs GRADLE_TASK="build"
fun main() { // Netty, port = 8080, host = "0.0.0.0"
    embeddedServer(Netty, port = System.getenv("PORT").toInt()) {
        configureRouting()
        configureSerialization()
        configureHTTP()
        configureAdministration()
        setStorage()
    }.start(wait = true)


}
