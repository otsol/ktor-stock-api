package com.otso

import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.features.*
import io.ktor.server.engine.*
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.request.*
import kotlin.test.*
import io.ktor.server.testing.*
import com.otso.plugins.*

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication({ configureRouting() }) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Hello World!", response.content)
            }
            handleRequest(HttpMethod.Get, "/date/2021-10-21").apply {
                assertEquals(HttpStatusCode.OK, response.status())
            }
        }
    }
}