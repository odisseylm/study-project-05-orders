package com.mvv.bank.orders.domain

import scala.language.strictEquality
import javax.annotation.Untainted
import javax.annotation.concurrent.Immutable
import com.mvv.nullables.ifNull
import com.mvv.utils.{require, requireNotBlank, equalImpl}
import com.mvv.log.safe
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.StringUtils.{substringAfterLast, substringBeforeLast}


// scala fixes BigDecimal and does not use trailing zero during equals/hashCode
// and we do not need to fix it manually
@Untainted @Immutable
case class Amount private (
  value: BigDecimal,
  currency: Currency,
  ) derives CanEqual :
  override def toString: String = s"$value $currency"

/*
@Untainted @Immutable
class Amount private (
  val value: BigDecimal,
  val currency: Currency,
  ) extends Equals derives CanEqual :
  //private val aa = value.stripTrailingZeros
  override def toString: String = s"$value $currency"
  override def hashCode: Int = 31 * value.hashCode + currency.hashCode
  override def canEqual(other: Any): Boolean = other.isInstanceOf[Amount]
  // it causes warning "pattern selector should be an instance of Matchable" with Scala 3
  //override def equals(other: Any): Boolean = other match
  //  case that: Amount => that.canEqual(this) && this.currency == that.currency && (this.value.compare(that.value) == 0)
  //  case _ => false
  override def equals(other: Any): Boolean =
    // it is inlined and have resulting byte code similar to code with 'match'
    // in scala BigDecimal performs equals by real value skipping trailing zeros!!!
    equalImpl(this, other) { (v1, v2) => v1.currency == v2.currency && v1.value == v2.value }
*/

object Amount :
  // scala style
  def apply(value: BigDecimal, currency: Currency): Amount = of(value, currency)

  def of(value: BigDecimal, currency: Currency): Amount = new Amount(value, currency)

  // TODO: remove or rename, since it should be impl dependent on 'locals'
  // standard java method to get from string. It can help to integrate with other java frameworks.
  def valueOf(amount: String): Amount = parseAmount(amount)



private val MAX_AMOUNT_LENGTH = 1000 + 1 + Currency.MAX_LENGTH

private def parseAmount(amountString: String|Null): Amount = {
  val amount: String = requireNotBlank(amountString, "Amount string is null/blank.")
  try {
    require(amount.length <= MAX_AMOUNT_LENGTH, s"Too long amount string [${amount.safe}] (${amount.length}).")

    val strCurrency = substringAfterLast(amount, ' ').ifNull("")
    requireNotBlank(strCurrency, s"Amount should have currency at the end (format like '155.46 USD' is expected).")

    val strAmount = substringBeforeLast(amount, " ").nn
    Amount(BigDecimal(strAmount), Currency.of(strCurrency))
  }
  catch { case ex: Exception => throw IllegalArgumentException(s"Error of parsing amount [${amount.safe}].", ex) }
}
