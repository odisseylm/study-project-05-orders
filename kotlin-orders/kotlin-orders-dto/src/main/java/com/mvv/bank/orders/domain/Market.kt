package com.mvv.bank.orders.domain

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId


interface Market {
    val marketName: String
    val marketZoneId: ZoneId
    val description: String

    val defaultOpenTime: LocalTime  // inclusive
    val defaultCloseTime: LocalTime // exclusive

    fun isWorkingDay(date: LocalDate): Boolean
    fun openTime(date: LocalDate): LocalTime = if (isWorkingDay(date)) defaultOpenTime else LocalTime.MIDNIGHT
    fun closeTime(date: LocalDate): LocalTime = if (isWorkingDay(date)) defaultCloseTime else LocalTime.MIDNIGHT
}

interface MarketFactory {
    fun market(marketId: String)
}

/*
Symbols:
Stock Exchange
NASDAQ
NYSE
*/