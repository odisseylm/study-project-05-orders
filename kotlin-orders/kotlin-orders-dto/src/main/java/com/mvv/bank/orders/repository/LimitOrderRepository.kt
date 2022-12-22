package com.mvv.bank.orders.repository

import com.mvv.bank.orders.domain.FxCashLimitOrder

interface FxCashLimitOrderRepository {
    fun saveOrders(orders: List<FxCashLimitOrder>)
}
