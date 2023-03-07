package com.mvv.bank.orders.rest.entities

import com.mvv.bank.orders.domain.CurrencyPair
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime


data class FxRate (
    val market: String,
    val timestamp: ZonedDateTime,
    val marketDate: LocalDate,
    val marketTime: LocalTime,

    // The CurrencyPair as in Ccy1/Ccy2 where the Bid will be the price for selling ccy1 and buying ccy2.
    // TODO: remove this domain object from DTO
    val currencyPair: CurrencyPair,
    //val cur1: String,
    //val cur2: String,

    // This FxRate might come from a combination of 2 rates through a cross currency, if it is the case,
    // return the cross currency.
    // Optional<String> crossCcy

    // we do not use there ask, bid, mid
    val bid: BigDecimal,
    val ask: BigDecimal,
)
