package com.mvv.bank.orders.repository.jpa.conversion

import com.mvv.bank.orders.domain.*
import com.mvv.bank.orders.domain.Amount
import com.mvv.bank.orders.domain.Currency
import com.mvv.bank.test.reflect.initProperty
import com.mvv.bank.orders.repository.jpa.entities.FxOrder as JpaFxOrder
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime
import com.mvv.bank.orders.domain.Amount as DomainAmount
import com.mvv.bank.orders.domain.BuySellType as DomainBuySellType
import com.mvv.bank.orders.domain.Currency as DomainCurrency
import com.mvv.bank.orders.domain.DailyExecutionType as DomainDailyExecutionType
import com.mvv.bank.orders.domain.OrderState as DomainOrderState
import com.mvv.bank.orders.domain.Side as DomainSide
import com.mvv.bank.orders.repository.jpa.entities.BuySellType as JpaBuySellType
import com.mvv.bank.orders.repository.jpa.entities.DailyExecutionType as JpaDailyExecutionType
import com.mvv.bank.orders.domain.OrderType as DomainOrderType
import com.mvv.bank.orders.repository.jpa.entities.OrderType as JpaOrderType
import com.mvv.bank.orders.repository.jpa.entities.OrderState as JpaOrderState
import com.mvv.bank.orders.repository.jpa.entities.Side as JpaSide
import java.math.BigDecimal as bd


class FxOrderMapperTest {
    private val market = TestPredefinedMarkets.KYIV1
    private val date = LocalDate.of(2022, java.time.Month.DECEMBER, 23)
    private val time = LocalTime.of(13, 5)
    private val dateTime = LocalDateTime.of(date, time)
    private val zonedDateTime = ZonedDateTime.of(dateTime, market.zoneId)


    @Test
    fun fxCashLimitOrder_domainToDto() {

        val fxOrderMapper = Mappers.getMapper(FxOrderMapper::class.java)

        val domainOrder = FxCashLimitOrder.create(
            id = null,
            side = DomainSide.CLIENT,
            buySellType = DomainBuySellType.BUY,
            buyCurrency = DomainCurrency.USD,
            sellCurrency = DomainCurrency.UAH,
            volume = bd("2000"),
            limitPrice = DomainAmount.of("40.0", DomainCurrency.UAH),
            dailyExecutionType = DomainDailyExecutionType.GTC,
            marketSymbol = market.symbol,
            market = market,
            orderState = DomainOrderState.TO_BE_PLACED,
            //placedAt =
        )

        val jpaOrder = fxOrderMapper.toDto(domainOrder) // as AbstractFxCashOrder)

        checkNotNull(jpaOrder)

        SoftAssertions().apply {
            assertThat(jpaOrder.id).isNull()
            assertThat(jpaOrder.orderType).isEqualTo(JpaOrderType.LIMIT_ORDER)
            assertThat(jpaOrder.side).isEqualTo(JpaSide.CLIENT)
            assertThat(jpaOrder.buySellType).isEqualTo(JpaBuySellType.BUY)
            assertThat(jpaOrder.buyCurrency).isEqualTo("USD")
            assertThat(jpaOrder.sellCurrency).isEqualTo("UAH")
            assertThat(jpaOrder.volume).isEqualTo(bd("2000"))
            assertThat(jpaOrder.limitStopPrice).isEqualTo(bd("40.0"))
            assertThat(jpaOrder.dailyExecutionType).isEqualTo(JpaDailyExecutionType.GTC)
            assertThat(jpaOrder.market).isNotNull.isEqualTo(market.symbol)
            assertThat(jpaOrder.orderState).isEqualTo(JpaOrderState.TO_BE_PLACED)
            //assertThat(jpaOrder.).isEqualTo()
        }.assertAll()
    }


    @Test
    fun fxCashStopOrder_domainToDto() {

        val fxOrderMapper = Mappers.getMapper(FxOrderMapper::class.java)

        val domainOrder = FxCashStopOrder.create(
            id = 567,
            side = DomainSide.CLIENT,
            buySellType = DomainBuySellType.BUY,
            buyCurrency = DomainCurrency.USD,
            sellCurrency = DomainCurrency.UAH,
            volume = bd("2000"),
            stopPrice = DomainAmount.of("40.0", DomainCurrency.UAH),
            dailyExecutionType = DomainDailyExecutionType.GTC,
            marketSymbol = market.symbol,
            market = market,
            resultingRate = FxRate(
                market, zonedDateTime, CurrencyPair.USD_UAH,
                bid = bd("39.00"), ask = bd("39.50"),
            ),
            orderState = DomainOrderState.PLACED,
            placedAt = ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]")
        )

        val jpaOrder = fxOrderMapper.toDto(domainOrder) // as AbstractFxCashOrder)

        checkNotNull(jpaOrder)
        SoftAssertions().apply {
            assertThat(jpaOrder.id).isEqualTo(567)
            assertThat(jpaOrder.orderType).isEqualTo(JpaOrderType.STOP_ORDER)
            assertThat(jpaOrder.side).isEqualTo(JpaSide.CLIENT)
            assertThat(jpaOrder.buySellType).isEqualTo(JpaBuySellType.BUY)
            assertThat(jpaOrder.buyCurrency).isEqualTo("USD")
            assertThat(jpaOrder.sellCurrency).isEqualTo("UAH")
            assertThat(jpaOrder.volume).isEqualTo(bd("2000"))
            assertThat(jpaOrder.limitStopPrice).isEqualTo(bd("40.0"))
            assertThat(jpaOrder.dailyExecutionType).isEqualTo(JpaDailyExecutionType.GTC)
            assertThat(jpaOrder.market).isNotNull.isEqualTo(market.symbol)
            assertThat(jpaOrder.orderState).isEqualTo(JpaOrderState.PLACED)

            assertThat(jpaOrder.resultingRateCcy1).isEqualTo("USD")
            assertThat(jpaOrder.resultingRateCcy2).isEqualTo("UAH")
            assertThat(jpaOrder.resultingRateDateTime).isEqualTo(zonedDateTime)
            assertThat(jpaOrder.resultingRateBid).isEqualTo(bd("39.00"))
            assertThat(jpaOrder.resultingRateAsk).isEqualTo(bd("39.50"))
            assertThat(jpaOrder.placedAt).isEqualTo(ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]"))
        }.assertAll()
    }


    @Test
    fun fxCashMarketOrder_domainToDto() {

        val fxOrderMapper = Mappers.getMapper(FxOrderMapper::class.java)

        val domainOrder = FxCashMarketOrder.create(
            id = 456,
            side = DomainSide.CLIENT,
            buySellType = DomainBuySellType.BUY,
            buyCurrency = DomainCurrency.USD,
            sellCurrency = DomainCurrency.UAH,
            volume = bd("2000"),
            marketSymbol = market.symbol,
            market = market,
            orderState = DomainOrderState.PLACED,
            //placedAt =
        )

        val jpaOrder = fxOrderMapper.toDto(domainOrder) // as AbstractFxCashOrder)

        checkNotNull(jpaOrder)

        SoftAssertions().apply {
            assertThat(jpaOrder.id).isEqualTo(456)
            assertThat(jpaOrder.orderType).isEqualTo(JpaOrderType.MARKET_ORDER)
            assertThat(jpaOrder.side).isEqualTo(JpaSide.CLIENT)
            assertThat(jpaOrder.buySellType).isEqualTo(JpaBuySellType.BUY)
            assertThat(jpaOrder.buyCurrency).isEqualTo("USD")
            assertThat(jpaOrder.sellCurrency).isEqualTo("UAH")
            assertThat(jpaOrder.volume).isEqualTo(bd("2000"))
            assertThat(jpaOrder.limitStopPrice).isNull()
            assertThat(jpaOrder.dailyExecutionType).isNull()
            assertThat(jpaOrder.market).isNotNull.isEqualTo(market.symbol)
            assertThat(jpaOrder.orderState).isEqualTo(JpaOrderState.PLACED)
            //assertThat(jpaOrder.).isEqualTo()
        }.assertAll()
    }

    // TODO: add DtoToDomain for Stop
    // TODO: add DtoToDomain for Market

    @Test
    fun fxCashLimitOrder_dtoToDomain() {

        val fxOrderMapper = Mappers.getMapper(FxOrderMapper::class.java)
        // TODO: do not change global mapper, need to clone it?
        initProperty(fxOrderMapper, "marketService", TestPredefinedMarkets.Companion)

        val jpaOrder: JpaFxOrder = JpaFxOrder().apply {
            id = 567
            orderType = JpaOrderType.LIMIT_ORDER
            side = JpaSide.CLIENT
            buySellType = JpaBuySellType.BUY
            buyCurrency = "USD"
            sellCurrency = "UAH"
            volume = bd("2000")
            limitStopPrice = bd("40.0")
            dailyExecutionType = JpaDailyExecutionType.GTC
            market = this@FxOrderMapperTest.market.symbol
            orderState = JpaOrderState.PLACED
            resultingRateCcy1 = "USD"
            resultingRateCcy2 = "UAH"
            resultingRateDateTime = zonedDateTime
            resultingRateBid = bd("39.00")
            resultingRateAsk = bd("39.50")
            placedAt = ZonedDateTime.parse("2023-01-03T01:05:20+02:00[Europe/Kiev]")
        }


        val domainOrder: AbstractFxCashOrder = fxOrderMapper.toDomain(jpaOrder) // as AbstractFxCashOrder)
        //checkNotNull(domainOrder)

        SoftAssertions().apply {
            assertThat(domainOrder.id).isEqualTo(567)
            assertThat(domainOrder.side).isEqualTo(DomainSide.CLIENT)
            assertThat(domainOrder.orderType).isEqualTo(DomainOrderType.LIMIT_ORDER)
            assertThat(domainOrder.buySellType).isEqualTo(DomainBuySellType.BUY)
            assertThat(domainOrder.buyCurrency).isEqualTo(Currency.USD)
            assertThat(domainOrder.sellCurrency).isEqualTo(Currency.UAH)
            assertThat(domainOrder.volume).isEqualTo(bd("2000"))
            assertThat(domainOrder.marketSymbol).isNotNull.isEqualTo(market.symbol)
            assertThat(domainOrder.market).isNotNull.isEqualTo(market)
            assertThat(domainOrder.orderState).isEqualTo(DomainOrderState.PLACED)
            //assertThat(jpaOrder.).isEqualTo()

            val rate = FxRate(
                market, zonedDateTime, CurrencyPair.USD_UAH,
                bid = bd("39.00"), ask = bd("39.50"))
            assertThat(domainOrder.resultingRate).isEqualTo(rate)
            assertThat(domainOrder.resultingQuote).isEqualTo(FxRateAsQuote(rate, domainOrder.priceCurrency))
            assertThat(domainOrder.resultingPrice).isEqualTo(Amount.of(bd("39.00"), Currency.UAH))

            assertThat(domainOrder).isExactlyInstanceOf(FxCashLimitOrder::class.java)
            if (domainOrder is FxCashLimitOrder) {
                assertThat(domainOrder.limitPrice).isEqualTo(Amount.of("40.0", Currency.UAH))
                assertThat(domainOrder.dailyExecutionType).isEqualTo(DomainDailyExecutionType.GTC)
            }

        }.assertAll()
    }
}
