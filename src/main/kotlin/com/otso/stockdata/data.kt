package com.otso.stockdata

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.response.*
//import io.ktor.client.response.HttpResponse
import io.ktor.client.statement.*
import io.ktor.network.tls.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.security.SecureRandom
//import com.
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.typesafe.config.ConfigException
import kotlinx.serialization.Serializable
import java.util.*

import java.time.LocalDate
import java.time.format.DateTimeFormatter

val client = HttpClient(CIO) {
    engine {
        // this: CIOEngineConfig
        maxConnectionsCount = 1000
        endpoint {
            // this: EndpointConfig
            maxConnectionsPerRoute = 100
            pipelineMaxSize = 20
            keepAliveTime = 5000
            connectTimeout = 5000
            connectAttempts = 5
        }
        https {
            // this: TLSConfigBuilder
            serverName = "api.ktor.io"
            cipherSuites = CIOCipherSuites.SupportedSuites

            random = SecureRandom()

        }
    }
}

var stockDataStorage = emptyList<List<StockDetail>>()
var stockDataStorage2 = emptyList<StockpointEODH>()
var stockStorage = mutableMapOf<String, List<StockpointEODH>>()

suspend fun loadData(): String  {
//= coroutineScope {
//    val data = launch {
//        //client.get("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=IBM&outputsize=full&apikey=PVQQDKZ26IILSNPS")
//        val response: io.ktor.client.statement.HttpResponse = client.get("https://eodhistoricaldata.com/api/eod/AAPL.US?from=2017-01-05&to=2017-02-10&period=d&fmt=json&api_token={OeAFFmMliFG5orCUuwAKQ8l4WWFQ67YX}")
//        csvFile = response.receive()
//        return@launch
//    }
    val response: io.ktor.client.statement.HttpResponse = client.get("https://eodhistoricaldata.com/api/eod/MCD.US?api_token=OeAFFmMliFG5orCUuwAKQ8l4WWFQ67YX&period=d")//get("https://eodhistoricaldata.com/api/eod/AMZN.US?api_token=62032ef650f652.09181735")
    return response.receive<String>()
}

fun load(): String = runBlocking {
    val data = loadData()
    println(data)
    if(data.lastIndexOf("\n")>0) {
        data.substring(0, data.lastIndexOf("\n"));
    } else {
        data;
    }
}


fun setStorage() {
    var csvFile: String = load()
    //println(csvFile)
    val rows: List<Map<String, String>> = csvReader().readAllWithHeader(csvFile)
    //println(rows)
    var simpleFormat = DateTimeFormatter.ISO_DATE
    stockDataStorage =
        rows.map { m ->
            m.entries.map {
                StockDetail(it.key, it.value)
            }
        }
    stockDataStorage2 =
        stockDataStorage.map {
            StockpointEODH(
                it[0].data,             //datestring
                it[1].data.toFloat(),   //open
                it[2].data.toFloat(),   //high
                it[3].data.toFloat(),   //low
                it[4].data.toFloat(),   //close
                it[5].data.toFloat(),   //adjustedClose
                it[6].data.toInt(),     //volume
                "MCD",            //name
                LocalDate.parse(it[0].data, simpleFormat)
            )
        }

    println(stockDataStorage.toString())
    println("ok")
}

suspend fun loadStockData(stock: String): String {
    //var simpleFormat = DateTimeFormatter.ISO_DATE
    var currentDate = LocalDate.now()
    val currentDS = currentDate.toString()
    println(currentDS)
    var tenDaysAgo = currentDate.minusDays(10L)
    val tenAgoDS = tenDaysAgo.toString()
    println(tenAgoDS)

    val response: io.ktor.client.statement.HttpResponse = client.get("https://eodhistoricaldata.com/api/eod/${stock}.US?from=${tenAgoDS}&to=${currentDS}&period=d&api_token=62032ef650f652.09181735")//get("https://eodhistoricaldata.com/api/eod/AMZN.US?api_token=62032ef650f652.09181735")
    return response.receive<String>()
}

fun loadStock(stock: String): String = runBlocking {
    val data = loadStockData(stock)
    //println(data)
    if(data.lastIndexOf("\n")>0) {
        data.substring(0, data.lastIndexOf("\n"));
    } else {
        data;
    }
}

fun setStockDataStorage(stock: String): List<StockpointEODH>? {
    if( stockStorage.keys.contains(stock)) {
        println(stockStorage.toString())
        println("ok")
        return stockStorage[stock];
    } else {
        var csvFile: String = loadStock(stock)
        //println(csvFile)
        val rows: List<Map<String, String>> = csvReader().readAllWithHeader(csvFile)
        //println(rows)
        var simpleFormat = DateTimeFormatter.ISO_DATE
        val stockDataStorage3 =
            rows.map { m ->
                m.entries.map {
                    StockDetail(it.key, it.value)
                }
            }
        val stockDataStorage4 =
            stockDataStorage3.map {
                StockpointEODH(
                    it[0].data,             //datestring
                    it[1].data.toFloat(),   //open
                    it[2].data.toFloat(),   //high
                    it[3].data.toFloat(),   //low
                    it[4].data.toFloat(),   //close
                    it[5].data.toFloat(),   //adjustedClose
                    it[6].data.toInt(),     //volume
                    stock,            //name
                    LocalDate.parse(it[0].data, simpleFormat)
                )
            }
        stockStorage[stock] = stockDataStorage4
        println(stockStorage.toString())
        println("ok")
        return stockStorage[stock];
    }



}


