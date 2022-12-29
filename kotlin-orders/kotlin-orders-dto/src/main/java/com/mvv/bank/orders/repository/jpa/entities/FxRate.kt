package com.mvv.bank.orders.repository.jpa.entities

import jakarta.persistence.*
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime


@Entity
@IdClass(FxRateId::class)
@Table(name = "FX_RATES")
@Suppress("JpaDataSourceORMInspection")
class FxRate {
    @Id
    @Column(name = "CUR1", nullable = false)
    //@Basic(optional = false)
    //@Convert(converter = CurrencyConverter::class)
    //lateinit var cur1: Currency // TODO: try to use Currency instead of String
    lateinit var cur1: String

    @Id
    @Column(name = "CUR2", nullable = false)
    //@Convert(converter = CurrencyConverter::class)
    //lateinit var cur2: Currency
    lateinit var cur2: String

    @Column(name = "MARKET_SYMBOL", nullable = false)
    lateinit var marketSymbol: String

    @Column(name = "MARKET_DATE", nullable = false)
    lateinit var marketDate: LocalDate

    @Column(name = "MARKET_DATE_TIME", nullable = false)
    lateinit var marketDateTime: LocalDateTime

    @Id
    @Column(name = "DATE_TIME", nullable = false)
    lateinit var dateTime: ZonedDateTime

    @Column(name = "BID", nullable = false)
    lateinit var bid: BigDecimal

    @Column(name = "ASK", nullable = false)
    lateinit var ask: BigDecimal
}

data class FxRateId (
    val cur1: String,
    val cur2: String,
    val dateTime: ZonedDateTime,
) : Serializable