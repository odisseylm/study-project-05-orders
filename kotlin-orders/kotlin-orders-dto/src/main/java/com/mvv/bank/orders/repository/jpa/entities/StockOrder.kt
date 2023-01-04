package com.mvv.bank.orders.repository.jpa.entities

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.ZonedDateTime


@Entity
@Table(name = "QUOTE_ORDERS")
@Suppress("JpaDataSourceORMInspection")
class StockOrder : BaseOrder() {

    @Column(name = "PRODUCT", nullable = false)
    lateinit var product: String

    @Column(name = "PRICE_CUR", nullable = true)
    lateinit var priceCurrency: String

    @Column(name = "RES_PRICE_BID")
    var resultingPrice: BigDecimal? = null
    @Column(name = "RES_QUOTE_BID")
    var resultingQuoteBid: BigDecimal? = null
    @Column(name = "RES_QUOTE_ASK")
    var resultingQuoteAsk: BigDecimal? = null
    @Column(name = "RES_QUOTE_TIMESTAMP")
    var resultingQuoteTimestamp: ZonedDateTime? = null
}
