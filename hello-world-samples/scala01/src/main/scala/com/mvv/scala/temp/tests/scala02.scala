package com.mvv1:
  class AA


// hm... Absolutely different AA class (in root package _root_ :-( )
class AA(var prop1: String)


package com.mvv2 {

class AA (var prop1: String) :
  def vv: Int = 567

class BB(var prop1: String)


case class PosInt(value: Int) :
  require(value > 0)
  export value.*
  override def toString: String = s"$value"


def ff (
       arg0: Int,
       arg1: Int,
       arg2: Int,
       ) = "qwerty"


  case class BaseCaseClass(value1: Int) {}

  //case class ChildCaseClass1(value2: String) extends BaseCaseClass {}
  class ChildCaseClass2(value1: Int, val value2: String) extends BaseCaseClass(value1) {}


  @main
def main2(): Unit = {

  println(s"PosInt: ${PosInt(123) + 10}")
  println(s"PosInt: ${PosInt(123)}")

  ff(
    1,
    2,
    3,
  )

  ff(
    arg1 = 1,
    arg2 = 2,
    arg0 = 3,
  )
}

}

@inline
final def sum(x: Int, y: Int): Int = x + y
