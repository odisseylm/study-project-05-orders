package com.mvv.bank.orders.rest

import com.mvv.bank.orders.domain.Amount

class StockOrder : BaseOrder() {
    lateinit var product: String

    var resultingPrice: Amount? = null
    var resultingQuote: Amount? = null
}