package com.mvv.bank.orders.service

import com.mvv.bank.orders.domain.AbstractFxCashOrder
import com.mvv.bank.orders.domain.OrderNaturalKey
import org.springframework.stereotype.Service


@Service
class FxOrderService {

    fun getOrder(id: Long): AbstractFxCashOrder {
        TODO("Not implemented")
    }

    fun getOrder(id: OrderNaturalKey): AbstractFxCashOrder {
        TODO("Not implemented")
    }
}
