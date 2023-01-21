package com.mvv.bank.orders.domain

import com.mvv.scala.props.collectRequiredProperties
import org.junit.jupiter.api.DisplayName

import scala.language.unsafeNulls
//
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
//
import scala.jdk.CollectionConverters.*


class OrderTest {

  @Test
  @DisplayName("collectRequiredProperties")
  def testCollectRequiredPropertiesFromTrait(): Unit = {
    //collectRequiredProperties(classOf[AbstractOrder[?,?]])
    val propNames = collectRequiredProperties(classOf[Order[?,?]])
    assertThat(propNames.asJava).containsExactlyInAnyOrder(
      "id",
      "product",
      "user",
      "volume",
      "placedAt",
      "executedAt",
      "canceledAt",
      "expiredAt",
      "orderState",
      "resultingPrice",
      "resultingQuote",
      "market",
      "side",
      "orderType",
      "buySellType",
    )
  }


  @Test
  @DisplayName("collectRequiredProperties")
  def testCollectRequiredPropertiesOfClass(): Unit = {
    val propNames = collectRequiredProperties(classOf[AbstractOrder[?,?]])
    assertThat(propNames.asJava).containsExactlyInAnyOrder(
      "id",
      "product",
      "user",
      "volume",
      "placedAt",
      "executedAt",
      "canceledAt",
      "expiredAt",
      "orderState",
      "resultingPrice",
      "resultingQuote",
      "market",
      "side",
      "orderType",
      "buySellType",
    )
  }

}
