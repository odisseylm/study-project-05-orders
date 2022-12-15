package com.mvv.bank.orders.domain

interface OrderExecutionContext {
}

interface Order {
    fun isConditionOk(context: OrderExecutionContext): Boolean
    fun execute(context: OrderExecutionContext): Unit // TODO: what to return ?
}
