package com.mvv.bank.orders.rest.entities

import java.math.BigDecimal


data class Amount (
    val value: BigDecimal,
    val currency: String,
) {
    override fun toString(): String = "$value $currency"

    companion object {
        // to have API similar to domain API
        @JvmStatic fun of(value: BigDecimal, currency: String) = Amount(value, currency)
    }
}
