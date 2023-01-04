package com.mvv.bank.orders.repository.jpa.conversion

import com.mvv.bank.orders.domain.test.predefined.TestPredefinedCompanies
import com.mvv.bank.orders.domain.test.predefined.TestPredefinedMarkets
import com.mvv.bank.orders.domain.test.predefined.TestPredefinedUsers
import com.mvv.bank.orders.domain.of

import com.mvv.bank.test.reflect.initProperty
import com.mvv.bank.util.newInstance
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime

import com.mvv.bank.orders.domain.Amount as DomainAmount
import com.mvv.bank.orders.domain.Currency as DomainCurrency
import com.mvv.bank.orders.domain.StockQuote as DomainStockQuote

import com.mvv.bank.orders.domain.Side as DomainSide
import com.mvv.bank.orders.domain.OrderType as DomainOrderType
import com.mvv.bank.orders.domain.OrderState as DomainOrderState
import com.mvv.bank.orders.domain.BuySellType as DomainBuySellType
import com.mvv.bank.orders.domain.DailyExecutionType as DomainDailyExecutionType
import com.mvv.bank.orders.domain.StockLimitOrder as DomainLimitOrder
import com.mvv.bank.orders.domain.StockStopOrder as DomainStopOrder
import com.mvv.bank.orders.domain.StockMarketOrder as DomainMarketOrder

import com.mvv.bank.orders.repository.jpa.entities.Side as DtoSide
import com.mvv.bank.orders.repository.jpa.entities.OrderType as DtoOrderType
import com.mvv.bank.orders.repository.jpa.entities.OrderState as DtoOrderState
import com.mvv.bank.orders.repository.jpa.entities.BuySellType as DtoBuySellType
import com.mvv.bank.orders.repository.jpa.entities.DailyExecutionType as DtoDailyExecutionType
import com.mvv.bank.orders.repository.jpa.entities.StockOrder as DtoOrder

import java.math.BigDecimal as bd


class StockOrderMapperTest {
    private val testMarket = TestPredefinedMarkets.KYIV1
    private val testDate = LocalDate.of(2022, java.time.Month.DECEMBER, 23)
    private val testTime = LocalTime.of(13, 5)
    private val testDateTime = LocalDateTime.of(testDate, testTime)
    private val testTimestamp = ZonedDateTime.of(testDateTime, testMarket.zoneId)
    private val testCompany = TestPredefinedCompanies.APPLE
    private val testUser = TestPredefinedUsers.USER1

    private val orderMapper = Mappers.getMapper(StockOrderMapper::class.java).clone()
        .apply { initProperty(this, "marketService", TestPredefinedMarkets) }
        .apply { initProperty(this, "companyService", TestPredefinedCompanies) }
        as StockOrderMapper


    @Test
    fun limitOrder_domainToDto() {

        val domainOrder = DomainLimitOrder.create(
            id = null,
            user = testUser,
            side = DomainSide.CLIENT,
            buySellType = DomainBuySellType.BUY,
            company = testCompany,
            volume = bd("2000"),
            limitPrice = DomainAmount.of("40.0", DomainCurrency.USD),
            dailyExecutionType = DomainDailyExecutionType.GTC,
            market = testMarket,
            orderState = DomainOrderState.TO_BE_PLACED,
        )

        val dtoOrder = orderMapper.toDto(domainOrder)
        checkNotNull(dtoOrder)

        SoftAssertions().apply {
            assertThat(dtoOrder.id).isNull()
            assertThat(dtoOrder.orderType).isEqualTo(DtoOrderType.LIMIT_ORDER)
            assertThat(dtoOrder.side).isEqualTo(DtoSide.CLIENT)
            assertThat(dtoOrder.buySellType).isEqualTo(DtoBuySellType.BUY)
            assertThat(dtoOrder.product).isEqualTo(testCompany.symbol.value)
            assertThat(dtoOrder.volume).isEqualTo(bd("2000"))
            assertThat(dtoOrder.limitStopPrice).isEqualTo(bd("40.0"))
            assertThat(dtoOrder.priceCurrency).isEqualTo("USD")
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
            company = testCompany,
            volume = bd("2000"),
            stopPrice = DomainAmount.of("40.0", DomainCurrency.USD),
            dailyExecutionType = DomainDailyExecutionType.GTC,
            market = testMarket,
            resultingQuote = DomainStockQuote.of(
                testMarket, testCompany, testTimestamp,
                bid = bd("39.00"), ask = bd("39.50"), DomainCurrency.USD,
            ),
            orderState = DomainOrderState.PLACED,
            placedAt = ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]")
        )

        val dtoOrder = orderMapper.toDto(domainOrder)
        checkNotNull(dtoOrder)

        SoftAssertions().apply {
            assertThat(dtoOrder.id).isEqualTo(567)
            assertThat(dtoOrder.orderType).isEqualTo(DtoOrderType.STOP_ORDER)
            assertThat(dtoOrder.side).isEqualTo(DtoSide.CLIENT)
            assertThat(dtoOrder.buySellType).isEqualTo(DtoBuySellType.BUY)
            assertThat(dtoOrder.product).isEqualTo(testCompany.symbol.value)
            assertThat(dtoOrder.volume).isEqualTo(bd("2000"))
            assertThat(dtoOrder.limitStopPrice).isEqualTo(bd("40.0"))
            assertThat(dtoOrder.dailyExecutionType).isEqualTo(DtoDailyExecutionType.GTC)
            assertThat(dtoOrder.market).isNotNull.isEqualTo(testMarket.symbol.value)
            assertThat(dtoOrder.orderState).isEqualTo(DtoOrderState.PLACED)

            assertThat(dtoOrder.priceCurrency).isEqualTo("USD")
            assertThat(dtoOrder.resultingPrice).isEqualTo(bd("39.50"))
            assertThat(dtoOrder.resultingQuoteTimestamp).isEqualTo(testTimestamp)
            assertThat(dtoOrder.resultingQuoteBid).isEqualTo(bd("39.00"))
            assertThat(dtoOrder.resultingQuoteAsk).isEqualTo(bd("39.50"))
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
            company = testCompany,
            volume = bd("2000"),
            market = testMarket,
            orderState = DomainOrderState.PLACED,
        )

        val dtoOrder = orderMapper.toDto(domainOrder)
        checkNotNull(dtoOrder)

        SoftAssertions().apply {
            assertThat(dtoOrder.id).isEqualTo(456)
            assertThat(dtoOrder.orderType).isEqualTo(DtoOrderType.MARKET_ORDER)
            assertThat(dtoOrder.side).isEqualTo(DtoSide.CLIENT)
            assertThat(dtoOrder.buySellType).isEqualTo(DtoBuySellType.BUY)
            assertThat(dtoOrder.product).isEqualTo(testCompany.symbol.value)
            assertThat(dtoOrder.volume).isEqualTo(bd("2000"))
            assertThat(dtoOrder.limitStopPrice).isNull()
            assertThat(dtoOrder.dailyExecutionType).isNull()
            assertThat(dtoOrder.market).isNotNull.isEqualTo(testMarket.symbol.value)
            assertThat(dtoOrder.orderState).isEqualTo(DtoOrderState.PLACED)
        }.assertAll()
    }


    @Test
    fun limitOrder_withoutDailyExecType_domainToDto() {

        val domainOrder = newInstance<DomainLimitOrder>().apply {
            id = null
            user = testUser
            side = DomainSide.CLIENT
            buySellType = DomainBuySellType.BUY
            product = testCompany.symbol
            orderState = DomainOrderState.TO_BE_PLACED
            // market, volume, limitPrice, dailyExecutionType are missed by some strange mistake
        }

        assertThatCode { orderMapper.toDto(domainOrder) }
            .hasMessage("The following properties [company, dailyExecutionType, limitPrice, market, volume] are not initialized.")
    }


    @Test
    fun limitOrder_dtoToDomain() {

        val dtoOrder = DtoOrder().apply {
            id = 567
            user = testUser.value
            orderType = DtoOrderType.LIMIT_ORDER
            side = DtoSide.CLIENT
            buySellType = DtoBuySellType.BUY
            product = testCompany.symbol.value
            volume = bd("2000")
            limitStopPrice = bd("40.0")
            priceCurrency = "USD"
            dailyExecutionType = DtoDailyExecutionType.GTC
            market = testMarket.symbol.value
            orderState = DtoOrderState.PLACED
            resultingPrice = bd("39.50")
            resultingQuoteTimestamp = testTimestamp
            resultingQuoteBid = bd("39.00")
            resultingQuoteAsk = bd("39.50")
            placedAt = ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]")
        }


        val domainOrder  = orderMapper.toDomain(dtoOrder)

        SoftAssertions().apply {
            assertThat(domainOrder.id).isEqualTo(567)
            assertThat(domainOrder.user).isEqualTo(testUser)
            assertThat(domainOrder.side).isEqualTo(DomainSide.CLIENT)
            assertThat(domainOrder.orderType).isEqualTo(DomainOrderType.LIMIT_ORDER)
            assertThat(domainOrder.buySellType).isEqualTo(DomainBuySellType.BUY)
            assertThat(domainOrder.product).isEqualTo(testCompany.symbol)
            assertThat(domainOrder.company).isEqualTo(testCompany)
            assertThat(domainOrder.volume).isEqualTo(bd("2000"))
            assertThat(domainOrder.market).isNotNull.isEqualTo(testMarket)
            assertThat(domainOrder.orderState).isEqualTo(DomainOrderState.PLACED)

            assertThat(domainOrder.resultingPrice).isEqualTo(DomainAmount.of(bd("39.50"), DomainCurrency.USD))
            assertThat(domainOrder.resultingQuote).isEqualTo(DomainStockQuote.of(
                testMarket, testCompany, testTimestamp,
                bid = bd("39.00"), ask = bd("39.50"), DomainCurrency.USD,
            ))

            assertThat(domainOrder).isExactlyInstanceOf(DomainLimitOrder::class.java)
            if (domainOrder is DomainLimitOrder) {
                assertThat(domainOrder.limitPrice).isEqualTo(DomainAmount.of(bd("40.0"), DomainCurrency.USD))
                assertThat(domainOrder.dailyExecutionType).isEqualTo(DomainDailyExecutionType.GTC)
            }

        }.assertAll()
    }


    @Test
    fun stopOrder_dtoToDomain() {

        val dtoOrder: DtoOrder = DtoOrder().apply {
            id = 567
            user = testUser.value
            orderType = DtoOrderType.STOP_ORDER
            side = DtoSide.CLIENT
            buySellType = DtoBuySellType.SELL
            product = testCompany.symbol.value
            volume = bd("2000")
            limitStopPrice = bd("40.0")
            priceCurrency = "USD"
            dailyExecutionType = DtoDailyExecutionType.DAY_ONLY
            market = testMarket.symbol.value
            orderState = DtoOrderState.PLACED
            resultingPrice = bd("39.00")
            resultingQuoteBid = bd("39.00")
            resultingQuoteAsk = bd("39.50")
            resultingQuoteTimestamp = testTimestamp
            placedAt = ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]")
            expiredAt = ZonedDateTime.parse("2023-01-03T01:06:20+02:00[Europe/Kiev]")
        }


        val domainOrder = orderMapper.toDomain(dtoOrder)

        SoftAssertions().apply {
            assertThat(domainOrder.id).isEqualTo(567)
            assertThat(domainOrder.user).isEqualTo(testUser)
            assertThat(domainOrder.side).isEqualTo(DomainSide.CLIENT)
            assertThat(domainOrder.orderType).isEqualTo(DomainOrderType.STOP_ORDER)
            assertThat(domainOrder.buySellType).isEqualTo(DomainBuySellType.SELL)
            assertThat(domainOrder.product).isEqualTo(testCompany.symbol)
            assertThat(domainOrder.company).isEqualTo(testCompany)
            assertThat(domainOrder.volume).isEqualTo(bd("2000"))
            assertThat(domainOrder.market).isNotNull.isEqualTo(testMarket)
            assertThat(domainOrder.orderState).isEqualTo(DomainOrderState.PLACED)

            val quote = DomainStockQuote(
                testMarket.symbol, testCompany.symbol, testTimestamp, testDate, testTime,
                bid = DomainAmount.of(bd("39.00"), DomainCurrency.USD), ask = DomainAmount.of(bd("39.50"), DomainCurrency.USD))
            assertThat(domainOrder.resultingPrice).isEqualTo(DomainAmount.of(bd("39.00"), DomainCurrency.USD))
            assertThat(domainOrder.resultingQuote).isEqualTo(quote)

            assertThat(domainOrder.placedAt).isEqualTo(ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]"))
            assertThat(domainOrder.expiredAt).isEqualTo(ZonedDateTime.parse("2023-01-03T01:06:20+02:00[Europe/Kiev]"))

            assertThat(domainOrder).isExactlyInstanceOf(DomainStopOrder::class.java)
            if (domainOrder is DomainStopOrder) {
                assertThat(domainOrder.stopPrice).isEqualTo(DomainAmount.of("40.0", DomainCurrency.USD))
                assertThat(domainOrder.dailyExecutionType).isEqualTo(DomainDailyExecutionType.DAY_ONLY)
            }

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
            product = testCompany.symbol.value
            volume = bd("2000")
            market = testMarket.symbol.value
            orderState = DtoOrderState.PLACED
            priceCurrency = "USD"
            resultingPrice = bd("39.50")
            resultingQuoteBid = bd("39.00")
            resultingQuoteAsk = bd("39.50")
            resultingQuoteTimestamp = testTimestamp
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
            assertThat(domainOrder.product).isEqualTo(testCompany.symbol)
            assertThat(domainOrder.company).isEqualTo(testCompany)
            assertThat(domainOrder.volume).isEqualTo(bd("2000"))
            assertThat(domainOrder.market).isNotNull.isEqualTo(testMarket)
            assertThat(domainOrder.orderState).isEqualTo(DomainOrderState.PLACED)

            val stockQuote = DomainStockQuote.of(
                testMarket, testCompany, testTimestamp,
                bid = bd("39.00"), ask = bd("39.50"), DomainCurrency.USD)
            assertThat(domainOrder.resultingPrice).isEqualTo(DomainAmount.of(bd("39.50"), DomainCurrency.USD))
            assertThat(domainOrder.resultingQuote).isEqualTo(stockQuote)

            assertThat(domainOrder.placedAt).isEqualTo(ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]"))
            assertThat(domainOrder.executedAt).isEqualTo(ZonedDateTime.parse("2023-01-03T01:06:20+02:00[Europe/Kiev]"))

            assertThat(domainOrder).isExactlyInstanceOf(DomainMarketOrder::class.java)

        }.assertAll()
    }


    @Test
    fun marketOrder_withLimitStopPrice_dtoToDomain() {

        val dtoOrder = DtoOrder().apply {
            id = null
            user = testUser.value
            orderType = DtoOrderType.MARKET_ORDER
            side = DtoSide.CLIENT
            buySellType = DtoBuySellType.BUY
            product = testCompany.symbol.value
            volume = bd("2000")
            limitStopPrice = bd("41.0")
            priceCurrency = "USD"
            market = testMarket.symbol.value
            orderState = DtoOrderState.TO_BE_PLACED
            placedAt = ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]")
            executedAt = ZonedDateTime.parse("2023-01-03T01:06:20+02:00[Europe/Kiev]")
        }

        assertThatCode { orderMapper.toDomain(dtoOrder) }
            .hasMessage("Market price cannot have limit/stop price (41.0).")
    }

    @Test
    fun marketOrder_withDailyExecType_dtoToDomain() {

        val dtoOrder: DtoOrder = DtoOrder().apply {
            id = 567
            user = testUser.value
            orderType = DtoOrderType.MARKET_ORDER
            side = DtoSide.CLIENT
            buySellType = DtoBuySellType.BUY
            product = testCompany.symbol.value
            priceCurrency = "USD"
            volume = bd("2000")
            dailyExecutionType = DtoDailyExecutionType.DAY_ONLY // Not allowed
            market = testMarket.symbol.value
            orderState = DtoOrderState.PLACED
            placedAt = ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]")
            executedAt = ZonedDateTime.parse("2023-01-03T01:06:20+02:00[Europe/Kiev]")
        }

        assertThatCode { orderMapper.toDomain(dtoOrder) }
            .hasMessage("Market price cannot have daily execution type (DAY_ONLY).")
    }

}
