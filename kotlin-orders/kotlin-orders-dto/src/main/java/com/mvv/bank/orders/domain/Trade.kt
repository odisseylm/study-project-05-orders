package com.mvv.bank.orders.domain

import java.math.BigDecimal
import java.time.Instant


interface Trade<P> {
    val product: P
    val count: BigDecimal // integer in case of stocks; float in case of cash/money
    val amount: Amount
    val tradedAt: Instant
    val market: Market
}

data class CashTrade (
    val id: Long,
    override val product: Currency,
    override val count: BigDecimal,
    override val amount: Amount,
    override val tradedAt: Instant,
    override val market: Market,
) : Trade<Currency>
