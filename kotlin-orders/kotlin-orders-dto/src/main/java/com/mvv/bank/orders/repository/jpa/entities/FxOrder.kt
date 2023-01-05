package com.mvv.bank.orders.repository.jpa.entities

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.ZonedDateTime


@Entity
@Table(name = "FX_ORDERS")
@Suppress("JpaDataSourceORMInspection")
class FxOrder : BaseOrder() {

    @Column(name = "BUY_CUR", nullable = false)
    lateinit var buyCurrency: String
    @Column(name = "SELL_CUR", nullable = false)
    lateinit var sellCurrency: String

    @get:Transient
    val priceCurrency: String get() =
        when (buySellType) {
            // opposite
            //null -> null
            BuySellType.BUY  -> sellCurrency
            BuySellType.SELL -> buyCurrency
        }


    @Column(name = "RES_TIMESTAMP")
    var resultingRateTimestamp: ZonedDateTime? = null
    @Column(name = "RES_CCY1")
    var resultingRateCcy1: String? = null
    @Column(name = "RES_CCY2")
    var resultingRateCcy2: String? = null
    @Column(name = "RES_RATE_BID")
    var resultingRateBid: BigDecimal? = null
    @Column(name = "RES_RATE_ASK")
    var resultingRateAsk: BigDecimal? = null
}
