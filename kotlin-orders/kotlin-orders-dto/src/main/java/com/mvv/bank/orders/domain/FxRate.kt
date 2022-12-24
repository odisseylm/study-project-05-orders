package com.mvv.bank.orders.domain

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime


data class FxRate (
    val marketSymbol: String,
    val marketDate: LocalDate,
    val marketDateTime: LocalDateTime,
    val dateTime: ZonedDateTime,

    // The CurrencyPair as in Ccy1/Ccy2 where the Bid will be the price for selling ccy1 and buying ccy2.
    val currencyPair: CurrencyPair,

    // This FxRate might come from a combination of 2 rates through a cross currency, if it is the case,
    // return the cross currency.
    // Optional<String> crossCcy

    // we do not use there ask, bid, mid
    val bid: BigDecimal,
    val ask: BigDecimal,

    // true if the CurrencyPair follows market convention, e.g. EUR/USD and not USD/EUR (but it gets more complicated for
    // other cross-currencies, e.g. CHF/JPY?)
    //boolean isMarketConvention();
) {
    override fun toString(): String = "$currencyPair $mid"
}

val FxRate.mid: BigDecimal get() = (bid + ask) / BigDecimal.valueOf(2) // math context is not needed there (at least now)
val FxRate.spread: BigDecimal get() = ask - bid


@Suppress("MemberVisibilityCanBePrivate")
class FxRateAsQuote (
    val rate: FxRate,
    val priceCurrency: Currency,
) : Quote {
    override val productSymbol: String get() = rate.currencyPair.opposite(priceCurrency).toString()
    override val marketSymbol: String get() = rate.marketSymbol
    override val marketDate: LocalDate get() = rate.marketDate
    override val marketDateTime: LocalDateTime get() = rate.marketDateTime
    override val dateTime: ZonedDateTime get() = rate.dateTime
    override val bid: Amount get() = Amount.of(rate.bid, priceCurrency)
    override val ask: Amount get() = Amount.of(rate.ask, priceCurrency)

    init {
        check(priceCurrency == rate.currencyPair.base || priceCurrency == rate.currencyPair.counter) {
            "Currency $priceCurrency is not contained in ${rate.currencyPair}." }
    }
}

fun FxRate.containsCurrency(currency: Currency): Boolean = this.currencyPair.containsCurrency(currency)

fun FxRate.asPrice(priceCurrency: Currency, buySellType: BuySellType): Amount {
    check(this.containsCurrency(priceCurrency)) {
        "FX rate ${this.currencyPair} does not contain currency $priceCurrency." }

    val price: BigDecimal = when (buySellType) {
        BuySellType.BUY -> this.bid
        BuySellType.SELL -> this.ask
    }
    val fixedPriceValue: BigDecimal = if (this.currencyPair.counter == priceCurrency) price else invertRate(price)
    return Amount.of(fixedPriceValue, priceCurrency)
}

fun invertRate(price: BigDecimal): BigDecimal {
    if (price == BigDecimal.ZERO) {
        // or throw exception? In general, it is unexpected value...
        return BigDecimal.ZERO
    }

    val precision = price.precision()
    // T O D O: write more logical solution
    val resScale = if (price.scale() < 2) precision + 2 + (2 - price.scale())
                   else precision + price.scale()

    return BigDecimal.ONE.divide(price, resScale, RoundingMode.HALF_UP)
}
