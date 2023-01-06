package com.mvv.bank.orders.rest

import com.mvv.bank.orders.rest.conversion.CashOrderMapper
import com.mvv.bank.orders.rest.entities.CashOrder
import com.mvv.bank.orders.service.CashOrderService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("orders/cash")
class CashOrdersRestController (
    val orderService: CashOrderService,
    val cashOrderMapper: CashOrderMapper,
) {

    @GetMapping
    fun order(id: Long): CashOrder = cashOrderMapper.toDto(orderService.getOrder(id))
        //?: throw NotFoundException("FX order with id $id is not found.")

}
