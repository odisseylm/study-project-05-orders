package com.mvv.bank.orders.domain

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime


// A stock quote is the price of a stock as quoted on an exchange.
// A basic quote for a specific stock provides information, such as its bid and ask price,
// last traded price, and volume traded.
// https://www.investopedia.com/terms/s/stockquote.asp
// https://www.wallstreetmojo.com/stock-quote/
data class StockQuote(
    val marketSymbol: String,
    val symbol: String, // symbol for this market; see https://www.investopedia.com/terms/s/stocksymbol.asp

    val date: LocalDate,
    val dateTime: LocalDateTime,

    val bid: Amount,
    val ask: Amount,

    val last: Amount,

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
)
