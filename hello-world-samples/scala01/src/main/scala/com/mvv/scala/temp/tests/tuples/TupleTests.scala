package com.mvv.scala.temp.tests.tuples


/*
Tupled and Untupled Functions
One scenario you’ll encounter occasionally is when you have data in a tuple, let’s say
an N-element tuple, and you need to call an N-parameter function:
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

  // val mult222 = multTup2.untupled // <=== compilation error
  //
  // There isn’t a corresponding multTup2.untupled available.
  // Also, Function.tupled and Function.untupled only work for arities between two and five

  val d5 = multTup2( (1.0, 2.0) )
  println(d5)

  // multTup2( (1.0, 2.0, 3.0) ) // compilation error (too long tuple)
}
