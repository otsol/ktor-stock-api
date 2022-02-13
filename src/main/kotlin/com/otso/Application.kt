package com.otso

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.otso.plugins.*
import com.otso.stockdata.load
import com.otso.stockdata.setStorage
import io.ktor.application.*


fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureRouting()
        configureSerialization()
        configureHTTP()
        configureAdministration()
        setStorage()
    }.start(wait = true)


}
