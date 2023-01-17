package com.mvv.scala.temp.tests.reflection


import scala.quoted.* // Import `quotes`, `Quotes`, and `Expr`

def f(x: Expr[Int])(using Quotes): Expr[Int] = {
  import quotes.reflect.* // Import `Tree`, `TypeRepr`, `Symbol`, `Position`, .....
  import quotes.reflect.Symbol
  //val tree: Tree =
  //...
  //...
  ???
}


def ffff(): Unit = {

  //import scala.reflect.runtime.universe.*

}