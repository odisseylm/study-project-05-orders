package org.mvv.scala.tools.props

import scala.annotation.targetName


trait Trait1
trait Trait21 extends Trait1
trait Trait20

trait Trait31 extends Trait1
trait Trait32 extends Trait21

trait Trait41 extends Trait1
trait Trait420 extends Trait20
trait Trait431 extends Trait31



class FindTheBestTypeTest {

  @targetName("doSmtWithAny")
  def doSmt(v: Any): Unit = println("doSmtWithAny")
  @targetName("doSmtWithAnyRef")
  def doSmt(v: AnyRef): Unit = println("doSmtWithAnyRef")
  def doSmt(v: Trait21): Unit = println("doSmtWithTrait2")



  def doFindTheBest(): Unit = {

  }

}
