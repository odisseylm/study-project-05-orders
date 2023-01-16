package com.mvv.scala.temp.tests

trait Expr

case class Var(name: String) extends Expr :
  println(this)
case class Num(number: Double) extends Expr :
  println(this)
case class UnOp(operator: String, arg: Expr) extends Expr :
  println(this)
case class BinOp(operator: String, left: Expr, right: Expr) extends Expr :
  println(this)


def simplifyTop(expr: Expr): Expr =
  expr match
    case UnOp("-", UnOp("-", e)) => println("match 1"); e
    case BinOp("+", e, Num(0)) => println("match 2"); e
    case BinOp("*", e, Num(1)) => println("match 3"); e
    case _ => println("match default"); expr

//noinspection ScalaUnusedSymbol
val second1: List[Int] => Int = (_: @unchecked) match
    case x :: y :: _ => y

//noinspection ScalaUnnecessaryParentheses, ScalaUnusedSymbol
val second2: (List[Int] => Int) =
  case x :: y :: _ => y
  case _ => -333 // fuck

//noinspection ScalaUnusedSymbol
@unchecked
val second3: PartialFunction[List[Int], Int] =
  case x :: y :: _ => y
  case _ => -333 // fuck

//noinspection ScalaUnusedSymbol
def testMatches(): Unit = {
  val l = List(5, 6, 7)
  val v1 = second1(l)
  val v2 = second2(l)
  val v3 = second3(l)

  println(s"v1: $v1, v2: $v2, v3: $v3")
}
