package com.otso.stockdata

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.response.*

import io.ktor.client.statement.*
import io.ktor.network.tls.*
import kotlinx.coroutines.runBlocking
import java.security.SecureRandom

import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import io.ktor.http.*
import io.ktor.http.content.*
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

var envMCDKEY: String = System.getenv("API_KEY_MCD") ?: "OeAFFmMliFG5orCUuwAKQ8l4WWFQ67YX" // public demo api key
var envSTOCKKEY: String = System.getenv("API_KEY_STOCK") ?: "OeAFFmMliFG5orCUuwAKQ8l4WWFQ67YX" // public demo api key

// Used to save all stock data on the server
var stockDataStorage = emptyList<List<StockDetail>>()
var stockDataStorage2 = emptyList<StockpointEODH>()
var stockStorage = mutableMapOf<String, List<StockpointEODH>>()

// helper function for setStorage
suspend fun loadData(): String  {

    val response: io.ktor.client.statement.HttpResponse = client.get("https://eodhistoricaldata.com/api/eod/MCD.US?api_token=${envMCDKEY}&period=d")
    return response.receive<String>()
}
// helper function for setStorage
fun load(): String = runBlocking {
    val data = loadData()
    //println(data)
    if(data.lastIndexOf("\n")>0) {
        data.substring(0, data.lastIndexOf("\n"));
    } else {
        data;
    }
}


fun setStorage() {  // set stock history for McDonald's stock. Multiple decades. Run only once on startup.
    var csvFile: String = load()
    val rows: List<Map<String, String>> = csvReader().readAllWithHeader(csvFile)

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
}


// helper function for setStockDataStorage
fun loadStock(stock: String): String? = runBlocking {

    var currentDate = LocalDate.now()
    val currentDS = currentDate.toString()
    println(currentDS)
    var tenDaysAgo = currentDate.minusDays(10L)
    val tenAgoDS = tenDaysAgo.toString()
    println(tenAgoDS)

    val response: io.ktor.client.statement.HttpResponse? = try {
         client.get("https://eodhistoricaldata.com/api/eod/${stock}.US?from=${tenAgoDS}&to=${currentDS}&period=d&api_token=${envSTOCKKEY}")
    } catch (cause: Throwable) {
        null
    }
    if(response == null) { //response.status != HttpStatusCode.OK
        null
    } else {
    val data = response.receive<String>()
    //println(data)
    if(data.lastIndexOf("\n")>0) {
        data.substring(0, data.lastIndexOf("\n"));
    } else {
        data;
    }
    }
}
// load recent data for a single stock from EOD Historical Data
fun setStockDataStorage(stock: String): List<StockpointEODH>? {
    if( stockStorage.keys.contains(stock)) {
        return stockStorage[stock]; // not null because of the contains check
    } else {
        var csvFile: String = loadStock(stock) ?: return emptyList()
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
        return stockStorage[stock];
    }



}


