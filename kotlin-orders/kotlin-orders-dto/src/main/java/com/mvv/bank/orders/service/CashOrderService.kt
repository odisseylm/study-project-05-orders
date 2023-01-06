package com.mvv.bank.orders.service

import com.mvv.bank.orders.domain.AbstractCashOrder
import com.mvv.bank.orders.domain.OrderNaturalKey
import org.springframework.stereotype.Service


@Service
class CashOrderService {

    fun getOrder(id: Long): AbstractCashOrder {
        TODO("Not implemented")
    }

    fun getOrder(id: OrderNaturalKey): AbstractCashOrder {
        TODO("Not implemented")
    }
}
