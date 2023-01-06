package com.mvv.bank.orders.rest.entities


class CashOrder: BaseOrder() {
    lateinit var buyCurrency: String
    lateinit var sellCurrency: String
    var resultingRate: FxRate? = null
}
