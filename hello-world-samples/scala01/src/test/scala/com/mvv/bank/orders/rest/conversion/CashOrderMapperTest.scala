package com.mvv.bank.orders.rest.conversion

import org.assertj.core.api.{Assertions, SoftAssertions}
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
import com.mvv.bank.orders.domain.Amount as DomainAmount
import com.mvv.bank.orders.domain.Currency as DomainCurrency
import com.mvv.bank.orders.domain.CashOrder as DomainCashOrder
import com.mvv.bank.orders.rest.entities.Amount as DtoAmount
import com.mvv.bank.orders.rest.entities.CashOrder as DtoCashOrder
import org.assertj.core.api.Assertions.assertThat


//noinspection AccessorLikeMethodIsUnit
class CashOrderMapperTest {

  @Test
  def toDomain(): Unit = {

    val mapper = Mappers.getMapper(classOf[CashOrderMapper])
    assertThat(mapper)
      .describedAs("No mapper for CashOrder222Mapper is found.")
      .isNotNull


    val dtoOrder = new DtoCashOrder(
      id = 123,
      buyCurrency = "EUR", sellCurrency = "USD",
      volume = BigDecimal("1000"),
      limitPrice = DtoAmount.of(BigDecimal("1.15"), "USD"),
      dtoField = "value1",
    )

    val domainOrder = mapper.toDomain(dtoOrder)

    //SoftAssertions().->(
    //  assertThat(domainOrder.id).isEqualTo(123L)
    //)

    assertThat(domainOrder.id).isEqualTo(123L)
    assertThat(domainOrder.buyCurrency).isEqualTo(DomainCurrency.of("EUR"))
    assertThat(domainOrder.sellCurrency).isEqualTo(DomainCurrency.of("USD"))
    assertThat(domainOrder.volume).isEqualTo(BigDecimal("1000"))
    assertThat(domainOrder.limitPrice)
      .isEqualTo(DomainAmount.of(BigDecimal("1.15"), DomainCurrency.of("USD")))
    assertThat(domainOrder.domainField)
      .isEqualTo("value1")
  }

}
