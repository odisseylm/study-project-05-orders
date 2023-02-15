package org.mvv.scala.mapstruct

import scala.language.unsafeNulls
import scala.jdk.CollectionConverters.*
//
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import org.mvv.scala.mapstruct.testclasses.{GenericClass2, JGenericClass2}

class GenericsTest {

  @Test
  def test(): Unit = {

    val inspector = ScalaBeansInspector()
    val _class =  inspector.inspectClass(classOf[GenericClass2])

    println(_class.parents.dump("Parents"))
    println(_class.parentTypeNames.dump("Parent type names"))
    println(_class.fields.keys.dump("Fields"))
    println(_class.methods.keys.dump("Methods"))

    val a = SoftAssertions()

    a.assertThat(_class.fields.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
      "baseClass1Var1: GenericBaseClass1.this.C/java.lang.Object",
      "class2Var: java.lang.String",
      "bVal: java.time.LocalTime",
      "aVar: GenericTrait1.this.A/java.lang.Object",
      "cVal: java.lang.String",
    )

    a.assertThat(_class.declaredFields.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
      //"baseClass1Var1: java.lang.Object",
      //"aVar: java.lang.Object",
      "class2Var: java.lang.String",
      "bVal: java.time.LocalTime",
      "cVal: java.lang.String",
    )

    a.assertThat(_class.methods.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
      "baseClass1Var1_=(GenericBaseClass1.this.C/java.lang.Object)",
      "aVar_=(GenericTrait1.this.A/java.lang.Object)",
    )

    a.assertAll()
  }

  @Test
  def showJavaImplMethods(): Unit = {
    val cls = classOf[JGenericClass2]
    val ms = cls.getMethods.nnArray
    println(ms.mkString("\n"))
  }

  extension (collection: IterableOnce[?])
    private def dump(label: String): String =
      collection.iterator.mkString(s"$label:\n    ", "\n    ", "\n")
}
