package com.mvv.bank.orders.domain

import scala.language.unsafeNulls
//
import scala.math.BigDecimal as bd
//
import java.time.ZonedDateTime
//
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
//
import com.mvv.bank.orders.domain.test.predefined.{ TestPredefinedCompanies, TestPredefinedMarkets, TestDateTimeService }
import com.mvv.test.SoftAssertions.runTests


class TradesTest {
    private val now = ZonedDateTime.now()

    @Test
    def createFxCashTrade():Unit = {
        val trade = FxCashTrade(
            id = 123L,
            market = TestPredefinedMarkets.KYIV1.symbol,
            buySellType = BuySellType.BUY,
            buyCurrency = Currency.EUR,
            sellCurrency = Currency.UAH,
            volume = bd("1000"),
            price = Amount.of(bd("36.7"), Currency.UAH),
            tradedAt = now,
        )

        SoftAssertions().runTests { a =>

            a.assertThat(trade.id).isEqualTo(123L)
            a.assertThat(trade.market).isNotNull.isEqualTo(TestPredefinedMarkets.KYIV1.symbol)
            a.assertThat(trade.buySellType).isEqualTo(BuySellType.BUY)
            a.assertThat(trade.buyCurrency).isEqualTo(Currency.EUR)
            a.assertThat(trade.sellCurrency).isEqualTo(Currency.UAH)

            a.assertThat(trade.asInstanceOf[Trade[Currency]].product).isEqualTo(Currency.EUR)
            a.assertThat(trade.priceCurrency).isEqualTo(Currency.UAH)

            a.assertThat(trade.volume).isEqualTo(bd(1000))
            a.assertThat(trade.price).isEqualTo(Amount.valueOf("36.7 UAH"))
            a.assertThat(trade.tradedAt).isEqualTo(now)

        }.assertAll()
    }

    @Test
    def createStockTrade(): Unit = {
        val trade = StockTrade(
            id = 124L,
            market = TestPredefinedMarkets.NASDAQ.symbol,
            company = TestPredefinedCompanies.APPLE.symbol,
            buySellType = BuySellType.BUY,
            volume = bd("1000"),
            price = Amount.of(bd("95.55"), Currency.USD),
            tradedAt = now,
        )

        SoftAssertions().runTests { a =>

            a.assertThat(trade.id).isEqualTo(124L)
            a.assertThat(trade.market).isNotNull.isEqualTo(TestPredefinedMarkets.NASDAQ.symbol)
            a.assertThat(trade.buySellType).isEqualTo(BuySellType.BUY)
            a.assertThat(trade.product).isEqualTo(TestPredefinedCompanies.APPLE.symbol)
            a.assertThat(trade.company).isEqualTo(TestPredefinedCompanies.APPLE.symbol)
            a.assertThat(trade.volume).isEqualTo(bd(1000))
            a.assertThat(trade.price).isEqualTo(Amount.valueOf("95.55 USD"))
            a.assertThat(trade.tradedAt).isEqualTo(now)

        }.assertAll()
    }

    @Test
    def testTestDateTimeService(): Unit = {
        val dateTimeService = TestDateTimeService(nowSupplier = { now.toInstant })
        assertThat(dateTimeService.now()).isEqualTo(now.toInstant)
    }
}
