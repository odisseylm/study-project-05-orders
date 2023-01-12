package com.mvv.scala.temp.tests

import scala.compiletime.uninitialized
import scala.util.Random


class ScalaClass (
  val val1: String,
  ) {
  var var1: String = uninitialized
  var var2: String = _
  var var3: String = ""

  lazy val lazyVal: String = { "lazy value" }
}

case class CaseClass1 (prop1: String)

class UsualClass1 (val prop1: String)

object UsualClass1 :
  def apply(prop1: String): UsualClass1 = new UsualClass1(prop1)
  def unapply(obj: UsualClass1): Option[String] = Some(obj.prop1)


def matchTest02(v: Any): Any =
  v match
    case CaseClass1(vv) => vv
    case UsualClass1(vv) => vv

/*
class Person(private var name: String) {
  // this line essentially creates a circular reference
  def name = name
  def name_=(aName: String) { name = aName }
}
*/

//noinspection ScalaWeakerAccess
class Stock (private var _symbol: String) :
  def symbol: String = _symbol
  def symbol_= (symbol: String): Unit = { this.symbol = symbol }

class Stock2 (_symbol: String) :
  def symbol: String = _symbol
  def symbol_= (symbol: String): Unit = { this.symbol = symbol }


//class Stock3 private () :
class Stock3 :
  private var _symbol: String = _
  def symbol: String = this._symbol
  def symbol_= (symbol: String): Unit = { this._symbol = symbol }

object Stock3 :
  def apply(symbol: String): Stock3 = {

    //Stock3("987").applyy( _.symbol = "678" )

    val sss = Stock3("987")
    //sss.applyy(d => d.symbol = "678" )

    //Stock3("987").applyy( _.symbol = "678" )

    //val eee: Stock3 = Stock3("987").applyy( _.symbol = "678" )
    //val s: Stock3 = Stock3("987").applyy( _.symbol = "678" )

    Stock3("Fuck")
  }


implicit class AnyRefExtension[T <: AnyRef](underlying: T) {
  def build(builder: T => T): T = builder(underlying)
  def let(procedure: T => Unit): Unit = procedure(underlying)
  def applyy(procedure: T => Unit): T = { procedure(underlying); underlying }
  //def applyy[T](run: T => T): T = { run(underlying); underlying }
  //def applyy(run: T => T): T = { run(underlying); underlying }
}

implicit class OptionExtensions[T](underlying: Option[T]) {
  def let(procedure: T => Unit): Unit = underlying foreach {
    procedure
  }
}


def aa(): Unit = {
  //val obj = Scot3().applyy( _.symbol = "456" )
  val obj = Stock3("456")

  obj.let( _.symbol = "579" )
  obj.applyy( _.symbol = "579" )

  val obj2 = Stock3("456").applyy( _.symbol = "579" )
  val obj3 = Stock3("456").applyy( v => v.symbol = "579" )
  val obj4 = Stock3("456").applyy { v => v.symbol = "579"; v.symbol = "579"; v.symbol = "579"; }

  println(s"obj: $obj")
}


trait NonTransparentTrait1
trait NonTransparentTrait2
class Class11 extends NonTransparentTrait1 with NonTransparentTrait2

def test345(): Unit = {

  val obj = Class11()
  val trait1: NonTransparentTrait1 = obj
  val trait2: NonTransparentTrait2 = obj

}


//transparent trait TransparentTrait1
transparent trait TransparentTrait2
class Class12 extends NonTransparentTrait1 with TransparentTrait2

def test346(): Unit = {

  val obj = Class12()
  val trait1: NonTransparentTrait1 = obj
  val trait2: TransparentTrait2 = obj

}



object Trading extends App {
  val Sell = "sell"
  val Buy = "buy"

  transparent trait Id :
    def id: Int

  trait Order :
    def quantity: Int

  case class BuyOrder(id: Int, quantity: Int) extends Order with Id

  case class SellOrder(id: Int, quantity: Int) extends Order with Id

  def mkOrder(side: String, qty: Int) =
    if (side.equals(Sell)) SellOrder(mkId, qty) else BuyOrder(mkId, qty)

  def mkId: Int = Random.nextInt()

  println(mkOrder("buy", 100))
}

def test7867868768(): Unit = {
  val trading = Trading
  val asId: Trading.Id = Trading.BuyOrder(345, 789)
  val idFck = asId.id
}

