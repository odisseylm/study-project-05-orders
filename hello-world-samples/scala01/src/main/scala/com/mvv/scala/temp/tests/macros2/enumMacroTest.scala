package com.mvv.scala.temp.tests.macros2

//
import com.mvv.scala.macros.dumpTerm


enum TestEnum1 :
  case TestEnumValue1, TestEnumValue2
  case TestEnumValue3
enum TestEnum2 :
  case TestEnumValue1, TestEnumValue2


@main
def test1(): Unit = {


  val v1: TestEnum1 = TestEnum1.TestEnumValue2

  val aa22: (TestEnum1 => TestEnum2) = enumMappingFunc[TestEnum1,TestEnum2]()

  val res22 = aa22(TestEnum1.TestEnumValue1)
  println(s"res22: $res22")

  val res23 = aa22(TestEnum1.TestEnumValue2)
  println(s"res22: $res23")

  //val aa2: Any = enumMappingFunc[TestEnum1,TestEnum2]()
  //val aaaa: Any = enumMappingFunc[TestEnum1,TestEnum2]()
  val aa: scala.reflect.Enum = TestEnum1.TestEnumValue2

//  val v2: TestEnum2 = dumpTerm( v1 match
//    case TestEnum1.TestEnumValue1 => TestEnum2.TestEnumValue1
//    case TestEnum1.TestEnumValue2 => TestEnum2.TestEnumValue2
//  )

  val v221: (TestEnum1 => TestEnum2) = vvvv => vvvv match
    case TestEnum1.TestEnumValue1 => TestEnum2.TestEnumValue1
    case TestEnum1.TestEnumValue2 => TestEnum2.TestEnumValue2


  val v222: (TestEnum1 => TestEnum2) = dumpTerm( vvvv => vvvv match
    case TestEnum1.TestEnumValue1 => TestEnum2.TestEnumValue1
    case TestEnum1.TestEnumValue2 => TestEnum2.TestEnumValue2
  )
  println(v222)


  //val v222: TestEnum2 = dumpTerm(v1 match
  //  case TestEnum1.TestEnumValue1 => TestEnum2.TestEnumValue1
  //  case TestEnum1.TestEnumValue2 => TestEnum2.TestEnumValue2
  //)

}

val v1111: TestEnum1 = TestEnum1.TestEnumValue2

//val v24443: TestEnum2 = dumpTerm(v1111 match { case TestEnum1.TestEnumValue1 => TestEnum2.TestEnumValue1 } )
//val v24444: TestEnum2 = dumpTerm(v1111 match { case com.mvv.scala.temp.tests.macros2.TestEnum1.TestEnumValue1 => com.mvv.scala.temp.tests.macros2.TestEnum2.TestEnumValue1 } )
//val v24445: TestEnum2 = dumpTerm(v1111 match { case `com.mvv.scala.temp.tests.macros2`.TestEnum1.TestEnumValue1 => `com.mvv.scala.temp.tests.macros2`.TestEnum2.TestEnumValue1 } )

