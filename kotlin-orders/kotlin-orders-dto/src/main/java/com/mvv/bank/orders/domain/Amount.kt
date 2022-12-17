package com.mvv.bank.orders.domain

import com.mvv.bank.shared.log.safe
import java.math.BigDecimal


data class Amount (
    val amount: BigDecimal,
    val currency: Currency,
) {
    override fun toString(): String = "$amount $currency"

    companion object {
        @JvmStatic // standard java method to get from string. It can help to integrate with other frameworks.
        fun valueOf(currencyPair: String) = parseAmount(currencyPair)
        @JvmStatic // short valueOf version
        fun of(currencyPair: String) = parseAmount(currencyPair)
    }
}


private const val MAX_AMOUNT_LENGTH = 1000 + 1 + Currency.MAX_LENGTH

private fun parseAmount(amount: String): Amount {
    try {
        check(amount.length <= MAX_AMOUNT_LENGTH) {
            "Too long amount string [${amount.safe}] (${amount.length})" }
        val strCurrency = amount.substringAfterLast(' ', "")
        val currency = Currency(strCurrency)

        val strAmount = amount.substringBeforeLast(' ')
        return Amount(BigDecimal(strAmount), currency)
    }
    catch (ex: Exception) {
        throw IllegalArgumentException("Error of parsing amount ${amount.safe}.", ex)
    }
}
