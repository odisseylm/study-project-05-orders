package com.mvv.bank.orders.repository.jpa

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

@Suppress("JpaDataSourceORMInspection")
@Entity
@Table(name = "FX_ORDERS")
class JpaFxOrderEntity {
    @Id
    @Column(name = "ID")
    var id: Long? = null

    //@Column(name = "CUR")
    //@Convert(converter = CurrencyPairConverter::class)
    //var currencyPair: CurrencyPair? = null

    @Column(name = "CCY1")
    var ccy1: String? = null

    @Column(name = "CCY2")
    var ccy2: String? = null

    @Column(name = "ORDER_TYPE")
    @Convert(converter = OrderTypeConverter::class)
    lateinit var orderType: JpaOrderType

    @Column(name = "RATE")
    var price: BigDecimal? = null

    @Column(name = "PLACED_AT")
    var placedAt: Instant? = null

    @Column(name = "EXECUTED_AT")
    var executedAt: Instant? = null
}
