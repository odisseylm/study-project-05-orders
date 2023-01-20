//noinspection ScalaUnusedSymbol // T O D O: remove after adding test and so on
package com.mvv.bank.orders.domain

import scala.annotation.meta.{getter, param}
import scala.language.strictEquality
//
import javax.annotation.Untainted
import javax.annotation.Tainted
import javax.annotation.concurrent.Immutable
import com.mvv.nullables.{isNotNull, isNull}
import com.mvv.utils.{require, requireNotNull, equalImpl}
import com.mvv.collections.in
import com.mvv.log.safe
//
import scala.annotation.unused


@Untainted @Immutable
case class Currency private (
  @(Tainted @param) @(Untainted @getter)
  value: String) extends Equals derives CanEqual :
  validateCurrency(value)
  @Untainted
  override def toString: String = this.value


/*
@Untainted @Immutable
class Currency private (value: String) extends Equals derives CanEqual :
  validateCurrency(value)
  @Untainted
  override def toString: String = this.value
  override def hashCode: Int = this.value.hashCode
  infix override def canEqual(other: Any): Boolean = other.isInstanceOf[Currency]
  // it causes warning "pattern selector should be an instance of Matchable" with Scala 3
  //override def equals(other: Any): Boolean = other match
  //  case that: Currency => that.canEqual(this) && this.value == that.value
  //  case _ => false
  override def equals(other: Any): Boolean =
    // it is inlined and have resulting byte code similar to code with 'match'
    equalImpl(this, other) { _.value == _.value }
*/


given givenCanEqual_Currency_Null: CanEqual[Currency, Null] = CanEqual.derived
given givenCanEqual_CurrencyNull_Null: CanEqual[Currency|Null, Null] = CanEqual.derived
given givenCanEqual_CurrencyNull_Currency: CanEqual[Currency|Null, Currency] = CanEqual.derived
given givenCanEqual_Null_Currency: CanEqual[Null, Currency] = CanEqual.derived
given givenCanEqual_Null_CurrencyNull: CanEqual[Null, Currency|Null] = CanEqual.derived
given givenCanEqual_Currency_CurrencyNull: CanEqual[Currency, Currency|Null] = CanEqual.derived



//noinspection ScalaUnusedSymbol
object Currency :
  val MIN_LENGTH: Int = 3
  val MAX_LENGTH: Int = 3 // ??? probably it can be 4 for crypto ???
  val LENGTH_RANGE: Range = Currency.MIN_LENGTH to Currency.MAX_LENGTH

  def apply(currency: String): Currency = of(currency)

  def of (currency: String): Currency = new Currency(currency)
  // standard java method to get from string. It can help to integrate with other java frameworks.
  def valueOf (currency: String): Currency = of(currency)

  // popular ones
  val UAH = new Currency("UAH")
  val USD = new Currency("USD")
  val EUR = new Currency("EUR")
  val JPY = new Currency("JPY")
  // feel free to add other popular ones...


private val CURRENCY_PAIR_SEPARATOR: Char = '_'



//noinspection ScalaUnusedSymbol // TODO: add tests and remove this comment

@Untainted @Immutable
case class CurrencyPair private (
  base: Currency,
  counter: Currency,
  ) extends Equals derives CanEqual :
  @Untainted
  private val asString = s"$base$CURRENCY_PAIR_SEPARATOR$counter"
  @Untainted
  override def toString: String = asString

/*
@Untainted @Immutable
class CurrencyPair private (
  val base: Currency,
  val counter: Currency,
  ) extends Equals derives CanEqual :
  @Untainted
  private val asString = s"$base$CURRENCY_PAIR_SEPARATOR$counter"
  @Untainted
  override def toString: String = asString

  override def hashCode: Int = asString.hashCode
  override def canEqual(other: Any): Boolean = other.isInstanceOf[CurrencyPair]
  // it causes warning "pattern selector should be an instance of Matchable" with Scala 3
  //override def equals(other: Any): Boolean = other match
  //  case that: CurrencyPair => that.canEqual(this) && this.asString == that.asString
  //  case _ => false
  override def equals(other: Any): Boolean =
    // it is inlined and have resulting byte code similar to code with 'match'
    equalImpl(this, other) { (v1, v2) => v1.base == v2.base && v1.counter == v2.counter }

  def copy(
    base: Currency = this.base,
    counter: Currency = this.counter,
  ): CurrencyPair = CurrencyPair.of(base, counter)
*/


extension (self: CurrencyPair)
  def oppositeCurrency(oppositeTo: Currency): Currency =
    oppositeTo match
      case self.base    => self.counter
      case self.counter => self.base
      case _ => throw IllegalArgumentException(s"No opposite currency to $oppositeTo in $self.")

  def inverted: CurrencyPair = CurrencyPair.of(base = self.counter, counter = self.base)


//noinspection ScalaUnusedSymbol,ScalaWeakerAccess
// @unused
object CurrencyPair :
  val MIN_LENGTH: Int = Currency.MIN_LENGTH * 2 + 1
  val MAX_LENGTH: Int = Currency.MAX_LENGTH * 2 + 1

  def of(base: Currency, counter: Currency): CurrencyPair = new CurrencyPair(base, counter)
  def of(base: String, counter: String): CurrencyPair = of(Currency.of(base), Currency.of(counter))
  def of (currencyPair: String): CurrencyPair = parseCurrencyPair(currencyPair)
  // standard java method to get from string. It can help to integrate with other java frameworks.
  def valueOf (currencyPair: String): CurrencyPair = parseCurrencyPair(currencyPair)

  import com.mvv.bank.orders.domain.Currency.{EUR, UAH, USD}

  // popular ones
  val USD_EUR: CurrencyPair = of(USD, EUR)
  val EUR_USD: CurrencyPair = of(EUR, USD)

  val USD_UAH: CurrencyPair = of(USD, UAH)
  val UAH_USD: CurrencyPair = of(UAH, USD)

  val EUR_UAH: CurrencyPair = of(EUR, UAH)
  val UAH_EUR: CurrencyPair = of(UAH, EUR)

  // feel free to add other popular ones...


extension (currencyPair: CurrencyPair)
  def containsCurrency (currency: Currency): Boolean =
    currencyPair.base == currency || currencyPair.counter == currency
  def containsCurrencies (ccy1: Currency, ccy2: Currency): Boolean =
    containsCurrency (ccy1) && containsCurrency (ccy2)


private def validateCurrency(currency: String|Null): Unit =
  require(isValidCurrency(currency), s"Invalid currency [${currency.safe}]." )


private def isValidCurrency(currency: String|Null): Boolean =
  // see https://en.wikipedia.org/wiki/ISO_4217
  // see https://www.investopedia.com/terms/i/isocurrencycode.asp
  isNotNull(currency)
    && (Currency.LENGTH_RANGE contains currency.nn.length)
    //&& currency.nn.forall { ch => 'A' to 'Z' contains ch } // resulting byte code is big and awful ((
    && currency.nn.forall { ch => 'A' <= ch && ch <= 'Z' }


private def parseCurrencyPair(stringCurrencyPair: String|Null): CurrencyPair = {
  val str = requireNotNull(stringCurrencyPair, s"Invalid currency pair [${stringCurrencyPair.safe}].")

  val minLen = CurrencyPair.MIN_LENGTH
  val maxLen = CurrencyPair.MAX_LENGTH
  val separator = CURRENCY_PAIR_SEPARATOR

  if minLen == maxLen then
    require(str.length == maxLen,
      s"Invalid currency pair [${str.safe}] (length should be $maxLen).")
  else
    require(minLen to maxLen contains str.length,
      s"Invalid currency pair [${str.safe}] (length should be in range $minLen..$maxLen)." )

  val curList: Array[String] = str.split(separator)

  require(curList.length == 2,
    s"Invalid currency pair [${str.safe}] (format like 'USD${separator}EUR' is expected)." )

  try { CurrencyPair.of(Currency.of(curList(0)), Currency.of(curList(1))) }
  catch { case ex: Exception => throw IllegalArgumentException("Invalid currency pair [${currencyPair.safe}].", ex) }
}


private def testOfEqualWithNull(): Unit = {
  val cur = Currency("USD")
  val curOrNull1: Currency|Null = Currency("USD")
  val curOrNull2: Currency|Null = null

  if (cur == null) println("1")
  if (null == cur) println("2")

  if (curOrNull1 == null) println("1")
  if (null == curOrNull1) println("2")

  if (curOrNull2 == null) println("1")
  if (null == curOrNull2) println("2")

  if (curOrNull2 == cur) println("1")
  if (cur == curOrNull2) println("2")
}
