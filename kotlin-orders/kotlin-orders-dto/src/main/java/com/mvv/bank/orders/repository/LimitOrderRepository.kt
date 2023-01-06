package com.mvv.bank.orders.repository

import com.mvv.bank.orders.domain.CashLimitOrder

interface CashLimitOrderRepository {
    fun saveOrders(orders: List<CashLimitOrder>)
}
