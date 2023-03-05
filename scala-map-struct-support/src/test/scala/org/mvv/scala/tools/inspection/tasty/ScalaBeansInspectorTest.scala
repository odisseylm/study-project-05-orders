package org.mvv.scala.tools.inspection.tasty

import org.junit.jupiter.api.{ Test, Disabled }
//
import org.mvv.scala.tools.inspection.tasty.ScalaBeansInspector



class ScalaBeansInspectorTest {

  @Test
  def aa(): Unit = {
    val inspector = ScalaBeansInspector()
    inspector.inspectTastyFile(s"$testClassesDir/org/mvv/scala/tools/beans/testclasses/InheritedFromJavaClass2.tasty")
  }
}
