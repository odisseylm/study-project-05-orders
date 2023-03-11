package com.mvv.bank.orders.rest.conversion.impl

import com.mvv.bank.orders.domain.AbstractCashOrder.Base
import com.mvv.bank.orders.domain.{
    TestPredefinedMarkets,
    CashLimitOrder,
    User,
    Amount as DomainAmount,
    Currency as DomainCurrency,
    DailyExecutionType as DomainDailyExecutionType,
    Side as DomainSide,
    BuySellType as DomainBuySellType,
    OrderState as DomainOrderState,
}
import com.mvv.bank.orders.rest.conversion.j.JCashOrderMapper
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers



//noinspection AccessorLikeMethodIsUnit
class CashOrderMapperTest {

  /*
  @Test
  def toDto(): Unit = {

    val domainOrder = CashLimitOrder(
        Base(
            id = Option(123L),
            user = User("vovan@g.com"),
            side = DomainSide.CLIENT,
            buySellType = DomainBuySellType.BUY,
            buyCurrency = DomainCurrency.EUR,
            sellCurrency = DomainCurrency.USD,
            volume = BigDecimal("123.00"),
            market = TestPredefinedMarkets.KYIV1,
            orderState = DomainOrderState.PLACED,
            placedAt = None, // Option(),
            executedAt = None, // Option(),
            resultingRate = None, // Option(),
            resultingPrice = None, // Option(),
            resultingQuote = None, // Option(),
        ),
        limitPrice = DomainAmount(BigDecimal("123"), DomainCurrency.USD),
        DomainDailyExecutionType.DAY_ONLY,
    )

    //val cashOrderMapper = Mappers.getMapper(classOf[JCashOrderMapper]).nn
    val cashOrderMapper = Mappers.getMapper(classOf[com.mvv.bank.orders.rest.conversion.j.JCashOrderMapper]).nn
    val dtoOrder = cashOrderMapper.toDto(domainOrder).nn
  }
  */

  @Test
  def toDomain(): Unit = {
  }

}
