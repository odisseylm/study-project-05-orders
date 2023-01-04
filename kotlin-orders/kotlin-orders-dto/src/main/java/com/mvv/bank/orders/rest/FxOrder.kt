package com.mvv.bank.orders.rest


class FxOrder: BaseOrder() {
    lateinit var buyCurrency: String
    lateinit var sellCurrency: String
    var resultingRate: FxRate? = null
}
