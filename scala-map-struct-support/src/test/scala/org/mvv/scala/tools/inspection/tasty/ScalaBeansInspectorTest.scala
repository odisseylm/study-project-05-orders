package org.mvv.scala.tools.inspection.tasty

import org.junit.jupiter.api.{ Test, Disabled }
//
import org.mvv.scala.testClassesDir



class ScalaBeansInspectorTest {

  @Test
  def aa(): Unit = {
    val inspector = TastyScalaBeansInspector()
    inspector.inspectTastyFile(s"$testClassesDir/org/mvv/scala/tools/beans/testclasses/InheritedFromJavaClass2.tasty")
  }
}
