package com.mvv.bank.orders.rest.conversion.impl.temp

import scala.compiletime.uninitialized
//
import org.mapstruct.Mapper


enum RestBuySellType1 :
  case Buy, Sell
class RestOrder1 :
  var amount: BigDecimal = uninitialized
  var cur1: String = uninitialized
  var cur2: String = uninitialized
  var buySellType: RestBuySellType1 = uninitialized


enum DomainBuySellType1 :
  case Buy, Sell
class DomainOrder1 :
  var amount: BigDecimal = uninitialized
  var cur1: String = uninitialized
  var cur2: String = uninitialized
  var buySellType: DomainBuySellType1 = uninitialized


@main
def aaa(): Unit = {
  import org.mvv.scala.mapstruct.beanProperties
  def i = org.mvv.scala.mapstruct.ScalaBeansInspector()
  val cls: org.mvv.scala.mapstruct._Class = i.inspectClass(classOf[DomainOrder1])
  val bp = cls.beanProperties
  println(bp)
}

@Mapper
trait Order1Mapper :
  def toRestOrder1(restOrder: DomainOrder1): RestOrder1
  def toDomainOrder1(domainOrder: RestOrder1): DomainOrder1
