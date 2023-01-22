package com.mvv.bank.orders.domain

import java.time.{LocalDate, LocalTime, Instant, ZoneId, ZonedDateTime}


trait Quote :
  def product: String // symbol for this market; see https://www.investopedia.com/terms/s/stocksymbol.asp
  def market: MarketSymbol

  def timestamp: ZonedDateTime
  // date/time in market/exchange timezone
  def marketDate: LocalDate
  def marketTime: LocalTime

  def bid: Amount
  def ask: Amount
