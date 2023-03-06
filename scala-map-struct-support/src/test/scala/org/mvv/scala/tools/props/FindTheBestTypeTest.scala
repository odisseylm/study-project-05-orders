package org.mvv.scala.tools.props

import scala.annotation.targetName
import org.mvv.scala.tools.Logger
import org.mvv.scala.tools.quotes.topClassOrModuleFullName


trait Trait1
trait Trait21 extends Trait1
trait Trait20

trait Trait31 extends Trait1
trait Trait32 extends Trait21

trait Trait41 extends Trait1
trait Trait420 extends Trait20
trait Trait431 extends Trait31



class FindTheBestTypeTest {
  private val log = Logger(topClassOrModuleFullName)

  @targetName("doSmtWithAny")
  def doSmt(v: Any): Unit = log.info("doSmtWithAny")
  @targetName("doSmtWithAnyRef")
  def doSmt(v: AnyRef): Unit = log.info("doSmtWithAnyRef")
  def doSmt(v: Trait21): Unit = log.info("doSmtWithTrait2")

  def doFindTheBest(): Unit = {
    ???
  }

}
