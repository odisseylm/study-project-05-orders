package com.mvv.scala.temp.tests.enums

import com.mvv.scala.temp.tests.enums.Eastwood.Ugly


enum BuySellType :
  case Buy, Sell

//enum ProductType {
//    Cash }
//  , Stock
//}

//noinspection ScalaUnusedSymbol
enum Direction(val degrees: Int):
  case North extends Direction(0)
  case South extends Direction(180)

//noinspection ScalaUnusedSymbol
enum JDirection (val degrees: Int) extends java.lang.Enum[JDirection]:
  case North extends JDirection(0)
  case South extends JDirection(180)

//noinspection ScalaUnusedSymbol
enum Eastwood[+G, +B]:

  def map[G2](f: G=>G2): Eastwood[G2, B] =
    this match
      case Good(g) => Good(f(g))
      case Bad(b) => Bad(b)
      case Ugly(ex) => Ugly(ex)

  case Good(g: G)
  case Bad(b: B)
  case Ugly(ex: Throwable)


//noinspection ScalaUnusedSymbol
def sss(): Unit = {

  val values = BuySellType.values
  //val values222 = BuySellType.values()
  val value1 = BuySellType.valueOf("BUY")

  //val cc = BuySellType.ordinal


}