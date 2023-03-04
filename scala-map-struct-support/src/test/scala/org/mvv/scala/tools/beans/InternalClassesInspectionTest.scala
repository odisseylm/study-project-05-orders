package org.mvv.scala.tools.beans

import scala.language.unsafeNulls
import scala.jdk.CollectionConverters.*
//
import org.junit.jupiter.api.Test
import org.assertj.core.api.SoftAssertions
//
import org.mvv.scala.tools.inspection.tasty.ScalaBeansInspector



class TopClass :
  class TopClassInternalClassLevel1 :
    class TopClassInternalClassLevel2


object TopClass :
  class TopObjectInternalClassLevel1:
    class TopObjectInternalClassLevel2

  object TopObjectObjectLevel2 :
    class TopObjectObjectLevel2InternalClassLevel1:
      class TopObjectObjectLevel2InternalClassLevel2



class InternalClassesInspectionTest {

  @Test
  def inspectInternalClasses(): Unit = {
    val inspector = ScalaBeansInspector()
    inspector.inspectClass(classOf[TopClass])

    val inspectedClasses = inspector.classesDescr
    val allClassesFromKeys = inspectedClasses.keys
    val allClassesFromValues = inspectedClasses.values.map(_.fullName)

    println(s"%%% allClassesFromKeys: " + allClassesFromKeys.mkString("\n", "\n", "\n"))
    println(s"%%% allClassesFromValues: " + allClassesFromKeys.mkString("\n", "\n", "\n"))

    val a = SoftAssertions()

    a.assertThat(allClassesFromValues.toList.asJava).isEqualTo(allClassesFromKeys.toList.asJava)

    a.assertThat(inspectedClasses.keys.asJava).containsExactlyInAnyOrder(
      "org.mvv.scala.tools.beans.TopClass",
      "org.mvv.scala.tools.beans.TopClass.TopClassInternalClassLevel1",
      "org.mvv.scala.tools.beans.TopClass.TopClassInternalClassLevel1.TopClassInternalClassLevel2",
      //
      "org.mvv.scala.tools.beans.TopClass$",
      "org.mvv.scala.tools.beans.TopClass$.TopObjectInternalClassLevel1",
      "org.mvv.scala.tools.beans.TopClass$.TopObjectInternalClassLevel1.TopObjectInternalClassLevel2",
      //
      "org.mvv.scala.tools.beans.TopClass$.TopObjectObjectLevel2$",
      "org.mvv.scala.tools.beans.TopClass$.TopObjectObjectLevel2$.TopObjectObjectLevel2InternalClassLevel1",
      "org.mvv.scala.tools.beans.TopClass$.TopObjectObjectLevel2$.TopObjectObjectLevel2InternalClassLevel1.TopObjectObjectLevel2InternalClassLevel2",
    )

    a.assertAll()
  }
}
