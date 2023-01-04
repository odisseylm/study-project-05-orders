package com.mvv.bank.orders.domain

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime


interface Quote {
    val product: String // symbol for this market; see https://www.investopedia.com/terms/s/stocksymbol.asp

    val market: String

    val timestamp: ZonedDateTime
    // date/time in market/exchange timezone
    val marketDate: LocalDate
    val marketTime: LocalTime

    val bid: Amount
    val ask: Amount
}

// A stock quote is the price of a stock as quoted on an exchange.
// A basic quote for a specific stock provides information, such as its bid and ask price,
// last traded price, and volume traded.
// https://www.investopedia.com/terms/s/stockquote.asp
// https://www.wallstreetmojo.com/stock-quote/
data class StockQuote (
    override val market: String,
    override val product: String, // symbol for this market; see https://www.investopedia.com/terms/s/stocksymbol.asp

    override val timestamp: ZonedDateTime,
    override val marketDate: LocalDate,
    override val marketTime: LocalTime,

    override val bid: Amount,
    override val ask: Amount,

    val last: Amount? = null,

    val high: Amount? = null,
    val low: Amount? = null,

    val high52week: Amount? = null,
    val low52week: Amount? = null,

    val lastTradedPrice: Amount? = null,
    val lastTradedPriceDatetime: Amount? = null,

    val previousClose: Amount? = null,
    val open: Amount? = null,
    val close: Amount? = null,

    val change: Amount? = null,
    val changePercent: BigDecimal? = null,

    val volume: BigDecimal? = null,
    //val averageVolume: BigDecimal? = null,
    val volume3m: BigDecimal? = null,
) : Quote {
    companion object { } // for possibility to write companion extension functions
}


fun StockQuote.asPrice(buySellType: BuySellType): Amount =
    when (buySellType) {
        BuySellType.BUY  -> this.ask
        BuySellType.SELL -> this.bid
    }


fun StockQuote.Companion.of(
    market: Market,
    company: Company,
    timestamp: ZonedDateTime,
    bid: BigDecimal,
    ask: BigDecimal,
    currency: Currency,
): StockQuote = StockQuote(
    market = market.symbol,
    product = company.symbol,
    timestamp = timestamp,
    marketDate = timestamp.withZoneSameInstant(market.zoneId).toLocalDate(),
    marketTime = timestamp.withZoneSameInstant(market.zoneId).toLocalTime(),
    bid = Amount.of(bid, currency),
    ask = Amount.of(ask, currency),
)
