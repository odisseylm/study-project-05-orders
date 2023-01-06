package com.mvv.bank.orders.rest

import com.mvv.bank.orders.rest.conversion.FxOrderMapper
import com.mvv.bank.orders.rest.entities.FxOrder
import com.mvv.bank.orders.service.FxOrderService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("orders/cash")
class FxOrdersRestController (
    val orderService: FxOrderService,
    val fxOrderMapper: FxOrderMapper,
) {

    @GetMapping
    fun order(id: Long): FxOrder = fxOrderMapper.toDto(orderService.getOrder(id))
        //?: throw NotFoundException("FX order with id $id is not found.")

}
