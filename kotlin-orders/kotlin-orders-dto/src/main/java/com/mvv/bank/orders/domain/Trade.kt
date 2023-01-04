package com.mvv.bank.orders.domain

import com.mvv.bank.log.safe
import java.math.BigDecimal
import java.time.ZonedDateTime


interface Trade<P> {
    val market: MarketSymbol

    val product: P
    val buySellType: BuySellType
    val volume: BigDecimal // integer in case of stocks; float in case of cash/money
    val price: Amount

    val amount: Amount get() = volume * price
    val tradedAt: ZonedDateTime
}

data class FxCashTrade (
    val id: Long,

    override val market: MarketSymbol,

    override val buySellType: BuySellType,
    val buyCurrency: Currency,
    val sellCurrency: Currency,
    override val volume: BigDecimal,
    override val price: Amount,

    override val tradedAt: ZonedDateTime,
) : Trade<Currency> {

    init {
        check(price.currency == priceCurrency) {
            "Seems price ${price.safe} has wrong currency for deal ${this}." }
    }

    @Deprecated("Better to use buyCurrency/sellCurrency directly if you access/config cash trade by direct CashTrade typed variable.")
    override val product: Currency get() = when (buySellType) {
        //null -> null
        BuySellType.BUY  -> buyCurrency
        BuySellType.SELL -> sellCurrency
    }

    val priceCurrency: Currency get() = when (buySellType) {
        // opposite
        //null -> null
        BuySellType.BUY  -> sellCurrency
        BuySellType.SELL -> buyCurrency
    }
}

data class StockTrade (
    val id: Long,

    override val market: MarketSymbol,
    val company: CompanySymbol,

    override val buySellType: BuySellType,
    override val volume: BigDecimal,
    override val price: Amount,
    override val tradedAt: ZonedDateTime,
) : Trade<CompanySymbol> {
    override val product: CompanySymbol get() = company
}
