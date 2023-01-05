package com.mvv.bank.orders.domain

import com.mvv.bank.log.safe
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import javax.annotation.Tainted
import javax.annotation.Untainted
import javax.annotation.concurrent.Immutable


@Untainted @Immutable
class MarketSymbol private constructor (@param:Tainted @field:Untainted val value: String) {
    init { validateMarketSymbol(value) }
    @Untainted
    override fun toString(): String = value
    override fun equals(other: Any?): Boolean =
        (this === other) || ((this.javaClass == other?.javaClass) && (this.value == (other as MarketSymbol).value))
    override fun hashCode(): Int = value.hashCode()

    companion object {
        @JvmStatic fun of(marketSymbol: String) = MarketSymbol(marketSymbol)
        // standard java method to get from string. It can help to integrate with other java frameworks.
        @JvmStatic fun valueOf(marketSymbol: String) = of(marketSymbol)
    }
}


interface Market {
    val name: String
    val symbol: MarketSymbol
    val zoneId: ZoneId
    val description: String

    val defaultOpenTime: LocalTime  // inclusive
    val defaultCloseTime: LocalTime // exclusive

    fun isWorkingDay(date: LocalDate): Boolean
    fun openTime(date: LocalDate): LocalTime = if (isWorkingDay(date)) defaultOpenTime else LocalTime.MIDNIGHT
    fun closeTime(date: LocalDate): LocalTime = if (isWorkingDay(date)) defaultCloseTime else LocalTime.MIDNIGHT
}


interface MarketFactory {
    fun market(marketSymbol: MarketSymbol): Market
}

/*
Stock Exchange symbols:
 * NASDAQ
 * NYSE
*/


private const val MARKET_SYMBOL_MAX_LENGTH = 25
private val marketSymbolPattern = Regex("^[A-Z\\-.]*\$")

private fun validateMarketSymbol(marketSymbol: String?) {
    if (marketSymbol.isNullOrBlank() ||
        (marketSymbol.length > MARKET_SYMBOL_MAX_LENGTH) ||
        !marketSymbolPattern.matches(marketSymbol))
        throw IllegalArgumentException("Invalid market symbol [${marketSymbol.safe}].")
}
