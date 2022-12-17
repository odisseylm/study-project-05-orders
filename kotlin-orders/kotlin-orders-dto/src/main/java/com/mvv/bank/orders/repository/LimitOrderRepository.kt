package com.mvv.bank.orders.repository

import com.mvv.bank.orders.domain.LimitOrder

interface LimitOrderRepository {
    fun saveOrders(orders: List<LimitOrder>)
}
