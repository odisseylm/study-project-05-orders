package com.mvv.scala.temp.tests.traits

// see https://dotty.epfl.ch/docs/reference/other-new-features/transparent-traits.html#

// !!!
// Type inference refers to the automatic detection of the type of an expression in a formal language.
// !!!

trait S :
//transparent trait S :
  def ss = 456
trait Kind
object Var extends Kind, S
object Val extends Kind, S


//noinspection DfaConstantConditions
def test1() = {
  val x = if 34 == 67 then Val else Var

  // !!! 'ss' is not visible if trait S is transparent !!!
  x.ss

  // however these examples are NOT inferred types and transparency of traits does not make any sense
  val y = Var // <<<<<< not inferred type
  y.ss
  val z = Val // <<<<<< not inferred type
  z.ss
}


//noinspection DfaConstantConditions
def test2() = {
  val x = Set(if 34 == 67 then Val else Var)
  val head = x.head // <<<<<< inferred type

  // !!! 'ss' is not visible if trait S is transparent !!!
  head.ss
  // !!! casting fails if trait S is transparent !!!
  val headAsS: S = head

  // however these examples are NOT inferred types and transparency of traits does not make any sense
  val y = Var // <<<<<< not inferred type
  y.ss
  val z = Val // <<<<<< not inferred type
  z.ss
}

class Class33

//noinspection DfaConstantConditions
def test3() = {
  val x = new Class33 with S  // <<<<<< inferred type too

  // !!! casting fails if trait S is transparent !!!
  val xAsS: S = x

  // !!! casting fails if trait S is transparent !!!
  x.ss
}
