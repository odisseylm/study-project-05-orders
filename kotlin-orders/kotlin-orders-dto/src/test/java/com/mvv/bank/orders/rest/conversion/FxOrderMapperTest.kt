package com.mvv.bank.orders.rest.conversion

import com.mvv.bank.orders.domain.test.predefined.TestPredefinedMarkets
import com.mvv.bank.orders.domain.test.predefined.TestPredefinedUsers
import com.mvv.bank.orders.domain.FxRateAsQuote
import com.mvv.bank.orders.domain.of

import com.mvv.bank.test.reflect.initProperty
import org.assertj.core.api.Assertions.assertThatCode

import com.mvv.bank.orders.domain.Amount as DomainAmount
import com.mvv.bank.orders.domain.FxRate as DomainFxRate
import com.mvv.bank.orders.domain.Currency as DomainCurrency
import com.mvv.bank.orders.domain.CurrencyPair as DomainCurrencyPair

import com.mvv.bank.orders.domain.Side as DomainSide
import com.mvv.bank.orders.rest.entities.Amount as DtoAmount
import com.mvv.bank.orders.domain.OrderType as DomainOrderType
import com.mvv.bank.orders.domain.OrderState as DomainOrderState
import com.mvv.bank.orders.domain.BuySellType as DomainBuySellType
import com.mvv.bank.orders.domain.FxCashStopOrder as DomainStopOrder
import com.mvv.bank.orders.domain.FxCashLimitOrder as DomainLimitOrder
import com.mvv.bank.orders.domain.FxCashMarketOrder as DomainMarketOrder
import com.mvv.bank.orders.domain.DailyExecutionType as DomainDailyExecutionType

import com.mvv.bank.orders.rest.entities.Side as DtoSide
import com.mvv.bank.orders.rest.entities.FxRate as DtoFxRate
import com.mvv.bank.orders.rest.entities.FxOrder as DtoOrder
import com.mvv.bank.orders.rest.entities.OrderType as DtoOrderType
import com.mvv.bank.orders.rest.entities.OrderState as DtoOrderState
import com.mvv.bank.orders.rest.entities.BuySellType as DtoBuySellType
import com.mvv.bank.orders.rest.entities.DailyExecutionType as DtoDailyExecutionType

import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import java.math.BigDecimal as bd
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime


internal class FxOrderMapperTest {
    private val testMarket = TestPredefinedMarkets.KYIV1
    private val testDate = LocalDate.of(2022, java.time.Month.DECEMBER, 23)
    private val testTime = LocalTime.of(13, 5)
    private val testDateTime = LocalDateTime.of(testDate, testTime)
    private val testTimestamp = ZonedDateTime.of(testDateTime, testMarket.zoneId)
    private val testUser = TestPredefinedUsers.USER1

    private val orderMapper = Mappers.getMapper(FxOrderMapper::class.java).clone()
        .also { initProperty(it, "marketService", TestPredefinedMarkets) }
         as FxOrderMapper


    @Test
    fun limitOrder_domainToDto() {

        val domainOrder = DomainLimitOrder.create(
            id = null,
            user = testUser,
            side = DomainSide.CLIENT,
            buySellType = DomainBuySellType.BUY,
            buyCurrency = DomainCurrency.USD,
            sellCurrency = DomainCurrency.UAH,
            volume = bd("2000"),
            limitPrice = DomainAmount.of("40.0", DomainCurrency.UAH),
            dailyExecutionType = DomainDailyExecutionType.GTC,
            market = testMarket,
            orderState = DomainOrderState.TO_BE_PLACED,
        )

        val dtoOrder = orderMapper.toDto(domainOrder)
        //checkNotNull(dtoOrder)

        SoftAssertions().apply {
            assertThat(dtoOrder.id).isNull()
            assertThat(dtoOrder.orderType).isEqualTo(DtoOrderType.LIMIT_ORDER)
            assertThat(dtoOrder.side).isEqualTo(DtoSide.CLIENT)
            assertThat(dtoOrder.buySellType).isEqualTo(DtoBuySellType.BUY)
            assertThat(dtoOrder.buyCurrency).isEqualTo("USD")
            assertThat(dtoOrder.sellCurrency).isEqualTo("UAH")
            assertThat(dtoOrder.volume).isEqualTo(bd("2000"))
            assertThat(dtoOrder.limitPrice).isEqualTo(DtoAmount.of(bd("40.0"), "UAH"))
            assertThat(dtoOrder.dailyExecutionType).isEqualTo(DtoDailyExecutionType.GTC)
            assertThat(dtoOrder.market).isNotNull.isEqualTo(testMarket.symbol.value)
            assertThat(dtoOrder.orderState).isEqualTo(DtoOrderState.TO_BE_PLACED)
        }.assertAll()
    }


    @Test
    fun stopOrder_domainToDto() {

        val domainOrder = DomainStopOrder.create(
            id = 567,
            user = testUser,
            side = DomainSide.CLIENT,
            buySellType = DomainBuySellType.BUY,
            buyCurrency = DomainCurrency.USD,
            sellCurrency = DomainCurrency.UAH,
            volume = bd("2000"),
            stopPrice = DomainAmount.of("40.0", DomainCurrency.UAH),
            dailyExecutionType = DomainDailyExecutionType.GTC,
            market = testMarket,
            resultingRate = DomainFxRate.of(
                testMarket, testTimestamp, DomainCurrencyPair.USD_UAH,
                bid = bd("39.00"), ask = bd("39.50"),
            ),
            orderState = DomainOrderState.PLACED,
            placedAt = ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]")
        )

        val dtoOrder = orderMapper.toDto(domainOrder)
        //checkNotNull(dtoOrder)

        SoftAssertions().apply {
            assertThat(dtoOrder.id).isEqualTo(567)
            assertThat(dtoOrder.orderType).isEqualTo(DtoOrderType.STOP_ORDER)
            assertThat(dtoOrder.side).isEqualTo(DtoSide.CLIENT)
            assertThat(dtoOrder.buySellType).isEqualTo(DtoBuySellType.BUY)
            assertThat(dtoOrder.buyCurrency).isEqualTo("USD")
            assertThat(dtoOrder.sellCurrency).isEqualTo("UAH")
            assertThat(dtoOrder.volume).isEqualTo(bd("2000"))
            assertThat(dtoOrder.stopPrice).isEqualTo(DtoAmount.of(bd("40.0"), "UAH"))
            assertThat(dtoOrder.dailyExecutionType).isEqualTo(DtoDailyExecutionType.GTC)
            assertThat(dtoOrder.market).isNotNull.isEqualTo(testMarket.symbol.value)
            assertThat(dtoOrder.orderState).isEqualTo(DtoOrderState.PLACED)

            assertThat(dtoOrder.resultingRate).isEqualTo(
                DtoFxRate(testMarket.symbol.value, testTimestamp, testDate, testTime,
                    DomainCurrencyPair.USD_UAH, bid = bd("39.00"), ask = bd("39.50")))
            assertThat(dtoOrder.placedAt).isEqualTo(ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]"))
        }.assertAll()
    }


    @Test
    fun marketOrder_domainToDto() {

        val domainOrder = DomainMarketOrder.create(
            id = 456,
            user = testUser,
            side = DomainSide.CLIENT,
            buySellType = DomainBuySellType.BUY,
            buyCurrency = DomainCurrency.USD,
            sellCurrency = DomainCurrency.UAH,
            volume = bd("2000"),
            market = testMarket,
            orderState = DomainOrderState.PLACED,
        )

        val dtoOrder = orderMapper.toDto(domainOrder)
        //checkNotNull(dtoOrder)

        SoftAssertions().apply {
            assertThat(dtoOrder.id).isEqualTo(456)
            assertThat(dtoOrder.orderType).isEqualTo(DtoOrderType.MARKET_ORDER)
            assertThat(dtoOrder.side).isEqualTo(DtoSide.CLIENT)
            assertThat(dtoOrder.buySellType).isEqualTo(DtoBuySellType.BUY)
            assertThat(dtoOrder.buyCurrency).isEqualTo("USD")
            assertThat(dtoOrder.sellCurrency).isEqualTo("UAH")
            assertThat(dtoOrder.volume).isEqualTo(bd("2000"))
            assertThat(dtoOrder.limitPrice).isNull()
            assertThat(dtoOrder.stopPrice).isNull()
            assertThat(dtoOrder.dailyExecutionType).isNull()
            assertThat(dtoOrder.market).isNotNull.isEqualTo(testMarket.symbol.value)
            assertThat(dtoOrder.orderState).isEqualTo(DtoOrderState.PLACED)
        }.assertAll()
    }


    @Test
    fun limitOrder_dtoToDomain() {

        val dtoOrder: DtoOrder = DtoOrder().apply {
            id = 567
            user = testUser.value
            orderType = DtoOrderType.LIMIT_ORDER
            side = DtoSide.CLIENT
            buySellType = DtoBuySellType.BUY
            buyCurrency = "USD"
            sellCurrency = "UAH"
            volume = bd("2000")
            limitPrice = DtoAmount.of(bd("40.0"), "UAH")
            dailyExecutionType = DtoDailyExecutionType.GTC
            market = testMarket.symbol.value
            orderState = DtoOrderState.PLACED
            resultingRate = DtoFxRate(testMarket.symbol.value,
                testTimestamp, testDate, testTime, DomainCurrencyPair.USD_UAH,
                bid = bd("39.00"), ask = bd("39.50"))
            placedAt = ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]")
        }


        val domainOrder = orderMapper.toDomain(dtoOrder)

        SoftAssertions().apply {
            assertThat(domainOrder.id).isEqualTo(567)
            assertThat(domainOrder.user).isEqualTo(testUser)
            assertThat(domainOrder.side).isEqualTo(DomainSide.CLIENT)
            assertThat(domainOrder.orderType).isEqualTo(DomainOrderType.LIMIT_ORDER)
            assertThat(domainOrder.buySellType).isEqualTo(DomainBuySellType.BUY)
            assertThat(domainOrder.buyCurrency).isEqualTo(DomainCurrency.USD)
            assertThat(domainOrder.sellCurrency).isEqualTo(DomainCurrency.UAH)
            assertThat(domainOrder.volume).isEqualTo(bd("2000"))
            assertThat(domainOrder.market).isNotNull.isEqualTo(testMarket)
            assertThat(domainOrder.orderState).isEqualTo(DomainOrderState.PLACED)

            val rate = DomainFxRate.of(
                testMarket, testTimestamp, DomainCurrencyPair.USD_UAH,
                bid = bd("39.00"), ask = bd("39.50")
            )
            assertThat(domainOrder.resultingRate).isEqualTo(rate)
            assertThat(domainOrder.resultingQuote).isEqualTo(FxRateAsQuote(rate, domainOrder.priceCurrency))
            assertThat(domainOrder.resultingPrice).isEqualTo(DomainAmount.of(bd("39.00"), DomainCurrency.UAH))

            assertThat(domainOrder).isExactlyInstanceOf(DomainLimitOrder::class.java)
            if (domainOrder is DomainLimitOrder) {
                assertThat(domainOrder.limitPrice).isEqualTo(DomainAmount.of("40.0", DomainCurrency.UAH))
                assertThat(domainOrder.dailyExecutionType).isEqualTo(DomainDailyExecutionType.GTC)
            }

        }.assertAll()
    }


    @Test
    fun stopOrder_dtoToDomain() {

        val dtoOrder = DtoOrder().apply {
            id = 567
            user = testUser.value
            orderType = DtoOrderType.STOP_ORDER
            side = DtoSide.CLIENT
            buySellType = DtoBuySellType.BUY
            buyCurrency = "USD"
            sellCurrency = "UAH"
            volume = bd("2000")
            stopPrice = DtoAmount.of(bd("40.0"), "UAH")
            dailyExecutionType = DtoDailyExecutionType.DAY_ONLY
            market = testMarket.symbol.value
            orderState = DtoOrderState.PLACED
            resultingRate = DtoFxRate(testMarket.symbol.value, testTimestamp, testDate, testTime,
                DomainCurrencyPair.USD_UAH, bid = bd("39.00"), ask = bd("39.50"))
            placedAt = ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]")
            expiredAt = ZonedDateTime.parse("2023-01-03T01:06:20+02:00[Europe/Kiev]")
        }


        val domainOrder = orderMapper.toDomain(dtoOrder)

        SoftAssertions().apply {
            assertThat(domainOrder.id).isEqualTo(567)
            assertThat(domainOrder.user).isEqualTo(testUser)
            assertThat(domainOrder.side).isEqualTo(DomainSide.CLIENT)
            assertThat(domainOrder.orderType).isEqualTo(DomainOrderType.STOP_ORDER)
            assertThat(domainOrder.buySellType).isEqualTo(DomainBuySellType.BUY)
            assertThat(domainOrder.buyCurrency).isEqualTo(DomainCurrency.USD)
            assertThat(domainOrder.sellCurrency).isEqualTo(DomainCurrency.UAH)
            assertThat(domainOrder.volume).isEqualTo(bd("2000"))
            assertThat(domainOrder.market).isNotNull.isEqualTo(testMarket)
            assertThat(domainOrder.orderState).isEqualTo(DomainOrderState.PLACED)

            val rate = DomainFxRate.of(
                testMarket, testTimestamp, DomainCurrencyPair.USD_UAH,
                bid = bd("39.00"), ask = bd("39.50")
            )
            assertThat(domainOrder.resultingRate).isEqualTo(rate)
            assertThat(domainOrder.resultingQuote).isEqualTo(FxRateAsQuote(rate, domainOrder.priceCurrency))
            assertThat(domainOrder.resultingPrice).isEqualTo(DomainAmount.of(bd("39.00"), DomainCurrency.UAH))

            assertThat(domainOrder.placedAt).isEqualTo(ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]"))
            assertThat(domainOrder.expiredAt).isEqualTo(ZonedDateTime.parse("2023-01-03T01:06:20+02:00[Europe/Kiev]"))

            assertThat(domainOrder).isExactlyInstanceOf(DomainStopOrder::class.java)
            if (domainOrder is DomainStopOrder) {
                assertThat(domainOrder.stopPrice).isEqualTo(DomainAmount.of("40.0", DomainCurrency.UAH))
                assertThat(domainOrder.dailyExecutionType).isEqualTo(DomainDailyExecutionType.DAY_ONLY)
            }

        }.assertAll()
    }


    @Test
    fun stopOrder_dtoToDomain_failIfLimitStopPriceCurrencyIsIncorrect() {

        val dtoOrder = DtoOrder().apply {
            id = 567
            user = testUser.value
            orderType = DtoOrderType.STOP_ORDER
            side = DtoSide.CLIENT
            buySellType = DtoBuySellType.BUY
            buyCurrency = "USD"
            sellCurrency = "UAH"
            volume = bd("2000")
            stopPrice = DtoAmount.of(bd("40.0"), "USD")
            dailyExecutionType = DtoDailyExecutionType.DAY_ONLY
            market = testMarket.symbol.value
            orderState = DtoOrderState.PLACED
            resultingRate = DtoFxRate(
                testMarket.symbol.value, testTimestamp, testDate, testTime,
                DomainCurrencyPair.USD_UAH, bid = bd("39.00"), ask = bd("39.50")
            )
            placedAt = ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]")
            expiredAt = ZonedDateTime.parse("2023-01-03T01:06:20+02:00[Europe/Kiev]")
        }

        SoftAssertions().apply {

            assertThatCode { orderMapper.toDomain(dtoOrder) }
                .hasMessage("Stop price currency (USD) differs from price currency (UAH).")

            dtoOrder.stopPrice = DtoAmount.of(bd("40.0"), "EUR")
            assertThatCode { orderMapper.toDomain(dtoOrder) }
                .hasMessage("Stop price currency (EUR) differs from price currency (UAH).")

        }.assertAll()
    }

    @Test
    fun marketOrder_dtoToDomain() {

        val dtoOrder: DtoOrder = DtoOrder().apply {
            id = 567
            user = testUser.value
            orderType = DtoOrderType.MARKET_ORDER
            side = DtoSide.CLIENT
            buySellType = DtoBuySellType.BUY
            buyCurrency = "USD"
            sellCurrency = "UAH"
            volume = bd("2000")
            market = testMarket.symbol.value
            orderState = DtoOrderState.PLACED
            resultingRate = DtoFxRate(testMarket.symbol.value, testTimestamp, testDate, testTime,
                DomainCurrencyPair.USD_UAH, bid = bd("39.00"), ask = bd("39.50"))
            placedAt = ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]")
            executedAt = ZonedDateTime.parse("2023-01-03T01:06:20+02:00[Europe/Kiev]")
        }


        val domainOrder = orderMapper.toDomain(dtoOrder)

        SoftAssertions().apply {
            assertThat(domainOrder.id).isEqualTo(567)
            assertThat(domainOrder.user).isEqualTo(testUser)
            assertThat(domainOrder.side).isEqualTo(DomainSide.CLIENT)
            assertThat(domainOrder.orderType).isEqualTo(DomainOrderType.MARKET_ORDER)
            assertThat(domainOrder.buySellType).isEqualTo(DomainBuySellType.BUY)
            assertThat(domainOrder.buyCurrency).isEqualTo(DomainCurrency.USD)
            assertThat(domainOrder.sellCurrency).isEqualTo(DomainCurrency.UAH)
            assertThat(domainOrder.volume).isEqualTo(bd("2000"))
            assertThat(domainOrder.market).isNotNull.isEqualTo(testMarket)
            assertThat(domainOrder.orderState).isEqualTo(DomainOrderState.PLACED)

            val rate = DomainFxRate.of(
                testMarket, testTimestamp, DomainCurrencyPair.USD_UAH,
                bid = bd("39.00"), ask = bd("39.50")
            )
            assertThat(domainOrder.resultingRate).isEqualTo(rate)
            assertThat(domainOrder.resultingQuote).isEqualTo(FxRateAsQuote(rate, domainOrder.priceCurrency))
            assertThat(domainOrder.resultingPrice).isEqualTo(DomainAmount.of(bd("39.00"), DomainCurrency.UAH))

            assertThat(domainOrder.placedAt).isEqualTo(ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]"))
            assertThat(domainOrder.executedAt).isEqualTo(ZonedDateTime.parse("2023-01-03T01:06:20+02:00[Europe/Kiev]"))

            assertThat(domainOrder).isExactlyInstanceOf(DomainMarketOrder::class.java)

        }.assertAll()
    }

    @Test
    fun marketOrder_withLimitStopPrice_dtoToDomain() {

        val dtoOrder = DtoOrder().apply {
            id = 567
            user = TestPredefinedUsers.USER1.value
            orderType = DtoOrderType.MARKET_ORDER
            side = DtoSide.CLIENT
            buySellType = DtoBuySellType.BUY
            buyCurrency = "USD"
            sellCurrency = "UAH"
            volume = bd("2000")
            limitPrice = DtoAmount.of(bd("41.0"), "UAH")
            stopPrice = DtoAmount.of(bd("42.0"), "UAH")
            market = testMarket.symbol.value
            orderState = DtoOrderState.PLACED
            resultingRate = DtoFxRate(testMarket.symbol.value, testTimestamp, testDate, testTime,
                DomainCurrencyPair.USD_UAH, bid = bd("39.00"), ask = bd("39.50"))
            placedAt = ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]")
            executedAt = ZonedDateTime.parse("2023-01-03T01:06:20+02:00[Europe/Kiev]")
        }

        assertThatCode { orderMapper.toDomain(dtoOrder) }
            .hasMessage("Market price cannot have limit/stop price (41.0 UAH/42.0 UAH).")
    }

    @Test
    fun marketOrder_withDailyExecType_dtoToDomain() {

        val dtoOrder: DtoOrder = DtoOrder().apply {
            id = 567
            user = TestPredefinedUsers.USER1.value
            orderType = DtoOrderType.MARKET_ORDER
            side = DtoSide.CLIENT
            buySellType = DtoBuySellType.BUY
            buyCurrency = "USD"
            sellCurrency = "UAH"
            volume = bd("2000")
            dailyExecutionType = DtoDailyExecutionType.DAY_ONLY // Not allowed
            market = testMarket.symbol.value
            orderState = DtoOrderState.PLACED
            resultingRate = DtoFxRate(testMarket.symbol.value, testTimestamp, testDate, testTime,
                DomainCurrencyPair.USD_UAH, bid = bd("39.00"), ask = bd("39.50"))
            placedAt = ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]")
            executedAt = ZonedDateTime.parse("2023-01-03T01:06:20+02:00[Europe/Kiev]")
        }

        assertThatCode { orderMapper.toDomain(dtoOrder) }
            .hasMessage("Market price cannot have daily execution type (DAY_ONLY).")
    }

}
