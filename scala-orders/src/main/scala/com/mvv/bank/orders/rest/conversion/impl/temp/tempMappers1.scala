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
  import org.mvv.scala.tools.beans.toBeanProperties
  import org.mvv.scala.tools.inspection.{ _Class, InspectMode }
  import org.mvv.scala.tools.inspection.tasty.{ TastyScalaBeansInspector, _ClassEx}

  def i = TastyScalaBeansInspector()
  val cls: _ClassEx = i.inspectClass(classOf[DomainOrder1])
  val bp = cls.toBeanProperties(InspectMode.AllSources)
  println(bp)
}

@Mapper
trait Order1Mapper :
  def toRestOrder1(restOrder: DomainOrder1): RestOrder1
  def toDomainOrder1(domainOrder: RestOrder1): DomainOrder1
