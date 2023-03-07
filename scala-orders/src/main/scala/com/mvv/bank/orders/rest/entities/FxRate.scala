package com.mvv.bank.orders.rest.entities

import java.time.{LocalDate, LocalTime, ZonedDateTime}
import scala.math.BigDecimal


case class FxRate (
    market: String,
    timestamp: ZonedDateTime,
    marketDate: LocalDate,
    marketTime: LocalTime,

    // The CurrencyPair as in Ccy1/Ccy2 where the Bid will be the price for selling ccy1 and buying ccy2.
    //currencyPair: CurrencyPair,
    cur1: String,
    cur2: String,

    // This FxRate might come from a combination of 2 rates through a cross currency, if it is the case,
    // return the cross currency.
    // Optional<String> crossCcy

    // we do not use there ask, bid, mid
    bid: BigDecimal,
    ask: BigDecimal,
)
