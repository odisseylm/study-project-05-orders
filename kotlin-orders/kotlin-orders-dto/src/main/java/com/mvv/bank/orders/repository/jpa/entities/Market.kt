package com.mvv.bank.orders.repository.jpa.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalTime
import java.time.ZoneId


@Entity
@Table(name = "MARKETS")
@Suppress("JpaDataSourceORMInspection")
class Market {
    @Id
    @Column(name = "SYMBOL", nullable = false)
    lateinit var symbol: String

    @Column(name = "NAME", nullable = false)
    lateinit var name: String

    @Column(name = "ZONE_ID", nullable = false)
    lateinit var zoneId: ZoneId

    @Column(name = "DESCR", nullable = false)
    lateinit var description: String

    @Column(name = "DEF_OPEN_TIME", nullable = false)
    lateinit var defaultOpenTime: LocalTime

    @Column(name = "DEF_CLOSE_TIME", nullable = false)
    lateinit var defaultCloseTime: LocalTime
}
