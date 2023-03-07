package com.mvv.bank.orders.rest.conversion.impl.temp

import org.assertj.core.api.SoftAssertions

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
    domainOrder.cur2 = "EUR"
    domainOrder.amount = BigDecimal("234")
    domainOrder.buySellType = DomainBuySellType1.Sell

    val restOrder = mapper.toRestOrder1(domainOrder)

    val a = SoftAssertions()

    a.assertThat(restOrder).isNotNull
    a.assertThat(restOrder.cur1).isEqualTo("USD")
    a.assertThat(restOrder.cur2).isEqualTo("EUR")
    a.assertThat(restOrder.amount).isEqualTo(BigDecimal("234"))
    a.assertThat(restOrder.buySellType).isEqualTo(RestBuySellType1.Sell)

    a.assertAll()
  }

}