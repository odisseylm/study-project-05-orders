package org.mvv.scala.tools.inspection.light

import scala.language.unsafeNulls
import scala.jdk.CollectionConverters.*
//
import org.junit.jupiter.api.Test
import org.assertj.core.api.SoftAssertions
//
import org.mvv.scala.tools.inspection.TopClass



class InternalClassesInspectionTest {

  @Test
  def inspectInternalClasses(): Unit = {
    val inspector = ScalaBeanInspector()
    inspector.inspectClass(classOf[TopClass])

    val inspectedClasses = inspector.classesDescr
    val allClassesFromKeys = inspectedClasses.keys
    val allClassesFromValues = inspectedClasses.values.map(_.fullName)

    println(s"%%% allClassesFromKeys: " + allClassesFromKeys.mkString("\n", "\n", "\n"))
    println(s"%%% allClassesFromValues: " + allClassesFromKeys.mkString("\n", "\n", "\n"))

    val a = SoftAssertions()

    a.assertThat(allClassesFromValues.toList.asJava).isEqualTo(allClassesFromKeys.toList.asJava)

    a.assertThat(inspectedClasses.keys.asJava).containsExactlyInAnyOrder(
      "org.mvv.scala.tools.inspection.TopClass",
      //"org.mvv.scala.tools.inspection.TopClass.TopClassInternalClassLevel1",
      //"org.mvv.scala.tools.inspection.TopClass.TopClassInternalClassLevel1.TopClassInternalClassLevel2",
      ////
      //"org.mvv.scala.tools.inspection.TopClass$",
      //"org.mvv.scala.tools.inspection.TopClass$.TopObjectInternalClassLevel1",
      //"org.mvv.scala.tools.inspection.TopClass$.TopObjectInternalClassLevel1.TopObjectInternalClassLevel2",
      ////
      //"org.mvv.scala.tools.inspection.TopClass$.TopObjectObjectLevel2$",
      //"org.mvv.scala.tools.inspection.TopClass$.TopObjectObjectLevel2$.TopObjectObjectLevel2InternalClassLevel1",
      //"org.mvv.scala.tools.inspection.TopClass$.TopObjectObjectLevel2$.TopObjectObjectLevel2InternalClassLevel1.TopObjectObjectLevel2InternalClassLevel2",
    )

    a.assertAll()
  }
}
