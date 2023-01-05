package com.mvv.bank.orders.rest.entities


class StockOrder : BaseOrder() {
    lateinit var product: String

    var resultingPrice: Amount? = null
    var resultingQuote: StockQuote? = null
}
