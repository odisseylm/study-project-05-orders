package com.mvv.bank.orders.domain

import com.mvv.bank.log.safe
import java.math.BigDecimal
import javax.annotation.Untainted
import javax.annotation.concurrent.Immutable


@Untainted @Immutable
class Amount private constructor (
    val value: BigDecimal,
    val currency: Currency,
) {
    @Untainted
    override fun toString(): String = "$value $currency"
    override fun equals(other: Any?): Boolean =
        (this === other) ||
        ((this.javaClass == other?.javaClass) && (currency == (other as Amount).currency) &&
         (this.value.compareTo(other.value) == 0))
    override fun hashCode(): Int = 31 * value.hashCode() + currency.hashCode()

    companion object {
        @JvmStatic fun of(amount: String) = parseAmount(amount)
        @JvmStatic fun of(amount: BigDecimal, currency: Currency) = Amount(amount, currency)
        @JvmStatic fun of(amount: String, currency: Currency) = of(BigDecimal(amount), currency)

        // standard java method to get from string. It can help to integrate with other java frameworks.
        @JvmStatic fun valueOf(amount: String) = parseAmount(amount)
    }
}

operator fun Amount.times(m: BigDecimal) = Amount.of(this.value * m, this.currency)
operator fun BigDecimal.times(amount: Amount) = Amount.of(this * amount.value, amount.currency)



private const val MAX_AMOUNT_LENGTH = 1000 + 1 + Currency.MAX_LENGTH

private fun parseAmount(amount: String): Amount {
    try {
        check(amount.length <= MAX_AMOUNT_LENGTH) {
            "Too long amount string [${amount.safe}] (${amount.length})" }

        val strCurrency = amount.substringAfterLast(' ', "")
        check(strCurrency.isNotBlank()) { "Amount should have currency at the end (format like '155.46 USD' is expected)." }

        val strAmount = amount.substringBeforeLast(' ')
        return Amount.of(BigDecimal(strAmount), Currency.of(strCurrency))
    }
    catch (ex: Exception) {
        throw IllegalArgumentException("Error of parsing amount ${amount.safe}.", ex)
    }
}
