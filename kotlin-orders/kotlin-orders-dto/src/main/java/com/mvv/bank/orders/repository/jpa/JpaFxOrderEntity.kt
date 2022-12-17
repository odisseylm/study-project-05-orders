package com.mvv.bank.orders.repository.jpa

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "FX_ORDERS")
@Suppress("JpaDataSourceORMInspection")
class JpaFxOrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    var id: Long? = null

    //@Column(name = "CUR")
    //@Convert(converter = CurrencyPairConverter::class)
    //var currencyPair: CurrencyPair? = null

    @Column(name = "CCY1", nullable = false)
    var ccy1: String? = null

    @Column(name = "CCY2", nullable = false)
    var ccy2: String? = null

    @Column(name = "ORDER_TYPE", nullable = false)
    @Convert(converter = OrderTypeConverter::class)
    lateinit var orderType: JpaOrderType

    @Column(name = "RATE", nullable = false)
    var price: BigDecimal? = null

    @Column(name = "PLACED_AT", nullable = false)
    var placedAt: Instant? = null

    @Column(name = "EXECUTED_AT")
    var executedAt: Instant? = null
}
