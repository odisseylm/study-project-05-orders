package com.mvv.bank.orders.dto

import com.mvv.bank.orders.domain.OrderType

data class GeneralOrder (
    val id: Long,
    val currencyPair: String,
    val type: OrderType,
)
