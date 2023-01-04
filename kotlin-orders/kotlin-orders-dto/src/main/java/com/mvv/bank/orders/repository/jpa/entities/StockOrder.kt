package com.mvv.bank.orders.repository.jpa.entities

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.ZonedDateTime


@Entity
@Table(name = "QUOTE_ORDERS")
@Suppress("JpaDataSourceORMInspection")
class StockOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    var id: Long? = null

    @Column(name = "USER", nullable = false)
    lateinit var user: String

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
    @Convert(converter = OrderState.SqlConverter::class) // TODO: move common/shared to base class
    lateinit var orderState: OrderState

    @Column(name = "PRODUCT", nullable = false)
    lateinit var product: String
    @Column(name = "VOLUME", nullable = false)
    lateinit var volume: BigDecimal

    @Column(name = "PRICE_CUR", nullable = true)
    lateinit var priceCurrency: String

    // It is nullable since market price does not require limit/stop price (since executed immediately)
    @Column(name = "LIMIT_PRICE", nullable = true)
    var limitStopPrice: BigDecimal? = null
    // It is nullable since market price does not require 'daily execution type' (since executed immediately)
    @Column(name = "DAILY_TYPE", nullable = true)
    var dailyExecutionType: DailyExecutionType? = null

    @Column(name = "RES_PRICE_BID")
    var resultingPrice: BigDecimal? = null
    @Column(name = "RES_QUOTE_BID")
    var resultingQuoteBid: BigDecimal? = null
    @Column(name = "RES_QUOTE_ASK")
    var resultingQuoteAsk: BigDecimal? = null
    @Column(name = "RES_QUOTE_TIMESTAMP")
    var resultingQuoteTimestamp: ZonedDateTime? = null

    @Column(name = "PLACED_AT", nullable = false)
    var placedAt: ZonedDateTime? = null
    @Column(name = "EXECUTED_AT")
    var executedAt: ZonedDateTime? = null
    @Column(name = "EXECUTED_AT")
    var canceledAt: ZonedDateTime? = null
    @Column(name = "EXECUTED_AT")
    var expiredAt: ZonedDateTime? = null
}
