package com.mvv.bank.orders.rest.conversion

import scala.language.unsafeNulls
//
import scala.math.BigDecimal as bd
//
import org.assertj.core.api.{Assertions, SoftAssertions}
import org.junit.jupiter.api.Test
import org.mapstruct.factory.Mappers
//
import com.mvv.bank.orders.domain.Amount as DomainAmount
import com.mvv.bank.orders.domain.Currency as DomainCurrency
import com.mvv.bank.orders.domain.CashOrder as DomainCashOrder
import com.mvv.bank.orders.rest.entities.Amount as DtoAmount
import com.mvv.bank.orders.rest.entities.CashOrder as DtoCashOrder
import org.assertj.core.api.SoftAssertions


//noinspection AccessorLikeMethodIsUnit
class CashOrderMapperTest {

  @Test
  def toDomain(): Unit = {

    val a = SoftAssertions()

    val mapper = Mappers.getMapper(classOf[CashOrderMapper]).nn
    a.assertThat(mapper)
      .describedAs("No mapper for CashOrder222Mapper is found.")
      .isNotNull

    val dtoOrder = new DtoCashOrder(
      id = 123,
      nullableLongProp = 124,
      optionLongProp1 = 125L,
      buyCurrency = "EUR", sellCurrency = "USD",
      volume = bd("1000"),
      limitPrice = DtoAmount.of(bd("1.15"), "USD"),
      dtoField = "value1",
    )

    val domainOrder = mapper.toDomain(dtoOrder)

    a.assertThat(domainOrder.id).isEqualTo(123L)
    a.assertThat(domainOrder.nullableLongProp).isEqualTo(124L)
    a.assertThat(domainOrder.optionLongProp1).isEqualTo(Some(125L))
    a.assertThat(domainOrder.buyCurrency).isEqualTo(DomainCurrency.of("EUR"))
    a.assertThat(domainOrder.sellCurrency).isEqualTo(DomainCurrency.of("USD"))
    a.assertThat(domainOrder.volume).isEqualTo(bd("1000"))
    a.assertThat(domainOrder.limitPrice)
      .isEqualTo(DomainAmount.of(bd("1.15"), DomainCurrency.of("USD")))
    a.assertThat(domainOrder.domainField).isEqualTo("value1")

    a.assertAll()
  }

  @Test
  def fromDomain(): Unit = {

    val a = SoftAssertions()

    val mapper: CashOrderMapper = Mappers.getMapper(classOf[CashOrderMapper]).nn
    a.assertThat(mapper)
      .describedAs("No mapper for CashOrder222Mapper is found.")
      .isNotNull

    val domainOrder = new DomainCashOrder()
    domainOrder.id = 123
    domainOrder.nullableLongProp = 124
    domainOrder.optionLongProp1 = Option(125L)
    domainOrder.buyCurrency = DomainCurrency.of("EUR")
    domainOrder.sellCurrency = DomainCurrency.of("USD")
    domainOrder.volume = bd("1000")
    domainOrder.limitPrice = DomainAmount.of(bd("1.15"), DomainCurrency("USD"))
    domainOrder.domainField = "value1"

    val dtoOrder = mapper.toDto(domainOrder)

    a.assertThat(dtoOrder.id).isEqualTo(123L)
    a.assertThat(dtoOrder.nullableLongProp).isEqualTo(124L)
    a.assertThat(dtoOrder.optionLongProp1).isEqualTo(125L)
    a.assertThat(dtoOrder.buyCurrency).isEqualTo("EUR")
    a.assertThat(dtoOrder.sellCurrency).isEqualTo("USD")
    a.assertThat(dtoOrder.volume).isEqualTo(bd("1000"))
    a.assertThat(dtoOrder.limitPrice).isEqualTo(DtoAmount.of(bd("1.15"), "USD"))
    a.assertThat(dtoOrder.dtoField).isEqualTo("value1")

    a.assertAll()
  }

}
