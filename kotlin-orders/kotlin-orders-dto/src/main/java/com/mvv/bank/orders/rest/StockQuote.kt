package com.mvv.bank.orders.rest

import com.mvv.bank.orders.domain.Amount
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime

class StockQuote (
    val market: String,
    val product: String,

    val timestamp: ZonedDateTime,
    val marketDate: LocalDate,
    val marketTime: LocalTime,

    val bid: Amount,
    val ask: Amount,
)
