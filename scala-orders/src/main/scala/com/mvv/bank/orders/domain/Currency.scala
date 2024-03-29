//noinspection ScalaUnusedSymbol // T O D O: remove after adding test and so on
package com.mvv.bank.orders.domain

import scala.language.strictEquality
//
import scala.annotation.unused
import scala.annotation.meta.{field, getter, param}
//
import javax.annotation.{Tainted, Untainted}
import javax.annotation.concurrent.Immutable
//
import com.mvv.nullables.{isNotNull, isNull, NullableCanEqualGivens}
import com.mvv.utils.{require, requireNotNull, equalImpl}
import com.mvv.collections.in
import com.mvv.log.safe


@Untainted @Immutable
case class Currency private (
  @(Tainted @param) @(Untainted @field @getter)
  value: String) derives CanEqual :
  validateCurrency(value)
  @Untainted override def toString: String = this.value



object Currency extends NullableCanEqualGivens[Currency]:
  val MIN_LENGTH: Int = 3
  val MAX_LENGTH: Int = 3 // ??? probably it can be 4 for crypto ???
  val LENGTH_RANGE: Range = Currency.MIN_LENGTH to Currency.MAX_LENGTH

  // T O D O: optimize to return predefined instances instead of creating new ones
  def apply(@Tainted currency: String): Currency = new Currency(currency)

  // standard java methods to get from string. It can help to integrate with other java frameworks.
  def of(@Tainted currency: String): Currency = Currency(currency)
  def valueOf(@Tainted currency: String): Currency = Currency(currency)

  // popular ones
  val UAH = new Currency("UAH")
  val USD = new Currency("USD")
  val EUR = new Currency("EUR")
  val JPY = new Currency("JPY")
  // feel free to add other popular ones...


private val CURRENCY_PAIR_SEPARATOR: Char = '_'


@Untainted @Immutable
case class CurrencyPair private (
  base: Currency,
  counter: Currency,
  ) derives CanEqual :
  @Untainted private val asString = s"$base$CURRENCY_PAIR_SEPARATOR$counter"
  @Untainted override def toString: String = asString



object CurrencyPair extends NullableCanEqualGivens[CurrencyPair] :
  val MIN_LENGTH: Int = Currency.MIN_LENGTH * 2 + 1
  val MAX_LENGTH: Int = Currency.MAX_LENGTH * 2 + 1

  // T O D O: optimize to return predefined instances instead of creating new ones
  def apply(base: Currency, counter: Currency): CurrencyPair = new CurrencyPair(base, counter)
  def apply(@Tainted base: String, @Tainted counter: String): CurrencyPair = CurrencyPair(Currency(base), Currency(counter))

  // to use from java (for example from MapStruct)
  def of(base: Currency, counter: Currency): CurrencyPair = CurrencyPair(base, counter)
  def of(@Tainted base: String, @Tainted counter: String): CurrencyPair = CurrencyPair(Currency(base), Currency(counter))

  // standard java methods to get from string. It can help to integrate with other java frameworks.
  def of(@Tainted currencyPair: String): CurrencyPair = parseCurrencyPair(currencyPair)
  def valueOf (@Tainted currencyPair: String): CurrencyPair = parseCurrencyPair(currencyPair)

  import com.mvv.bank.orders.domain.Currency.{EUR, UAH, USD}

  extension (currencyPair: CurrencyPair)
    def oppositeCurrency(oppositeTo: Currency): Currency =
      oppositeTo match
        case currencyPair.base    => currencyPair.counter
        case currencyPair.counter => currencyPair.base
        case _ => throw IllegalArgumentException(s"No opposite currency to $oppositeTo in $currencyPair.")

    def inverted: CurrencyPair = CurrencyPair(base = currencyPair.counter, counter = currencyPair.base)
    def containsCurrency(currency: Currency): Boolean =
      currencyPair.base == currency || currencyPair.counter == currency
    def containsCurrencies(ccy1: Currency, ccy2: Currency): Boolean =
      containsCurrency(ccy1) && containsCurrency(ccy2)


  // popular ones
  val USD_EUR: CurrencyPair = new CurrencyPair(USD, EUR)
  val EUR_USD: CurrencyPair = new CurrencyPair(EUR, USD)

  val USD_UAH: CurrencyPair = new CurrencyPair(USD, UAH)
  val UAH_USD: CurrencyPair = new CurrencyPair(UAH, USD)

  val EUR_UAH: CurrencyPair = new CurrencyPair(EUR, UAH)
  val UAH_EUR: CurrencyPair = new CurrencyPair(UAH, EUR)

  // feel free to add other popular ones...



private def validateCurrency(@Tainted currency: String): Unit =
  require(isValidCurrency(currency), s"Invalid currency [${currency.safe}]." )


private def isValidCurrency(@Tainted currency: String): Boolean =
  // see https://en.wikipedia.org/wiki/ISO_4217
  // see https://www.investopedia.com/terms/i/isocurrencycode.asp
  isNotNull(currency)
    && (Currency.LENGTH_RANGE contains currency.nn.length)
    //&& currency.nn.forall { ch => 'A' to 'Z' contains ch } // resulting byte code is big and awful ((
    && currency.nn.forall { ch => 'A' <= ch && ch <= 'Z' }


private def parseCurrencyPair(@Tainted stringCurrencyPair: String): CurrencyPair = {
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

  try { CurrencyPair(Currency(curList(0)), Currency(curList(1))) }
  catch { case ex: Exception => throw IllegalArgumentException(s"Invalid currency pair [${str.safe}].", ex) }
}
