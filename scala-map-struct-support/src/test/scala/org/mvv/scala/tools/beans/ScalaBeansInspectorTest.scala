package org.mvv.scala.tools.beans;

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.{Disabled, Test};



class ScalaBeansInspectorTest {

  private val classesDir = "/home/vmelnykov/projects/study-project-05-orders/scala-map-struct-processor-support/target/test-classes"

  @Test
  @Disabled
  def aa(): Unit = {

    val inspector = ScalaBeansInspector()
    inspector.inspectTastyFile(s"$classesDir/org/mvv/scala/mapstruct/testclasses/InheritedFromJavaClass2.tasty")



  }

}
