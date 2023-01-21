package com.mvv.bank.orders.domain

import scala.language.strictEquality
//

import com.mvv.nullables.CharSequenceCanEqualGivens.given
//import com.mvv.nullables.AllCanEqualGivens.given

//noinspection DfaConstantConditions
class EqualityTest {


  def equalityOfCurrency(): Unit = {

    val t1: Currency = Currency("USD")
    val t2: Currency|Null = Currency("USD")

    if (null == null) { }
    if (t1 == null) { }
    if (t2 == null) { }
    if (null == t1) { }
    if (null == t2) { }

    if (t1 == t1) { }
    if (t1 == t2) { }
    if (t2 == t1) { }
    if (t2 == t2) { }
  }


  def equalityOfOrderType(): Unit = {

    val t1 = OrderType.STOP_ORDER
    val t2: OrderType|Null = OrderType.MARKET_ORDER

    if (null == null) { }
    if (t1 == null) { }
    if (t2 == null) { }
    if (null == t1) { }
    if (null == t2) { }

    if (t1 == t1) { }
    if (t1 == t2) { }
    if (t2 == t1) { }
    if (t2 == t2) { }

    // different enums
    if (OrderType.MARKET_ORDER == OrderType.STOP_ORDER) { }
  }


  def equalityOfCharSequence(): Unit = {

    val t1: CharSequence = "USD"
    val t2: CharSequence | Null = "USD"

    if (null == null) {}
    if (t1 == null) {}
    if (t2 == null) {}
    if (null == t1) {}
    if (null == t2) {}

    if (t1 == t1) {}
    if (t1 == t2) {}
    if (t2 == t1) {}
    if (t2 == t2) {}
  }

}