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
