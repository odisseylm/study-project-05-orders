package com.mvv.bank.orders.domain

import java.math.BigDecimal


data class FxRate (
    // The CurrencyPair as in Ccy1/Ccy2 where the Bid will be the price for selling ccy1 and buying ccy2.
    val currencyPair: CurrencyPair,

    // This FxRate might come from a combination of 2 rates through a cross currency, if it is the case,
    // return the cross currency.
    // Optional<String> crossCcy

    // we do not use there ask, bid, mid
    val bid: BigDecimal,
    val ask: BigDecimal,

    // true if the CurrencyPair follows market convention, e.g. EUR/USD and not USD/EUR (but it gets more complicated for
    // other cross-currencies, eg CHF/JPY?)
    //boolean isMarketConvention();
) {
    override fun toString(): String = "$currencyPair $mid"
}

val FxRate.mid: BigDecimal get() = (bid + ask) / BigDecimal.valueOf(2) // math context is not needed there (at least now)
val FxRate.spread: BigDecimal get() = ask - bid
