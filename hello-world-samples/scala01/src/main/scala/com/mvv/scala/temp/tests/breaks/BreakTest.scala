package com.mvv.scala.temp.tests.breaks

import util.control.Breaks.*


@main
def test1(): Unit = {
  println("\n=== BREAK EXAMPLE ===")
  breakable {
    for (i <- 1 to 10) {
      println(i)
      if (i == 4) break // break out of the for loop
    }
  }
  println("\n=== CONTINUE EXAMPLE ===")

  val searchMe = "peter piper picked a peck of pickled peppers"
  var numPs = 0
  for (i <- 0 until searchMe.length) {
    breakable {
      if (searchMe.charAt(i) != 'p') {
        break // break out of the 'breakable', continue the outside loop
      } else {
        numPs += 1
      }
    }
  }
  println("Found " + numPs + " p's in the string.")
}
