package com.mvv.bank.orders.repository.jpa.conversion

import com.mvv.bank.orders.domain.test.predefined.TestPredefinedMarkets

import com.mvv.bank.orders.conversion.CurrencyMapper
import com.mvv.bank.orders.domain.Currency
import com.mvv.bank.orders.domain.CurrencyPair
import com.mvv.bank.orders.domain.currencyPair
import com.mvv.bank.orders.domain.invoke
import com.mvv.bank.orders.repository.jpa.entities.FxRate as JpaFxRate
import com.mvv.bank.orders.domain.FxRate as DomainFxRate
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import java.math.BigDecimal as bd
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime


internal class CurrencyMapperTest {

    @Test
    fun currencyMapping() {
        val currencyMapper: CurrencyMapper = Mappers.getMapper(CurrencyMapper::class.java)

        SoftAssertions().apply {

            assertThat(currencyMapper).isNotNull

            assertThat(currencyMapper.toDto(null)).isNull()

            assertThat(currencyMapper.toDomain("USD")).isEqualTo(Currency("USD"))
            assertThat(currencyMapper.toDomain("USD")).isEqualTo(Currency.USD)

            assertThat(currencyMapper.toDto(Currency("USD"))).isEqualTo("USD")
            assertThat(currencyMapper.toDto(Currency.USD)).isEqualTo("USD")

            assertThatCode { currencyMapper.toDomain("USD ") }
                .hasMessage("Invalid currency [USD ].")
                .isExactlyInstanceOf(IllegalArgumentException::class.java)

        }.assertAll()
    }

}



internal class FxRateMapperTest {
    private val testMarket = TestPredefinedMarkets.KYIV1
    private val testDate = LocalDate.now()
    private val testTime = LocalTime.now()
    private val testTimestamp = ZonedDateTime.of(testDate, testTime, testMarket.zoneId)

    @Test
    fun toDto() {
        val mapper = Mappers.getMapper(FxRateMapper::class.java)

        SoftAssertions().apply {

            assertThat(mapper).isNotNull

            val dto = mapper.toDto(
                DomainFxRate(testMarket, testTimestamp, CurrencyPair.EUR_USD, bid = bd("1.1"), ask = bd("1.2")))

            assertThat(dto).isNotNull

            checkNotNull(dto)
            assertThat(dto.cur1).isEqualTo("EUR")
            assertThat(dto.cur2).isEqualTo("USD")
            assertThat(dto.market).isNotNull.isEqualTo(testMarket.symbol.value)
            assertThat(dto.marketDate).isNotNull.isEqualTo(testDate)
            assertThat(dto.marketTime).isNotNull.isEqualTo(testTime)
            assertThat(dto.timestamp).isNotNull.isEqualTo(testTimestamp)
            assertThat(dto.bid).isEqualTo(bd("1.1"))
            assertThat(dto.ask).isEqualTo(bd("1.2"))

        }.assertAll()
    }

    @Test
    fun fromDto() {
        val mapper = Mappers.getMapper(FxRateMapper::class.java)

        SoftAssertions().apply {

            assertThat(mapper).isNotNull

            val domainObj = mapper.fromDto(
                JpaFxRate().apply {
                    market = testMarket.symbol.value
                    timestamp  = testTimestamp
                    marketDate = testDate
                    marketTime = testTime
                    cur1 = "EUR"
                    cur2 = "USD"
                    bid = bd("1.1")
                    ask = bd("1.2")
                }
            )

            assertThat(domainObj).isNotNull

            checkNotNull(domainObj) // for kotlin only
            assertThat(domainObj.currencyPair).isEqualTo(currencyPair("EUR_USD"))
            assertThat(domainObj.market).isNotNull.isEqualTo(testMarket.symbol)
            assertThat(domainObj.marketDate).isNotNull.isEqualTo(testDate)
            assertThat(domainObj.marketTime).isNotNull.isEqualTo(testTime)
            assertThat(domainObj.timestamp).isNotNull.isEqualTo(testTimestamp)
            assertThat(domainObj.bid).isEqualTo(bd("1.1"))
            assertThat(domainObj.ask).isEqualTo(bd("1.2"))

        }.assertAll()
    }
}
