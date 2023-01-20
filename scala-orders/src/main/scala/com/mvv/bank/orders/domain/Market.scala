//noinspection ScalaUnusedSymbol // T O D O: remove after adding test and so on
package com.mvv.bank.orders.domain

import javax.annotation.Tainted
import scala.annotation.targetName
import scala.language.strictEquality
//
import scala.annotation.meta.{field, getter, param}
import scala.annotation.unused
import scala.util.matching.Regex
//
import java.time.ZoneId
import java.time.LocalTime
import java.time.LocalDate
import java.time.ZonedDateTime
//
import javax.annotation.Tainted
import javax.annotation.Untainted
import javax.annotation.concurrent.Immutable
//
import com.mvv.nullables.{isNotNull, isNull}
import com.mvv.utils.{require, requireNotNull, isBlank, isNullOrBlank, equalImpl}
import com.mvv.collections.in
import com.mvv.nullables.given
import com.mvv.log.safe


@Untainted @Immutable
case class MarketSymbol private (
  @(Tainted @param) @(Untainted @field @getter)
  value: String
  ) derives CanEqual :
  validateMarketSymbol(value)
  @Untainted
  override def toString: String = value

/*
@Untainted @Immutable
class MarketSymbol private (
  @(Tainted @param) @(Untainted @field @getter)
  val value: String
  ) extends Equals derives CanEqual :
  validateMarketSymbol(value)

  @Untainted
  override def toString: String = value
  override def hashCode: Int = value.hashCode
  override def canEqual(other: Any): Boolean = other.isInstanceOf[MarketSymbol]
  // it causes warning "pattern selector should be an instance of Matchable" with Scala 3
  //override def equals(other: Any): Boolean = other match
  //  case that: MarketSymbol => that.canEqual(this) && this.value == that.value
  //  case _ => false
  override def equals(other: Any): Boolean =
  // it is inlined and have resulting byte code similar to code with 'match'
    equalImpl(this, other) { _.value == _.value }
*/

object MarketSymbol :
    def of(marketSymbol: String): MarketSymbol = MarketSymbol(marketSymbol)
    // standard java method to get from string. It can help to integrate with other java frameworks.
    def valueOf(marketSymbol: String): MarketSymbol = of(marketSymbol)


trait Market :
  val name: String
  val symbol: MarketSymbol
  val zoneId: ZoneId
  val description: String

  val defaultOpenTime: LocalTime  // inclusive
  val defaultCloseTime: LocalTime // exclusive

  def isWorkingDay(date: LocalDate): Boolean
  def openTime(date: LocalDate): LocalTime = if isWorkingDay(date) then defaultOpenTime else LocalTime.MIDNIGHT.nn
  def closeTime(date: LocalDate): LocalTime = if isWorkingDay(date) then defaultCloseTime else LocalTime.MIDNIGHT.nn


trait MarketProvider :
  def marketBySymbol(marketSymbol: MarketSymbol): Market


/*
Stock Exchange symbols:
 * NASDAQ
 * NYSE
*/


private val MARKET_SYMBOL_MAX_LENGTH = 25
//private val marketSymbolPattern = Regex("^[A-Z0-9\\-.]*\$")
private val marketSymbolPattern = Regex("") // TODO: fix regex


private def validateMarketSymbol(marketSymbol: CharSequence|Null): Unit =
  if (marketSymbol == null || marketSymbol.isBlank ||
    (marketSymbol.length > MARKET_SYMBOL_MAX_LENGTH) ||
    !marketSymbolPattern.matches(marketSymbol))
    throw IllegalArgumentException(s"Invalid market symbol [${marketSymbol.safe}].")
