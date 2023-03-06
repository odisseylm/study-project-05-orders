package org.mvv.scala.tools.inspection.tasty


import scala.language.unsafeNulls
import scala.jdk.CollectionConverters.*
//
import org.junit.jupiter.api.Test
import org.assertj.core.api.SoftAssertions
//
import org.mvv.scala.tools.{ nnArray, Logger }
import org.mvv.scala.tools.quotes.topClassOrModuleFullName
import org.mvv.scala.tools.beans.testclasses.{GenericClass2, JGenericClass2}



class GenericsTest {
  private val log = Logger(topClassOrModuleFullName)

  @Test
  def test(): Unit = {

    val inspector = TastyScalaBeansInspector()
    val _class =  inspector.inspectClass(classOf[GenericClass2])

    log.info(_class.parentClasses.dump("Parents"))
    log.info(_class.parentTypes.dump("Parent type names"))
    log.info(_class.fields.keys.dump("Fields"))
    log.info(_class.methods.keys.dump("Methods"))

    val a = SoftAssertions()

    /*
    // with runtime types
    a.assertThat(_class.fields.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
      "baseClass1Var1: GenericBaseClass1.this.C/java.lang.Object",
      "class2Var: java.lang.String",
      "bVal: java.time.LocalTime",
      "aVar: GenericTrait1.this.A/java.lang.Object",
      "cVal: java.lang.String",
    )
    */

    a.assertThat(_class.fields.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
      "baseClass1Var1: GenericBaseClass1.this.C",
      "class2Var: java.lang.String",
      "bVal: java.time.LocalTime",
      "aVar: GenericTrait1.this.A",
      "cVal: java.lang.String",
    )

    a.assertThat(_class.declaredFields.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
      //"baseClass1Var1: java.lang.Object",
      //"aVar: java.lang.Object",
      "class2Var: java.lang.String",
      "bVal: java.time.LocalTime",
      "cVal: java.lang.String",
    )

    /*
    // with runtime types
    a.assertThat(_class.methods.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
      "baseClass1Var1_=(GenericBaseClass1.this.C/java.lang.Object)",
      "aVar_=(GenericTrait1.this.A/java.lang.Object)",
    )
    */
    a.assertThat(_class.methods.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
      "baseClass1Var1_=(GenericBaseClass1.this.C)",
      "aVar_=(GenericTrait1.this.A)",
    )

    a.assertAll()
  }

  @Test
  def showJavaImplMethods(): Unit = {
    val cls = classOf[JGenericClass2]
    val ms = cls.getMethods.nnArray
    log.info(ms.mkString("\n"))
  }

  extension (collection: IterableOnce[?])
    private def dump(label: String): String =
      collection.iterator.mkString(s"$label:\n    ", "\n    ", "\n")
}
