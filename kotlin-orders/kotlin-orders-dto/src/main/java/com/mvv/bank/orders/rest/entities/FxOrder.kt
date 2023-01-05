package com.mvv.bank.orders.rest.entities


class FxOrder: BaseOrder() {
    lateinit var buyCurrency: String
    lateinit var sellCurrency: String
    var resultingRate: FxRate? = null
}
