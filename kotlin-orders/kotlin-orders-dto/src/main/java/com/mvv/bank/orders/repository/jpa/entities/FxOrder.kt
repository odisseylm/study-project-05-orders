package com.mvv.bank.orders.repository.jpa.entities

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.Instant

@Entity
@Table(name = "FX_ORDERS")
@Suppress("JpaDataSourceORMInspection")
class FxOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false)
    var id: Long? = null

    @Column(name = "BUY_CUR", nullable = false)
    //@Convert(converter = CurrencyConverter::class)
    //lateinit var buyCurrency: Currency
    lateinit var buyCurrency: String

    @Column(name = "SELL_CUR", nullable = false)
    //@Convert(converter = CurrencyConverter::class)
    //lateinit var sellCurrency: Currency
    lateinit var sellCurrency: String

    @Column(name = "ORDER_TYPE", nullable = false)
    @Convert(converter = OrderType.SqlConverter::class)
    lateinit var orderType: OrderType

    @Column(name = "RATE")
    var price: BigDecimal? = null

    @Column(name = "PLACED_AT", nullable = false)
    var placedAt: Instant? = null

    @Column(name = "EXECUTED_AT")
    var executedAt: Instant? = null
}
