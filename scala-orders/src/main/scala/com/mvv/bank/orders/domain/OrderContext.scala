package com.mvv.bank.orders.domain

import java.time.{Clock, Instant, ZoneId, ZonedDateTime}


trait DateTimeService :
  val clock: Clock
  val zoneId: ZoneId
  def now(): Instant


trait GeneralContext :
  val dateTimeService: DateTimeService


trait OrderContext extends GeneralContext :
  val market: Market

  /** Returns current data-time as ZonedDateTime with some (unspecified, can be system/server or client) time-zone. */
  def now(): ZonedDateTime = dateTimeService.now().atZone(dateTimeService.zoneId).nn

  /** Returns current data-time as ZonedDateTime with market time-zone. */
  def nowOnMarket: ZonedDateTime = now().withZoneSameInstant(market.zoneId).nn


object OrderContext :
  def create(
    dateTimeService: DateTimeService,
    market: Market,
  ) : OrderContext = OrderContextImpl(
    dateTimeService = dateTimeService,
    market = market,
  )


private case class OrderContextImpl (
    override val dateTimeService: DateTimeService,
    override val market: Market,
  ) extends OrderContext
