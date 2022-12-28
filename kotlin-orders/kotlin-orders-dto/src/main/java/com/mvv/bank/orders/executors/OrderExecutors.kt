package com.mvv.bank.orders.executors

import com.mvv.bank.orders.domain.FxRate


interface OrderExecutor<Price> {
    fun priceChanged(price: Price)
}

interface FxCashOrderExecutor : OrderExecutor<FxRate>
