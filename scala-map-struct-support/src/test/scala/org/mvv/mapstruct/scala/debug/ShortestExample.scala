package org.mvv.mapstruct.scala.debug

import org.mvv.mapstruct.scala.debug.dump.dumpExpr

class ShortestExample {

  val v1: (TestEnum1 => TestEnum2) = dumpExpr(vvvv => vvvv match
    case org.mvv.mapstruct.scala.debug.TestEnum1.TestEnumValue1 => org.mvv.mapstruct.scala.debug.TestEnum2.TestEnumValue1
  )

  val v2: (TestEnum1 => TestEnum2) = dumpExpr(vvvv => vvvv match
    case org.mvv.mapstruct.scala.debug.TestEnum1.TestEnumValue1 => org.mvv.mapstruct.scala.debug.TestEnum2.TestEnumValue1
  )

}
