package com.otso

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.otso.plugins.*
import com.otso.stockdata.load
import com.otso.stockdata.setStorage
//import io.ktor.application.*

// Remember to adjust heroku envs GRADLE_TASK="build"


// Set your environment variable PORT with export PORT=3000 etc. You may have to restart your IDE if you change
// the variable after launching the IDE.
// Heroku defines PORT automatically
fun main() {
    var envPORT: String = System.getenv("PORT") ?: "8080"
    embeddedServer(Netty, port = envPORT.toInt()) {
        configureRouting()
        configureSerialization()
        configureHTTP()
        //configureAdministration()
        setStorage()
    }.start(wait = true)


}
