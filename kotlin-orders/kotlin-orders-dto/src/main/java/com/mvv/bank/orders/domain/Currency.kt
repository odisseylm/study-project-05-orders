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
        const val MIN_LENGTH: Int = 3
        const val MAX_LENGTH: Int = 3 // ??? probably it can be 4 for crypto ???

        @JvmStatic // standard java method to get from string. It can help to integrate with other frameworks.
        fun valueOf(currency: String) = Currency(currency)
        @JvmStatic // short valueOf version
        fun of(currency: String) = Currency(currency)
    }
}

private const val CURRENCY_PAIR_SEPARATOR: Char = '_'

// Now we do not use 'value class' because it is fully not compatible with java
// (we need java now at least for using with mapstruct)
data class CurrencyPair (
    val base: Currency,
    val counter: Currency,
    ) {

    private val asString = "${base}_${counter}"

    // actually it can be without separator or the following '|', '/' can be used (what is better?)
    override fun toString() = asString

    // optimization
    override fun equals(other: Any?): Boolean = (other is CurrencyPair) && other.asString == this.asString
    override fun hashCode(): Int = asString.hashCode()

    companion object {
        const val MIN_LENGTH: Int = Currency.MIN_LENGTH * 2 + 1
        const val MAX_LENGTH: Int = Currency.MAX_LENGTH * 2 + 1

        @JvmStatic // standard java method to get from string. It can help to integrate with other frameworks.
        fun valueOf(currencyPair: String) = parseCurrencyPair(currencyPair)
        @JvmStatic // short valueOf version
        fun of(currencyPair: String) = parseCurrencyPair(currencyPair)
    }
}

private fun parseCurrencyPair(currencyPair: String): CurrencyPair {
    check(currencyPair.length in CurrencyPair.MIN_LENGTH..CurrencyPair.MAX_LENGTH) {
        "Invalid currency pair [${currencyPair.safe}] (length should be in range ${CurrencyPair.MIN_LENGTH}..${CurrencyPair.MAX_LENGTH})." }

    val currenciesList: List<String> = currencyPair.split(CURRENCY_PAIR_SEPARATOR)
    check(currenciesList.size == 2) {
        "Invalid currency pair [${currencyPair.safe}] (only one '$CURRENCY_PAIR_SEPARATOR' is expected)." }

    try {
        return CurrencyPair(Currency(currenciesList[0]), Currency(currenciesList[1]))
    }
    catch (ex: Exception) {
        throw IllegalArgumentException("Invalid currency pair [${currencyPair.safe}].", ex)
    }
}

private fun isValidCurrency(currency: String?): Boolean =
    // see https://en.wikipedia.org/wiki/ISO_4217
    // see https://www.investopedia.com/terms/i/isocurrencycode.asp
    currency != null
            && currency.length in Currency.MIN_LENGTH..Currency.MAX_LENGTH
            && currency.all { ch -> ch in 'A'..'Z' }


private fun validateCurrency(currency: String?) {
    check(isValidCurrency(currency)) { "Invalid currency [${currency.safe}]." }
}
