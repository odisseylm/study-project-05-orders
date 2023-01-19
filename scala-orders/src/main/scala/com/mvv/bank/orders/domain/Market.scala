package com.mvv.bank.orders.domain

import scala.language.strictEquality
import javax.annotation.Untainted
import javax.annotation.concurrent.Immutable
import com.mvv.utils.{isNotNull, isNull, require, requireNotNull, isBlank, isNullOrBlank}
import com.mvv.collections.in
import com.mvv.log.safe

import java.time.ZoneId
import java.time.LocalTime
import java.time.LocalDate
import java.time.ZonedDateTime
import scala.util.matching.Regex

import scala.annotation.unused


@Untainted @Immutable
class MarketSymbol private (
    //@param:Tainted @field:Untainted
    val value: String
  ) extends Equals derives CanEqual :
  validateMarketSymbol(value)

  @Untainted
  override def toString: String = value
  override def hashCode: Int = value.hashCode
  override def canEqual(other: Any): Boolean = other.isInstanceOf[MarketSymbol]
  override def equals(other: Any): Boolean = other match
    case that: MarketSymbol => (that canEqual this) && this.value == that.value
    case _ => false


object MarketSymbol :
    //@JvmStatic
    def of(marketSymbol: String): MarketSymbol = MarketSymbol(marketSymbol)
    // standard java method to get from string. It can help to integrate with other java frameworks.
    //@JvmStatic
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


//given CanEqual[CharSequence, Null] = CanEqual.derived
//given CanEqual[Null, CharSequence] = CanEqual.derived
given CanEqual[CharSequence|Null, Null] = CanEqual.derived
given CanEqual[Null, CharSequence|Null] = CanEqual.derived
//given CanEqual[CharSequence, Null] = CanEqual.derived
//given CanEqual[Null, CharSequence] = CanEqual.derived
//given CanEqual[CharSequence|Null, Null] = CanEqual.derived
//given CanEqual[Null, CharSequence|Null] = CanEqual.derived

private def validateMarketSymbol(marketSymbol: CharSequence|Null): Unit =
  if (marketSymbol == null || marketSymbol.isBlank ||
    (marketSymbol.length > MARKET_SYMBOL_MAX_LENGTH) ||
    !marketSymbolPattern.matches(marketSymbol))
    throw IllegalArgumentException(s"Invalid market symbol [${marketSymbol.safe}].")
