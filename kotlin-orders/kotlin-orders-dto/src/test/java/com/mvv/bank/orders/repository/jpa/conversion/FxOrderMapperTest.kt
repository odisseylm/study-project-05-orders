package com.mvv.bank.orders.repository.jpa.conversion

import com.mvv.bank.orders.domain.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import com.mvv.bank.orders.domain.Amount as DomainAmount
import com.mvv.bank.orders.domain.BuySellType as DomainBuySellType
import com.mvv.bank.orders.domain.Currency as DomainCurrency
import com.mvv.bank.orders.domain.DailyExecutionType as DomainDailyExecutionType
import com.mvv.bank.orders.domain.OrderState as DomainOrderState
import com.mvv.bank.orders.domain.Side as DomainSide
import com.mvv.bank.orders.repository.jpa.entities.BuySellType as JpaBuySellType
import com.mvv.bank.orders.repository.jpa.entities.DailyExecutionType as JpaDailyExecutionType
import com.mvv.bank.orders.repository.jpa.entities.OrderState as JpaOrderState
import com.mvv.bank.orders.repository.jpa.entities.Side as JpaSide
import java.math.BigDecimal as bd

class FxOrderMapperTest {
    private val market = TestPredefinedMarkets.KYIV1
    // TODO: use these fields
    //private val date = LocalDate.of(2022, java.time.Month.DECEMBER, 23)
    //private val time = LocalTime.of(13, 5)
    //private val dateTime = LocalDateTime.of(date, time)


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
        assertThat(jpaOrder.id).isNull()
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
    }


    @Test
    fun fxCashStopOrder_domainToDto() {

        val fxOrderMapper = Mappers.getMapper(FxOrderMapper::class.java)

        val domainOrder = FxCashStopOrder.create(
            id = null,
            side = DomainSide.CLIENT,
            buySellType = DomainBuySellType.BUY,
            buyCurrency = DomainCurrency.USD,
            sellCurrency = DomainCurrency.UAH,
            volume = bd("2000"),
            stopPrice = DomainAmount.of("40.0", DomainCurrency.UAH),
            dailyExecutionType = DomainDailyExecutionType.GTC,
            marketSymbol = market.symbol,
            market = market,
            orderState = DomainOrderState.TO_BE_PLACED,
            //placedAt =
        )

        val jpaOrder = fxOrderMapper.toDto(domainOrder) // as AbstractFxCashOrder)

        checkNotNull(jpaOrder)
        // TODO: use Lazy assertions
        assertThat(jpaOrder.id).isNull()
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
    }


    @Test
    fun fxCashMarketOrder_domainToDto() {

        val fxOrderMapper = Mappers.getMapper(FxOrderMapper::class.java)

        val domainOrder = FxCashMarketOrder.create(
            id = null,
            side = DomainSide.CLIENT,
            buySellType = DomainBuySellType.BUY,
            buyCurrency = DomainCurrency.USD,
            sellCurrency = DomainCurrency.UAH,
            volume = bd("2000"),
            marketSymbol = market.symbol,
            market = market,
            orderState = DomainOrderState.TO_BE_PLACED,
            //placedAt =
        )

        val jpaOrder = fxOrderMapper.toDto(domainOrder) // as AbstractFxCashOrder)

        checkNotNull(jpaOrder)
        // TODO: use Lazy assertions
        assertThat(jpaOrder.id).isNull()
        assertThat(jpaOrder.side).isEqualTo(JpaSide.CLIENT)
        assertThat(jpaOrder.buySellType).isEqualTo(JpaBuySellType.BUY)
        assertThat(jpaOrder.buyCurrency).isEqualTo("USD")
        assertThat(jpaOrder.sellCurrency).isEqualTo("UAH")
        assertThat(jpaOrder.volume).isEqualTo(bd("2000"))
        assertThat(jpaOrder.limitStopPrice).isNull()
        assertThat(jpaOrder.dailyExecutionType).isNull()
        assertThat(jpaOrder.market).isNotNull.isEqualTo(market.symbol)
        assertThat(jpaOrder.orderState).isEqualTo(JpaOrderState.TO_BE_PLACED)
        //assertThat(jpaOrder.).isEqualTo()
    }

    // TODO: add DtoToDomain for Limit
    // TODO: add DtoToDomain for Stop
    // TODO: add DtoToDomain for Market
}
