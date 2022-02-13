package com.otso.stockdata

import java.time.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class StockpointEODH(val datestring: String, val open: Float,
                          val high: Float, val low: Float, val close: Float,
                          val adjustedClose: Float, val volume: Int, val name: String,
                          @Transient val date: LocalDate = LocalDate.now())


@Serializable
data class StockDetail(val detail: String, val data: String)

//@Serializable
//data class Stockpoint(val name: String, val date: Date, val open: Int,
//                      val high: Int, val low: Int, val close: Int, val volume: Int) {
//
//}
//@Serializable
//data class Meta(@SerialName("1. Information") val first: String,
//                @SerialName("2. Symbol") val second: String,
//                @SerialName("3. Last Refreshed") val third: String,
//                @SerialName("4. Output Size") val fourth: String,
//                @SerialName("5. Time Zone") val fifth: String,
//                )
//
//@Serializable
//data class fullOutput(@SerialName("Meta Data") val metaData: Meta)
