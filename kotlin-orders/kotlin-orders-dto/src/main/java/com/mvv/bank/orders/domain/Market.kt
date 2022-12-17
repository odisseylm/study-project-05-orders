package com.mvv.bank.orders.domain

import java.time.ZoneId


interface Market {
    val marketZoneId: ZoneId
}

/*
Symbols:
Stock Exchange
NASDAQ
NYSE
*/
