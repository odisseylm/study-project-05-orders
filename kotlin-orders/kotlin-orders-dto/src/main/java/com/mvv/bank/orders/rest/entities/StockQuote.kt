package com.mvv.bank.orders.rest.entities

import com.mvv.bank.orders.domain.Market
import com.mvv.bank.orders.domain.Company

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime

data class StockQuote (
    val market: String,
    val company: String,

    val timestamp: ZonedDateTime,
    val marketDate: LocalDate,
    val marketTime: LocalTime,

    val bid: Amount,
    val ask: Amount,
) {
    companion object // for writing extension factory functions
}


// mainly for testing (probably it is better to move it to test sources...)
operator fun StockQuote.Companion.invoke(
    market: Market,
    company: Company,
    timestamp: ZonedDateTime,
    bid: BigDecimal,
    ask: BigDecimal,
    currency: String,
): StockQuote = StockQuote(
    market = market.symbol.value,
    company = company.symbol.value,
    timestamp = timestamp,
    marketDate = timestamp.withZoneSameInstant(market.zoneId).toLocalDate(),
    marketTime = timestamp.withZoneSameInstant(market.zoneId).toLocalTime(),
    bid = Amount(bid, currency),
    ask = Amount(ask, currency),
)
