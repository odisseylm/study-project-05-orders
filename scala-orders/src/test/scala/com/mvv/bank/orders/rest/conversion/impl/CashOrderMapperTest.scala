package com.mvv.bank.orders.rest.conversion.impl

import com.mvv.bank.orders.domain.AbstractCashOrder.Base
import com.mvv.bank.orders.domain.{CashLimitOrder, User, Amount as DomainAmount, Currency as DomainCurrency, DailyExecutionType as DomainDailyExecutionType}
//import com.mvv.bank.orders.rest.conversion.j.JCashOrderMapper
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers



//noinspection AccessorLikeMethodIsUnit
class CashOrderMapperTest {

    @Test
    def toDto(): Unit = {
        /*
        val domainOrder = CashLimitOrder(
            Base(
                id = None,
                user = User("vovan@g.com"),
            ),
            limitPrice = DomainAmount(BigDecimal("123"), DomainCurrency.USD),
            DomainDailyExecutionType.DAY_ONLY,
        )

        val cashOrderMapper = Mappers.getMapper(classOf[CashOrderMapper])
        val dtoOrder = cashOrderMapper.toDto(domainOrder).nn
        */
    }

    @Test
    def toDomain(): Unit = {
    }
}