package com.mvv.bank.orders.rest

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("orders/cash")
class FxOrdersRestController {

    @GetMapping
    fun order(): FxOrder =
        TODO("Not implemented.")

}
