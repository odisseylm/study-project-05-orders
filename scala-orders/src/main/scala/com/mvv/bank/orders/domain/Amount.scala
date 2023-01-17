package com.mvv.bank.orders.domain

import scala.language.strictEquality
import javax.annotation.Untainted
import javax.annotation.concurrent.Immutable

import com.mvv.utils.require
import com.mvv.log.Logs.safe
import com.mvv.utils.ifNull
import com.mvv.utils.isNotBlank
import com.mvv.utils.!!

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.StringUtils.{substringAfterLast, substringBeforeLast}

//import scala.Predef.String


@Untainted @Immutable
case class Amount private (
  value: BigDecimal,
  currency: Currency,
  ) extends Equals derives CanEqual :
  override def toString: String = s"$value $currency"
  override def hashCode: Int = 31 * value.hashCode + currency.hashCode
  override def canEqual(other: Any): Boolean = other.isInstanceOf[Amount]
  override def equals(other: Any): Boolean = other match
    case that: Amount => (that canEqual this) && this.currency == that.currency && (this.value.compare(that.value) == 0)
    case _ => false


object Amount :
  // scala style
  def apply(value: BigDecimal, currency: Currency): Amount = of(value, currency)

  //@JvmStatic
  def of(value: BigDecimal, currency: Currency): Amount = new Amount(value, currency)
  //@JvmStatic
  def of(value: BigDecimal, currency: String): Amount = of(value, Currency.of(currency))

  // TODO: remove or rename, since it should be impl dependent on 'locals'
  //@JvmStatic
  def of(amount: String): Amount = parseAmount(amount)
  // standard java method to get from string. It can help to integrate with other java frameworks.
  //@JvmStatic
  def valueOf(amount: String): Amount = parseAmount(amount)



private val MAX_AMOUNT_LENGTH = 1000 + 1 + Currency.MAX_LENGTH

private def parseAmount(amount: String): Amount = {
  try {
    require(amount.length <= MAX_AMOUNT_LENGTH, s"Too long amount string [${amount.safe}] (${amount.length}).")

    val strCurrency = substringAfterLast(amount, ' ').ifNull("")
    require(strCurrency.isNotBlank, s"Amount should have currency at the end (format like '155.46 USD' is expected).")

    val strAmount = substringBeforeLast(amount, " ").!!
    Amount.of(BigDecimal(strAmount), Currency.of(strCurrency))
  }
  catch { case ex: Exception => throw IllegalArgumentException(s"Error of parsing amount [${amount.safe}].", ex) }
}
