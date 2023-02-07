package com.mvv.bank.orders.rest.conversion.impl.temp

import scala.language.unsafeNulls
//
import org.mapstruct.factory.Mappers
//
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat


class JavaOrder1MapperTest {

  @Test
  def aaa(): Unit = {
    val mapper = Mappers.getMapper(classOf[JavaOrder1Mapper])

    val domainOrder = DomainOrder1()
    domainOrder.cur1 = "USD"
    domainOrder.cur1 = "EUR"
    domainOrder.amount = BigDecimal("234")
    domainOrder.buySellType = DomainBuySellType1.Sell

    val restOrder = mapper.toRestOrder1(domainOrder)

    assertThat(restOrder).isNotNull
    assertThat(restOrder.cur1).isEqualTo("USD")
    assertThat(restOrder.cur2).isEqualTo("EUR")
    assertThat(restOrder.amount).isEqualTo(BigDecimal("234"))
    assertThat(restOrder.buySellType).isEqualTo(RestBuySellType1.Sell)
  }

}