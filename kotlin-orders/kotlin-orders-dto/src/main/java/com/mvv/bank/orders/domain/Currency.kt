package com.mvv.bank.orders.domain

import com.mvv.bank.log.safe
import com.mvv.bank.orders.domain.Currency.Companion.EUR
import com.mvv.bank.orders.domain.Currency.Companion.UAH
import com.mvv.bank.orders.domain.Currency.Companion.USD
import javax.annotation.Tainted
import javax.annotation.Untainted
import javax.annotation.concurrent.Immutable


// Now we do not use 'value class' because it is fully not compatible with java
// (we need java now at least for using with mapstruct)
@Untainted @Immutable
class Currency private constructor (@param:Tainted @field:Untainted val value: String) {
    init { validateCurrency(value) }
    @Untainted
    override fun toString() = value
    override fun hashCode(): Int = value.hashCode()
    override fun equals(other: Any?): Boolean =
        (this === other) ||
        ((javaClass == other?.javaClass) && (value == (other as Currency).value))

    companion object {
        const val MIN_LENGTH: Int = 3
        const val MAX_LENGTH: Int = 3 // ??? probably it can be 4 for crypto ???

        operator fun invoke(currency: String) = Currency(currency) // T O D O: add caching of predefined currencies

        // for java (MapStruct so on)
        @JvmStatic fun of(currency: String) = invoke(currency)
        // standard java method to get from string. It can help to integrate with other java frameworks.
        @JvmStatic fun valueOf(currency: String) = invoke(currency)

        // popular ones
        val UAH = Currency("UAH")
        val USD = Currency("USD")
        val EUR = Currency("EUR")
        val JPY = Currency("JPY")
        // feel free to add other popular ones...
    }
}


private const val CURRENCY_PAIR_SEPARATOR: Char = '_'

// Now we do not use 'value class' because it is fully not compatible with java
// (we need java now at least for using with mapstruct)
@Untainted @Immutable
class CurrencyPair private constructor (
    val base: Currency,
    val counter: Currency,
    ) {
    @Untainted
    private val asString = "${base}${CURRENCY_PAIR_SEPARATOR}${counter}"
    @Untainted
    override fun toString() = asString
    override fun equals(other: Any?): Boolean = (other is CurrencyPair) && other.asString == this.asString
    override fun hashCode(): Int = asString.hashCode()

    fun copy(
        base: Currency = this.base,
        counter: Currency = this.counter,
    ): CurrencyPair = of(base, counter)

    fun oppositeCurrency(currency: Currency): Currency =
        when (currency) {
            this.base    -> this.counter
            this.counter -> this.base
            else -> throw IllegalArgumentException("No opposite currency to $currency in $this.")
        }

    val inverted get(): CurrencyPair = of(base = this.counter, counter = this.base)

    @Suppress("unused")
    companion object {
        const val MIN_LENGTH: Int = Currency.MIN_LENGTH * 2 + 1
        const val MAX_LENGTH: Int = Currency.MAX_LENGTH * 2 + 1

        operator fun invoke(base: Currency, counter: Currency) = CurrencyPair(base, counter)

        // for Java (MapStruct so on)
        @JvmStatic fun of(base: Currency, counter: Currency) = invoke(base, counter)
        @JvmStatic fun of(base: String, counter: String) = invoke(Currency.of(base), Currency.of(counter))
        // standard java method to get from string. It can help to integrate with other java frameworks.
        @JvmStatic fun valueOf(currencyPair: String) = parseCurrencyPair(currencyPair)

        // popular ones
        val USD_EUR = of(USD, EUR)
        val EUR_USD = of(EUR, USD)

        val USD_UAH = of(USD, UAH)
        val UAH_USD = of(UAH, USD)

        val EUR_UAH = of(EUR, UAH)
        //@JvmStatic
        val UAH_EUR = of(UAH, EUR)

        // feel free to add other popular ones...
    }
}

fun CurrencyPair.containsCurrency(currency: Currency): Boolean =
    this.base == currency || this.counter == currency
fun CurrencyPair.containsCurrencies(ccy1: Currency, ccy2: Currency): Boolean =
    containsCurrency(ccy1) && containsCurrency(ccy2)


private fun parseCurrencyPair(currencyPair: String): CurrencyPair {
    require(currencyPair.length in CurrencyPair.MIN_LENGTH..CurrencyPair.MAX_LENGTH) {
        "Invalid currency pair [${currencyPair.safe}] (length should be in range ${CurrencyPair.MIN_LENGTH}..${CurrencyPair.MAX_LENGTH})." }

    val currenciesList: List<String> = currencyPair.split(CURRENCY_PAIR_SEPARATOR)

    require(currenciesList.size == 2) {
        "Invalid currency pair [${currencyPair.safe}] (format like 'USD${CURRENCY_PAIR_SEPARATOR}EUR' is expected)." }


    return try { CurrencyPair.of(Currency.of(currenciesList[0]), Currency.of(currenciesList[1])) }
    catch (ex: Exception) { throw IllegalArgumentException("Invalid currency pair [${currencyPair.safe}].", ex) }
}

private fun isValidCurrency(currency: String?): Boolean =
    // see https://en.wikipedia.org/wiki/ISO_4217
    // see https://www.investopedia.com/terms/i/isocurrencycode.asp
    currency != null
            && currency.length in Currency.MIN_LENGTH..Currency.MAX_LENGTH
            && currency.all { ch -> ch in 'A'..'Z' }


private fun validateCurrency(currency: String?) {
    require(isValidCurrency(currency)) { "Invalid currency [${currency.safe}]." }
}
