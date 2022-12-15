package com.mvv.bank.orders.domain

import com.mvv.bank.shared.log.safe


// Now we do not use 'value class' because it is fully not compatible with java
// (we need java now at least for using with mapstruct)
data class Currency ( val value: String ) {
    init {
        validateCurrency(value)
    }

    override fun toString() = value

    companion object {
        @JvmStatic // standard java method to get from string. It can help to integrate with other frameworks.
        fun valueOf(currency: String) = Currency(currency)
        @JvmStatic // short valueOf version
        fun of(currency: String) = Currency(currency)
    }
}

private const val CURRENCY_PAIR_SEPARATOR = '_'

// Now we do not use 'value class' because it is fully not compatible with java
// (we need java now at least for using with mapstruct)
data class CurrencyPair (
    val base: Currency,
    val counter: Currency,
    ) {

    // actually it can be without separator or the following '|', '/' can be used (what is better?)
    override fun toString() = "${base}_${counter}"

    companion object {
        @JvmStatic // standard java method to get from string. It can help to integrate with other frameworks.
        fun valueOf(currencyPair: String) = parseCurrencyPair(currencyPair)
        @JvmStatic // short valueOf version
        fun of(currencyPair: String) = parseCurrencyPair(currencyPair)
    }
}

private fun parseCurrencyPair(currencyPair: String): CurrencyPair {
    check(currencyPair.length == 7 && currencyPair[3] == CURRENCY_PAIR_SEPARATOR) {
        "Invalid currency pair [${currencyPair.safe}]." }

    val base = currencyPair.substring(0, 3)
    val counter = currencyPair.substring(4)

    check(isValidCurrency(base) && isValidCurrency(counter)) {
        "Invalid currency pair [${currencyPair.safe}]." }

    return CurrencyPair(Currency(base), Currency(counter))
}

fun isValidCurrency(currency: String?): Boolean =
    // see https://en.wikipedia.org/wiki/ISO_4217
    // see https://www.investopedia.com/terms/i/isocurrencycode.asp
    currency != null && currency.length == 3 && currency.all { ch -> ch in 'A'..'Z' }


private fun validateCurrency(currency: String?) {
    check(isValidCurrency(currency)) { "Invalid currency [${currency.safe}]." }
}
