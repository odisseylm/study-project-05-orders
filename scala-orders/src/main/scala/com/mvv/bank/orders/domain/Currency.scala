package com.mvv.bank.orders.domain

import scala.language.strictEquality
import javax.annotation.Untainted
import javax.annotation.concurrent.Immutable
import com.mvv.utils.{isNotNull, isNull, require, requireNotNull}
import com.mvv.collections.in
import com.mvv.log.Logs.safe

import scala.annotation.unused


@Untainted @Immutable
case class Currency private (value: String) extends Equals derives CanEqual :
  validateCurrency(value)
  @Untainted
  override def toString: String = this.value
  override def hashCode: Int = this.value.hashCode
  override def canEqual(other: Any): Boolean = other.isInstanceOf[Currency]
  override def equals(other: Any): Boolean = other match
    case that: Currency => (that canEqual this) && this.value == that.value
    case _ => false



//noinspection ScalaUnusedSymbol
object Currency :
  val MIN_LENGTH: Int = 3
  val MAX_LENGTH: Int = 3 // ??? probably it can be 4 for crypto ???
  val LENGTH_RANGE: Range = Currency.MIN_LENGTH to Currency.MAX_LENGTH

  def apply(currency: String): Currency = of(currency)

  def of (currency: String): Currency = new Currency(currency)
  // standard java method to get from string. It can help to integrate with other java frameworks.
  //@JvmStatic
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
  override def equals(other: Any): Boolean = other match
    case that: CurrencyPair => (that canEqual this) && this.asString == that.asString
    case _ => false

  def copy(
    base: Currency = this.base,
    counter: Currency = this.counter,
  ): CurrencyPair = CurrencyPair.of(base, counter)

  def oppositeCurrency(currency: Currency): Currency =
    currency match
      case this.base    => this.counter
      case this.counter => this.base
      case _ => throw IllegalArgumentException(s"No opposite currency to $currency in $this.")

  def inverted: CurrencyPair = CurrencyPair.of(base = this.counter, counter = this.base)



//noinspection ScalaUnusedSymbol,ScalaWeakerAccess
// @unused
object CurrencyPair :
  val MIN_LENGTH: Int = Currency.MIN_LENGTH * 2 + 1
  val MAX_LENGTH: Int = Currency.MAX_LENGTH * 2 + 1

  //@JvmStatic
  def of(base: Currency, counter: Currency): CurrencyPair = new CurrencyPair(base, counter)
  //@JvmStatic
  def of(base: String, counter: String): CurrencyPair = of(Currency.of(base), Currency.of(counter))
  //@JvmStatic
  def of (currencyPair: String): CurrencyPair = parseCurrencyPair(currencyPair)
  // standard java method to get from string. It can help to integrate with other java frameworks.
  //@JvmStatic
  def valueOf (currencyPair: String): CurrencyPair = parseCurrencyPair(currencyPair)

  import com.mvv.bank.orders.domain.Currency.{EUR, UAH, USD}

  // popular ones
  val USD_EUR: CurrencyPair = of(USD, EUR)
  val EUR_USD: CurrencyPair = of(EUR, USD)

  val USD_UAH: CurrencyPair = of(USD, UAH)
  val UAH_USD: CurrencyPair = of(UAH, USD)

  val EUR_UAH: CurrencyPair = of(EUR, UAH)
  //@JvmStatic
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
    //&& currency.forall { ch => ch.in('A' to 'Z') }
    //&& currency.forall { ch => ch in ('A' to 'Z') }
    //&& currency.nn.forall { ch => 'A' to 'Z' contains ch }
    && currency.nn.forall { ch => 'A' <= ch && ch <= 'Z' }


private def parseCurrencyPair(string: String|Null): CurrencyPair = {
  val currencyPair = requireNotNull(string, s"Invalid currency pair [${string.safe}].")

  if CurrencyPair.MIN_LENGTH == CurrencyPair.MAX_LENGTH then
    require(currencyPair.length == CurrencyPair.MAX_LENGTH,
      s"Invalid currency pair [${currencyPair.safe}] (length should be ${CurrencyPair.MAX_LENGTH}).")
  else
    require(CurrencyPair.MIN_LENGTH to CurrencyPair.MAX_LENGTH contains currencyPair.length,
      s"Invalid currency pair [${currencyPair.safe}] (length should be in range ${CurrencyPair.MIN_LENGTH}..${CurrencyPair.MAX_LENGTH})." )

  val currenciesList: Array[String] = currencyPair.split(CURRENCY_PAIR_SEPARATOR)

  require(currenciesList.length == 2,
    s"Invalid currency pair [${currencyPair.safe}] (format like 'USD${CURRENCY_PAIR_SEPARATOR}EUR' is expected)." )

  try { CurrencyPair.of(Currency.of(currenciesList(0)), Currency.of(currenciesList(1))) }
  catch { case ex: Exception => throw IllegalArgumentException("Invalid currency pair [${currencyPair.safe}].", ex) }
}
