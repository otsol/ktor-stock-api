package com.otso.plugins


import com.otso.stockdata.stockDataStorage
//import com.otso.stockdata.rows
import com.otso.stockdata.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.application.*
import io.ktor.client.utils.EmptyContent.status
import io.ktor.response.*
import io.ktor.request.*

fun Application.configureRouting() {

    routing {
        route("/date") {
            get("{date}") {
                val date = call.parameters["date"] ?: return@get call.respondText(
                    "Missing or malformed date",
                    status = HttpStatusCode.BadRequest
                )
                val stockPoint =

                    stockDataStorage.find { l: List<StockDetail> -> l[0].data == date } ?: return@get call.respondText(
                        "No data point with date $date",
                        status = HttpStatusCode.NotFound
                    )
//                    rows.find { p: Map<String, String> -> p["Date"] == date } ?: return@get call.respondText(
//                        "No data point with date $date",
//                        status = HttpStatusCode.NotFound
//                    )
                println(stockDataStorage[15][1].data)
                call.respond(stockPoint)
            }
            get {
                val stockPoint = stockDataStorage2 ?: return@get call.respondText(
                "No data found",
                status = HttpStatusCode.NotFound
                )
                call.respond(stockPoint)
            }
        }
        route("/date1") {
            get("{date}") {
                val date = call.parameters["date"] ?: return@get call.respondText(
                    "Missing or malformed date",
                    status = HttpStatusCode.BadRequest
                )
                val stockPoint =

                    stockDataStorage2.find { it.datestring == date } ?: return@get call.respondText(
                        "No data point with date $date",
                        status = HttpStatusCode.NotFound
                    )


                call.respond(stockPoint)
            }
        }
        route("/stock") {
            get("{stock}") {
                val stock = call.parameters["stock"] ?: return@get call.respondText(
                    "Missing or malformed date",
                    status = HttpStatusCode.BadRequest
                )
                println(stock)
                val stockPoint = setStockDataStorage(stock) ?: return@get call.respondText(
                        "No data point with date $stock",
                        status = HttpStatusCode.NotFound
                    )


                call.respond(stockPoint)
            }
        }
        get("/") {
            call.respondText("Hello World!")
        }
    }
}

suspend fun elseResponseAndString(call: ApplicationCall, date: String): String {
    call.respondText(
        "No data point with date $date",
        status = HttpStatusCode.NotFound
    )
    return ""

}