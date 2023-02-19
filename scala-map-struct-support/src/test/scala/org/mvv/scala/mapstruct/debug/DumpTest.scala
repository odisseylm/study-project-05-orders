package org.mvv.scala.mapstruct.debug

import org.junit.jupiter.api.Test
import org.mvv.scala.mapstruct.debug.dump.dumpExpr


//noinspection ScalaUnnecessaryParentheses
class DumpTest {

  @Test
  def testDump1(): Unit = {
    val v222: (TestEnum1 => TestEnum2) = dumpExpr( vvvv => vvvv match
            case org.mvv.scala.mapstruct.debug.TestEnum1.TestEnumValue1 => org.mvv.scala.mapstruct.debug.TestEnum2.TestEnumValue1
            //case org.mvv.scala.mapstruct.debug.TestEnum1.TestEnumValue2 => org.mvv.scala.mapstruct.debug.TestEnum2.TestEnumValue2
    )
    println(v222)
  }

  @Test
  def testDump2(): Unit = {
    val v = "1"
    //noinspection ScalaUnusedSymbol
    val opt: Option[String] = dumpExpr( Option(v) )
  }
}
