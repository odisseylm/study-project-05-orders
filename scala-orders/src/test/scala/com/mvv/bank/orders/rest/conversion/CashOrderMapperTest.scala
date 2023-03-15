package com.mvv.bank.orders.rest.conversion

import scala.language.unsafeNulls
import scala.math.BigDecimal as bd
import java.time.{LocalDate, LocalDateTime, LocalTime, ZonedDateTime}
import org.mapstruct.factory.Mappers
//
import org.junit.jupiter.api.Test

import com.mvv.bank.orders.domain.AbstractCashOrder.Base
import com.mvv.bank.orders.domain.test.predefined.{ TestPredefinedMarkets, TestPredefinedUsers }
import com.mvv.scala.mapstruct.getScalaMapStructMapper

import com.mvv.bank.orders.domain.{
  FxRateAsQuote,
  Amount as DomainAmount,
  FxRate as DomainFxRate,
  Currency as DomainCurrency,
  CurrencyPair as DomainCurrencyPair,
  Side as DomainSide,
  OrderType as DomainOrderType,
  OrderState as DomainOrderState,
  BuySellType as DomainBuySellType,
  CashStopOrder as DomainStopOrder,
  CashLimitOrder as DomainLimitOrder,
  CashMarketOrder as DomainMarketOrder,
  DailyExecutionType as DomainDailyExecutionType,
}
import com.mvv.bank.orders.rest.entities.{
  Side as DtoSide,
  Amount as DtoAmount,
  FxRate as DtoFxRate,
  CashOrder as DtoOrder,
  OrderType as DtoOrderType,
  OrderState as DtoOrderState,
  BuySellType as DtoBuySellType,
  DailyExecutionType as DtoDailyExecutionType,
}

import com.mvv.utils.kotlin.also

import com.mvv.bank.orders.rest.entities.CashOrderBuilder.fillOrder
import com.mvv.bank.orders.rest.conversion.j.JCashOrderMapper

import com.mvv.test.initField
import com.mvv.scala.test.assertj.kotlinStyle.{SoftAssertions, apply}
import com.mvv.scala.test.assertj.ImplicitSoftAssertions.*



class CashOrderMapperTest {
    private val testMarket = TestPredefinedMarkets.KYIV1
    private val testDate = LocalDate.of(2022, java.time.Month.DECEMBER, 23)
    private val testTime = LocalTime.of(13, 5)
    private val testDateTime = LocalDateTime.of(testDate, testTime)
    private val testTimestamp = ZonedDateTime.of(testDateTime, testMarket.zoneId)
    private val testUser = TestPredefinedUsers.USER1

    private val orderMapper: CashOrderMapper = getScalaMapStructMapper[CashOrderMapper]().clone()
         .also { it => initField(it, "_marketProvider", TestPredefinedMarkets) }
         .asInstanceOf[CashOrderMapper]


    @Test
    def limitOrder_domainToDto(): Unit = {

        val domainOrder = DomainLimitOrder(
            Base(
                id = None,
                user = testUser,
                side = DomainSide.CLIENT,
                buySellType = DomainBuySellType.BUY,
                buyCurrency = DomainCurrency.USD,
                sellCurrency = DomainCurrency.UAH,
                volume = bd("2000"),
                market = testMarket,
                orderState = DomainOrderState.TO_BE_PLACED,
            ),
            limitPrice = DomainAmount(bd("40.0"), DomainCurrency.UAH),
            dailyExecutionType = DomainDailyExecutionType.GTC,
        )

        val dtoOrder = orderMapper.toDto(domainOrder)

        SoftAssertions().apply {
            assertThat(dtoOrder.id).isEqualTo(None)
            assertThat(dtoOrder.orderType).isEqualTo(DtoOrderType.LIMIT_ORDER)
            assertThat(dtoOrder.side).isEqualTo(DtoSide.CLIENT)
            assertThat(dtoOrder.buySellType).isEqualTo(DtoBuySellType.BUY)
            assertThat(dtoOrder.buyCurrency).isEqualTo("USD")
            assertThat(dtoOrder.sellCurrency).isEqualTo("UAH")
            assertThat(dtoOrder.volume).isEqualTo(bd("2000"))
            assertThat(dtoOrder.limitPrice).isEqualTo(Some(DtoAmount(bd("40.0"), "UAH")))
            assertThat(dtoOrder.dailyExecutionType).isEqualTo(Some(DtoDailyExecutionType.GTC))
            assertThat(dtoOrder.market).isNotNull.isEqualTo(testMarket.symbol.value)
            assertThat(dtoOrder.orderState).isEqualTo(DtoOrderState.TO_BE_PLACED)
        }.assertAll()
    }


    @Test
    def stopOrder_domainToDto(): Unit = {

        val domainOrder = DomainStopOrder(
            Base(
                id = Option(567),
                user = testUser,
                side = DomainSide.CLIENT,
                buySellType = DomainBuySellType.BUY,
                buyCurrency = DomainCurrency.USD,
                sellCurrency = DomainCurrency.UAH,
                volume = bd("2000"),
                market = testMarket,
                resultingRate = Option(DomainFxRate(
                    testMarket, testTimestamp, DomainCurrencyPair.USD_UAH,
                    bid = bd("39.00"), ask = bd("39.50"),
                )),
                orderState = DomainOrderState.PLACED,
                placedAt = Option(ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]")),
            ),
            stopPrice = DomainAmount(bd("40.0"), DomainCurrency.UAH),
            dailyExecutionType = DomainDailyExecutionType.GTC,
        )

        val dtoOrder = orderMapper.toDto(domainOrder)

        SoftAssertions().apply {
            assertThat(dtoOrder.id).isEqualTo(Some(567))
            assertThat(dtoOrder.orderType).isEqualTo(DtoOrderType.STOP_ORDER)
            assertThat(dtoOrder.side).isEqualTo(DtoSide.CLIENT)
            assertThat(dtoOrder.buySellType).isEqualTo(DtoBuySellType.BUY)
            assertThat(dtoOrder.buyCurrency).isEqualTo("USD")
            assertThat(dtoOrder.sellCurrency).isEqualTo("UAH")
            assertThat(dtoOrder.volume).isEqualTo(bd("2000"))
            assertThat(dtoOrder.stopPrice).isEqualTo(Some(DtoAmount(bd("40.0"), "UAH")))
            assertThat(dtoOrder.dailyExecutionType).isEqualTo(Some(DtoDailyExecutionType.GTC))
            assertThat(dtoOrder.market).isNotNull.isEqualTo(testMarket.symbol.value)
            assertThat(dtoOrder.orderState).isEqualTo(DtoOrderState.PLACED)

            assertThat(dtoOrder.resultingRate).isEqualTo(
                Some(DtoFxRate(testMarket.symbol.value, testTimestamp, testDate, testTime,
                    "USD", "UAH", bid = bd("39.00"), ask = bd("39.50"))))
            assertThat(dtoOrder.placedAt).isEqualTo(Some(ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]")))
        }.assertAll()
    }


    @Test
    def marketOrder_domainToDto(): Unit = {

        val domainOrder = DomainMarketOrder(
            Base(
                id = Option(456),
                user = testUser,
                side = DomainSide.CLIENT,
                buySellType = DomainBuySellType.BUY,
                buyCurrency = DomainCurrency.USD,
                sellCurrency = DomainCurrency.UAH,
                volume = bd("2000"),
                market = testMarket,
                orderState = DomainOrderState.PLACED,
            )
        )

        val dtoOrder = orderMapper.toDto(domainOrder)

        SoftAssertions().apply {
            assertThat(dtoOrder.id).isEqualTo(Option(456))
            assertThat(dtoOrder.orderType).isEqualTo(DtoOrderType.MARKET_ORDER)
            assertThat(dtoOrder.side).isEqualTo(DtoSide.CLIENT)
            assertThat(dtoOrder.buySellType).isEqualTo(DtoBuySellType.BUY)
            assertThat(dtoOrder.buyCurrency).isEqualTo("USD")
            assertThat(dtoOrder.sellCurrency).isEqualTo("UAH")
            assertThat(dtoOrder.volume).isEqualTo(bd("2000"))
            assertThat(dtoOrder.limitPrice).isEqualTo(None)
            assertThat(dtoOrder.stopPrice).isEqualTo(None)
            assertThat(dtoOrder.dailyExecutionType).isEqualTo(None)
            assertThat(dtoOrder.market).isNotNull.isEqualTo(testMarket.symbol.value)
            assertThat(dtoOrder.orderState).isEqualTo(DtoOrderState.PLACED)
        }.assertAll()
    }


    @Test
    def limitOrder_dtoToDomain(): Unit = {
        import com.mvv.bank.orders.rest.entities.CashOrderBuilder.*
        import com.mvv.bank.orders.rest.entities.OrderBuilder.*

        val dtoOrder: DtoOrder = DtoOrder().fillOrder {
            id <= Option(567)
            user <= testUser.value
            orderType <= DtoOrderType.LIMIT_ORDER
            side <= DtoSide.CLIENT
            buySellType <= DtoBuySellType.BUY
            buyCurrency <= "USD"
            sellCurrency <= "UAH"
            volume <= bd("2000")
            limitPrice <= Option(DtoAmount(bd("40.0"), "UAH"))
            dailyExecutionType <= Option(DtoDailyExecutionType.GTC)
            market <= testMarket.symbol.value
            orderState <= DtoOrderState.PLACED
            resultingRate <= Option(DtoFxRate(testMarket.symbol.value,
                testTimestamp, testDate, testTime, "USD", "UAH",
                bid = bd("39.00"), ask = bd("39.50")))
            placedAt <= Option(ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]"))
        }


        val domainOrder = orderMapper.toDomain(dtoOrder)

        SoftAssertions().apply {
            assertThat(domainOrder.id).isEqualTo(Some(567))
            assertThat(domainOrder.user).isEqualTo(testUser)
            assertThat(domainOrder.side).isEqualTo(DomainSide.CLIENT)
            assertThat(domainOrder.orderType).isEqualTo(DomainOrderType.LIMIT_ORDER)
            assertThat(domainOrder.buySellType).isEqualTo(DomainBuySellType.BUY)
            assertThat(domainOrder.buyCurrency).isEqualTo(DomainCurrency.USD)
            assertThat(domainOrder.sellCurrency).isEqualTo(DomainCurrency.UAH)
            assertThat(domainOrder.volume).isEqualTo(bd("2000"))
            assertThat(domainOrder.market).isNotNull.isEqualTo(testMarket)
            assertThat(domainOrder.orderState).isEqualTo(DomainOrderState.PLACED)

            val rate = DomainFxRate(
                testMarket, testTimestamp, DomainCurrencyPair.USD_UAH,
                bid = bd("39.00"), ask = bd("39.50")
            )
            assertThat(domainOrder.resultingRate).isEqualTo(Some(rate))
            assertThat(domainOrder.resultingQuote).isEqualTo(Some(FxRateAsQuote(rate, domainOrder.priceCurrency)))
            assertThat(domainOrder.resultingPrice).isEqualTo(Some(DomainAmount(bd("39.00"), DomainCurrency.UAH)))

            assertThat(domainOrder).isExactlyInstanceOf(classOf[DomainLimitOrder])
            domainOrder match {
                case domainLimitOrder: DomainLimitOrder =>
                    assertThat(domainLimitOrder.limitPrice).isEqualTo(DomainAmount(bd("40.0"), DomainCurrency.UAH))
                    assertThat(domainLimitOrder.dailyExecutionType).isEqualTo(DomainDailyExecutionType.GTC)
                case _ =>
            }

        }.assertAll()
    }


    @Test
    def stopOrder_dtoToDomain(): Unit = {
        import com.mvv.bank.orders.rest.entities.CashOrderBuilder.*
        import com.mvv.bank.orders.rest.entities.OrderBuilder.*

        val dtoOrder = DtoOrder().fillOrder {
            id <= Option(567L)
            user <= testUser.value
            orderType <= DtoOrderType.STOP_ORDER
            side <= DtoSide.CLIENT
            buySellType <= DtoBuySellType.BUY
            buyCurrency <= "USD"
            sellCurrency <= "UAH"
            volume <= bd("2000")
            stopPrice <= Option(DtoAmount(bd("40.0"), "UAH"))
            dailyExecutionType <= Option(DtoDailyExecutionType.DAY_ONLY)
            market <= testMarket.symbol.value
            orderState <= DtoOrderState.PLACED
            resultingRate <= Option(DtoFxRate(testMarket.symbol.value, testTimestamp, testDate, testTime,
                "USD", "UAH", bid = bd("39.00"), ask = bd("39.50")))
            placedAt <= Option(ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]"))
            expiredAt <= Option(ZonedDateTime.parse("2023-01-03T01:06:20+02:00[Europe/Kiev]"))
        }


        val domainOrder = orderMapper.toDomain(dtoOrder)

        SoftAssertions().apply {
            assertThat(domainOrder.id).isEqualTo(Some(567))
            assertThat(domainOrder.user).isEqualTo(testUser)
            assertThat(domainOrder.side).isEqualTo(DomainSide.CLIENT)
            assertThat(domainOrder.orderType).isEqualTo(DomainOrderType.STOP_ORDER)
            assertThat(domainOrder.buySellType).isEqualTo(DomainBuySellType.BUY)
            assertThat(domainOrder.buyCurrency).isEqualTo(DomainCurrency.USD)
            assertThat(domainOrder.sellCurrency).isEqualTo(DomainCurrency.UAH)
            assertThat(domainOrder.volume).isEqualTo(bd("2000"))
            assertThat(domainOrder.market).isNotNull.isEqualTo(testMarket)
            assertThat(domainOrder.orderState).isEqualTo(DomainOrderState.PLACED)

            val rate = DomainFxRate(
                testMarket, testTimestamp, DomainCurrencyPair.USD_UAH,
                bid = bd("39.00"), ask = bd("39.50")
            )
            assertThat(domainOrder.resultingRate).isEqualTo(Some(rate))
            assertThat(domainOrder.resultingQuote).isEqualTo(Some(FxRateAsQuote(rate, domainOrder.priceCurrency)))
            assertThat(domainOrder.resultingPrice).isEqualTo(Some(DomainAmount(bd("39.00"), DomainCurrency.UAH)))

            assertThat(domainOrder.placedAt).isEqualTo(Some(ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]")))
            assertThat(domainOrder.expiredAt).isEqualTo(Some(ZonedDateTime.parse("2023-01-03T01:06:20+02:00[Europe/Kiev]")))

            assertThat(domainOrder).isExactlyInstanceOf(classOf[DomainStopOrder])
            domainOrder match {
              case domainStopOrder: DomainStopOrder =>
                assertThat(domainStopOrder.stopPrice).isEqualTo(DomainAmount(bd("40.0"), DomainCurrency.UAH))
                assertThat(domainStopOrder.dailyExecutionType).isEqualTo(DomainDailyExecutionType.DAY_ONLY)
              case _ =>
            }

        }.assertAll()
    }


    @Test
    def stopOrder_dtoToDomain_failIfLimitStopPriceCurrencyIsIncorrect(): Unit = {
        import com.mvv.bank.orders.rest.entities.CashOrderBuilder.*
        import com.mvv.bank.orders.rest.entities.OrderBuilder.*

        val dtoOrder = DtoOrder().fillOrder {
            id <= Option(567L)
            user <= testUser.value
            orderType <= DtoOrderType.STOP_ORDER
            side <= DtoSide.CLIENT
            buySellType <= DtoBuySellType.BUY
            buyCurrency <= "USD"
            sellCurrency <= "UAH"
            volume <= bd("2000")
            stopPrice <= Option(DtoAmount(bd("40.0"), "USD"))
            dailyExecutionType <= Option(DtoDailyExecutionType.DAY_ONLY)
            market <= testMarket.symbol.value
            orderState <= DtoOrderState.PLACED
            resultingRate <= Option(DtoFxRate(
                testMarket.symbol.value, testTimestamp, testDate, testTime,
                "USD", "UAH", bid = bd("39.00"), ask = bd("39.50")))
            placedAt <= Option(ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]"))
            expiredAt <= Option(ZonedDateTime.parse("2023-01-03T01:06:20+02:00[Europe/Kiev]"))
        }

        SoftAssertions().apply {

            assertThatCode { orderMapper.toDomain(dtoOrder) }
                .hasMessage("Stop price currency (USD) differs from price currency (UAH).")

            dtoOrder.stopPrice = Option(DtoAmount(bd("40.0"), "EUR"))
            assertThatCode { orderMapper.toDomain(dtoOrder) }
                .hasMessage("Stop price currency (EUR) differs from price currency (UAH).")

        }.assertAll()
    }

    @Test
    def marketOrder_dtoToDomain(): Unit = {
        import com.mvv.bank.orders.rest.entities.CashOrderBuilder.*
        import com.mvv.bank.orders.rest.entities.OrderBuilder.*

        val dtoOrder: DtoOrder = DtoOrder().fillOrder {
            id <= Option(567L)
            user <= testUser.value
            orderType <= DtoOrderType.MARKET_ORDER
            side <= DtoSide.CLIENT
            buySellType <= DtoBuySellType.BUY
            buyCurrency <= "USD"
            sellCurrency <= "UAH"
            volume <= bd("2000")
            market <= testMarket.symbol.value
            orderState <= DtoOrderState.PLACED
            resultingRate <= Option(DtoFxRate(testMarket.symbol.value, testTimestamp, testDate, testTime,
                "USD", "UAH", bid = bd("39.00"), ask = bd("39.50")))
            placedAt <= Option(ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]"))
            executedAt <= Option(ZonedDateTime.parse("2023-01-03T01:06:20+02:00[Europe/Kiev]"))
        }


        val domainOrder = orderMapper.toDomain(dtoOrder)

        SoftAssertions().apply {
            assertThat(domainOrder.id).isEqualTo(Some(567))
            assertThat(domainOrder.user).isEqualTo(testUser)
            assertThat(domainOrder.side).isEqualTo(DomainSide.CLIENT)
            assertThat(domainOrder.orderType).isEqualTo(DomainOrderType.MARKET_ORDER)
            assertThat(domainOrder.buySellType).isEqualTo(DomainBuySellType.BUY)
            assertThat(domainOrder.buyCurrency).isEqualTo(DomainCurrency.USD)
            assertThat(domainOrder.sellCurrency).isEqualTo(DomainCurrency.UAH)
            assertThat(domainOrder.volume).isEqualTo(bd("2000"))
            assertThat(domainOrder.market).isNotNull.isEqualTo(testMarket)
            assertThat(domainOrder.orderState).isEqualTo(DomainOrderState.PLACED)

            val rate = DomainFxRate(
                testMarket, testTimestamp, DomainCurrencyPair.USD_UAH,
                bid = bd("39.00"), ask = bd("39.50")
            )
            assertThat(domainOrder.resultingRate).isEqualTo(Some(rate))
            assertThat(domainOrder.resultingQuote).isEqualTo(Some(FxRateAsQuote(rate, domainOrder.priceCurrency)))
            assertThat(domainOrder.resultingPrice).isEqualTo(Some(DomainAmount(bd("39.00"), DomainCurrency.UAH)))

            assertThat(domainOrder.placedAt).isEqualTo(Some(ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]")))
            assertThat(domainOrder.executedAt).isEqualTo(Some(ZonedDateTime.parse("2023-01-03T01:06:20+02:00[Europe/Kiev]")))

            assertThat(domainOrder).isExactlyInstanceOf(classOf[DomainMarketOrder])

        }.assertAll()
    }

    @Test
    def marketOrder_withLimitStopPrice_dtoToDomain(): Unit = {
        import com.mvv.scala.test.assertj.JScalaAssertions.assertThatCode
        import com.mvv.bank.orders.rest.entities.CashOrderBuilder.*
        import com.mvv.bank.orders.rest.entities.OrderBuilder.*


        val dtoOrder = DtoOrder().fillOrder {
            id <= Option(567L)
            user <= TestPredefinedUsers.USER1.value
            orderType <= DtoOrderType.MARKET_ORDER
            side <= DtoSide.CLIENT
            buySellType <= DtoBuySellType.BUY
            buyCurrency <= "USD"
            sellCurrency <= "UAH"
            volume <= bd("2000")
            limitPrice <= Option(DtoAmount(bd("41.0"), "UAH"))
            stopPrice <= Option(DtoAmount(bd("42.0"), "UAH"))
            market <= testMarket.symbol.value
            orderState <= DtoOrderState.PLACED
            resultingRate <= Option(DtoFxRate(testMarket.symbol.value, testTimestamp, testDate, testTime,
                "USD", "UAH", bid = bd("39.00"), ask = bd("39.50")))
            placedAt <= Option(ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]"))
            executedAt <= Option(ZonedDateTime.parse("2023-01-03T01:06:20+02:00[Europe/Kiev]"))
        }

        assertThatCode { orderMapper.toDomain(dtoOrder) }
            .hasMessage("Market price cannot have limit/stop price (41.0 UAH/42.0 UAH).")
    }

    @Test
    def marketOrder_withDailyExecType_dtoToDomain(): Unit = {
        import com.mvv.scala.test.assertj.JScalaAssertions.assertThatCode
        import com.mvv.bank.orders.rest.entities.CashOrderBuilder.*
        import com.mvv.bank.orders.rest.entities.OrderBuilder.*


        val dtoOrder: DtoOrder = DtoOrder().fillOrder {
            id <= Option(567L)
            user <= TestPredefinedUsers.USER1.value
            orderType <= DtoOrderType.MARKET_ORDER
            side <= DtoSide.CLIENT
            buySellType <= DtoBuySellType.BUY
            buyCurrency <= "USD"
            sellCurrency <= "UAH"
            volume <= bd("2000")
            dailyExecutionType <= Option(DtoDailyExecutionType.DAY_ONLY) // Not allowed
            market <= testMarket.symbol.value
            orderState <= DtoOrderState.PLACED
            resultingRate <= Option(DtoFxRate(testMarket.symbol.value, testTimestamp, testDate, testTime,
                "USD", "UAH", bid = bd("39.00"), ask = bd("39.50")))
            placedAt <= Option(ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]"))
            executedAt <= Option(ZonedDateTime.parse("2023-01-03T01:06:20+02:00[Europe/Kiev]"))
        }

        assertThatCode { orderMapper.toDomain(dtoOrder) }
            .hasMessage("Market price cannot have daily execution type (DAY_ONLY).")
    }
}
