package com.mvv.bank.orders.domain

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime


// In Foreign Exchange:
//  bid - price of client 'sell' (and dealer/bank 'buy') (lower price from pair),
//  ask - price of client 'buy'  (and dealer/bank 'sell')
//
// TODO: how to deal with inverted rates???
// TODO: 'bid < ask' rule should be applied for which counterCurrency???
//
data class FxRate (
    val market: MarketSymbol,
    val timestamp: ZonedDateTime,
    val marketDate: LocalDate,
    val marketTime: LocalTime,

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
    override fun toString(): String = "$currencyPair $mid($bid/$ask)"

    companion object // only for writing extension functions
}

fun FxRate.Companion.of(market: Market, timestamp: ZonedDateTime, currencyPair: CurrencyPair, bid: BigDecimal, ask: BigDecimal) =
    FxRate(
        market = market.symbol, timestamp = timestamp,
        marketDate = timestamp.withZoneSameInstant(market.zoneId).toLocalDate(),
        marketTime = timestamp.withZoneSameInstant(market.zoneId).toLocalTime(),
        currencyPair = currencyPair, bid = bid, ask = ask
    )

val FxRate.mid: BigDecimal get() = (bid + ask) / BigDecimal.valueOf(2) // math context is not needed there (at least now)
val FxRate.spread: BigDecimal get() = ask - bid
// TODO: should we swap bid and ask ???
fun FxRate.inverted(): FxRate = this.copy(currencyPair = this.currencyPair.inverted(), bid = invertRate(this.bid), ask = invertRate(this.ask))


@Suppress("MemberVisibilityCanBePrivate")
data class FxRateAsQuote (
    val rate: FxRate,
    val priceCurrency: Currency,
) : Quote {
    override val product: String get() = rate.currencyPair.oppositeCurrency(priceCurrency).toString()
    override val market: MarketSymbol get() = rate.market
    override val marketDate: LocalDate get() = rate.marketDate
    override val marketTime: LocalTime get() = rate.marketTime
    override val timestamp: ZonedDateTime get() = rate.timestamp
    override val bid: Amount get() = Amount.of(
        // TODO: should be invertRate(rate.bid) or invertRate(rate.ask)
        if (priceCurrency == rate.currencyPair.counter) rate.bid else invertRate(rate.bid), priceCurrency)
    override val ask: Amount get() = Amount.of(
        // TODO: should be invertRate(rate.ask) or invertRate(rate.bid)
        if (priceCurrency == rate.currencyPair.counter) rate.ask else invertRate(rate.ask), priceCurrency)
    init {
        check(priceCurrency == rate.currencyPair.base || priceCurrency == rate.currencyPair.counter) {
            "FX rate ${rate.currencyPair} does not suite order currencies (with price currency $priceCurrency)." }
            //"Currency $priceCurrency is not contained in ${rate.currencyPair}." }
    }
}

fun FxRate.containsCurrency(currency: Currency): Boolean = this.currencyPair.containsCurrency(currency)

fun FxRate.asPrice(priceCurrency: Currency, buySellType: BuySellType): Amount {
    check(this.containsCurrency(priceCurrency)) {
        "FX rate ${this.currencyPair} does not contain currency $priceCurrency." }

    val price: BigDecimal = when (buySellType) {
        BuySellType.BUY  -> this.bid
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

    return BigDecimal.ONE.divide(price, resScale, RoundingMode.HALF_UP).stripTrailingZeros()
}
