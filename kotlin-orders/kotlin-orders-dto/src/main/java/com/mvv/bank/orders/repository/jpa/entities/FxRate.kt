package com.mvv.bank.orders.repository.jpa.entities

import jakarta.persistence.*
import java.io.Serializable
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime


@Entity
@IdClass(FxRateId::class)
@Table(name = "FX_RATES")
@Suppress("JpaDataSourceORMInspection")
class FxRate {

    @Column(name = "MARKET", nullable = false)
    lateinit var market: String

    @Id
    @Column(name = "DATE_TIME", nullable = false)
    lateinit var timestamp: ZonedDateTime

    @Column(name = "MARKET_DATE", nullable = false)
    lateinit var marketDate: LocalDate

    @Column(name = "MARKET_TIME", nullable = false)
    lateinit var marketTime: LocalTime

    @Id
    @Column(name = "CUR1", nullable = false)
    //@Basic(optional = false)
    //@Convert(converter = CurrencyConverter::class)
    //lateinit var cur1: Currency // TODO: try to use Currency instead of String
    lateinit var cur1: String

    @Id
    @Column(name = "CUR2", nullable = false)
    lateinit var cur2: String

    @Column(name = "BID", nullable = false)
    lateinit var bid: BigDecimal

    @Column(name = "ASK", nullable = false)
    lateinit var ask: BigDecimal
}

data class FxRateId (
    val cur1: String,
    val cur2: String,
    val timestamp: ZonedDateTime,
) : Serializable
