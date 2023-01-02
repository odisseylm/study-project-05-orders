package com.mvv.bank.orders.repository.jpa.entities

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.ZonedDateTime


@Entity
@Table(name = "FX_ORDERS")
@Suppress("JpaDataSourceORMInspection")
class FxOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    var id: Long? = null

    @Column(name = "ORDER_TYPE", nullable = false)
    @Convert(converter = OrderType.SqlConverter::class)
    lateinit var orderType: OrderType

    @Column(name = "SIDE", nullable = false)
    @Convert(converter = Side.SqlConverter::class)
    lateinit var side: Side

    @Column(name = "MARKET", nullable = false)
    lateinit var market: String

    @Column(name = "BUY_SELL", nullable = false)
    @Convert(converter = BuySellType.SqlConverter::class)
    lateinit var buySellType: BuySellType

    @Column(name = "ORDER_STATE", nullable = false)
    @Convert(converter = OrderState.SqlConverter::class)
    lateinit var orderState: OrderState

    @Column(name = "BUY_CUR", nullable = false)
    lateinit var buyCurrency: String
    @Column(name = "SELL_CUR", nullable = false)
    lateinit var sellCurrency: String
    @Column(name = "VOLUME", nullable = false)
    lateinit var volume: BigDecimal

    // It is nullable since market price does not require limit/stop price (since executed immediately)
    @Column(name = "LIMIT_PRICE", nullable = true)
    var limitStopPrice: BigDecimal? = null
    // It is nullable since market price does not require 'daily execution type' (since executed immediately)
    @Column(name = "DAILY_TYPE", nullable = true)
    var dailyExecutionType: DailyExecutionType? = null


    @Column(name = "RES_TIMESTAMP")
    var resultingRateDateTime: ZonedDateTime? = null
    @Column(name = "RES_CCY1")
    var resultingRateCcy1: String? = null
    @Column(name = "RES_CCY2")
    var resultingRateCcy2: String? = null
    @Column(name = "RES_RATE_BID")
    var resultingRateBid: BigDecimal? = null
    @Column(name = "RES_RATE_ASK")
    var resultingRateAsk: BigDecimal? = null

    @Column(name = "PLACED_AT", nullable = false)
    var placedAt: ZonedDateTime? = null
    @Column(name = "EXECUTED_AT")
    var executedAt: ZonedDateTime? = null
    @Column(name = "EXECUTED_AT")
    var canceledAt: ZonedDateTime? = null
    @Column(name = "EXECUTED_AT")
    var expiredAt: ZonedDateTime? = null
}
