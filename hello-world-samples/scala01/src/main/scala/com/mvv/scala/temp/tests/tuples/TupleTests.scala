package com.mvv.scala.temp.tests.tuples


// TODO: play with tuples

def someFunctionToShutUpCompiler(): Unit = { }

/*

Tupled and Untupled Functions
One scenario you’ll encounter occasionally is when you have data in a tuple, let’s say
an N-element tuple, and you need to call an N-parameter function:
// src/script/scala/progscala3/fp/basics/Tupling.scala

scala> def mult(d1: Double, d2: Double) = d1 * d2
scala> val d23 = (2.2, 3.3)
Tupled and Untupled Functions | 197
| val d = mult(d23._1, d23._2)
val d23: (Double, Double) = (2.2,3.3)
val d: Double = 7.26

It’s tedious extracting the tuple elements like this.
Because of the literal syntax for tuples, like (2.2, 3.3), there seems to be a natural
symmetry between tuples and function parameter lists. We would love to have a new
version of mult that takes the tuple itself as a single parameter. Fortunately, the
scala.Function object provides tupled and untupled methods for us. There is also a
tupled method available for methods like mult:

scala> val multTup1 = Function.tupled(mult) // Scala 2: Function.tuples(mult _)
| val multTup2 = mult.tupled // Scala 2:(mult _).tupled
val multTup1: ((Double, Double)) => Double = scala.Function...
val multTup2: ((Double, Double)) => Double = scala.Function2...
scala> val d2 = multTup1(d23)
| val d3 = multTup2(d23)
val d2: Double = 7.26
val d3: Double = 7.26

The comments show that Scala 2 required the _ when using the two tupled methods.
There is a Function.untupled:

scala> val mult2 = Function.untupled(multTup2) // Go back...
| val d4 = mult2(d23._1, d23._2)
val mult2: (Double, Double) => Double = scala.Function$$$Lambda$...
val d4: Double = 7.26

However, there isn’t a corresponding multTup2.untupled available.
Also, Function.tupled and Function.untupled only work for arities between two and five,
inclusive, an arbitrary limitation. Above arity five, you can call myfunc.tupled up to arity 22.

*/



@main
def test456(): Unit = {
  def mult(d1: Double, d2: Double) = d1 * d2

  val d23: (Double, Double) = (2.2, 3.3)
  val d = mult(d23._1, d23._2)  // <=== access by pseudo-names _1, _2, _3, _N
  println(d)

  val multTup1 = Function.tupled(mult)
  val multTup2 = mult.tupled  // <=== recommended way; creating 'tuple' func using method Function2.tupled

  val d2 = multTup1(d23)
  println(d2)

  val d3 = multTup2(d23)
  println(d3)

  val mult2 = Function.untupled(multTup2)
  val d4 = mult2(d23._1, d23._2)
  println(d4)

  val d5 = multTup2( (1.0, 2.0) )
  println(d5)

  // multTup2( (1.0, 2.0, 3.0) ) // compilation error (too long tuple)
}
