package org.mvv.mapstruct.scala.debug

import org.junit.jupiter.api.Test


enum TestEnum1 :
    case TestEnumValue1, TestEnumValue2
    case TestEnumValue3
enum TestEnum2 :
    case TestEnumValue1, TestEnumValue2


class DumpTest {

  @Test
  def testDump1(): Unit = {
    val v = 1

    val v222: (TestEnum1 => TestEnum2) = dumpExpr( vvvv => vvvv match
            case org.mvv.mapstruct.scala.debug.TestEnum1.TestEnumValue1 => org.mvv.mapstruct.scala.debug.TestEnum2.TestEnumValue1
            //case org.mvv.mapstruct.scala.debug.TestEnum1.TestEnumValue2 => org.mvv.mapstruct.scala.debug.TestEnum2.TestEnumValue2
    )
    println(v222)


  }

}