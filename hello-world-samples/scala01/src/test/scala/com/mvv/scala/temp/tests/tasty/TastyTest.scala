package com.mvv.scala.temp.tests.tasty

import scala.tasty.inspector.Tasty
import scala.tasty.inspector.TastyInspector
//
import org.junit.jupiter.api.Test
import org.assertj.core.api.SoftAssertions


class TastyTest :

  @Test
  def inspectBeans(): Unit = {
    val classesDir = "/home/vmelnykov/projects/study-project-05-orders/hello-world-samples/scala01/target/classes"
    //val tastyFiles = List(s"$classesDir/com/mvv/scala/temp/tests/tasty/CaseScalaClassSample.tasty")

    val tastyFiles = List(s"$classesDir/com/mvv/scala/temp/tests/tasty/A3.tasty")
    TastyInspector.inspectTastyFiles(tastyFiles)(ScalaBeansInspector())
  }

  @Test
  def visibilityTest(): Unit = {
    import scala.language.unsafeNulls
    import scala.jdk.CollectionConverters.*

    val classesDir = "/home/vmelnykov/projects/study-project-05-orders/hello-world-samples/scala01/target/classes"

    val tastyFiles = List(s"$classesDir/com/mvv/scala/temp/tests/tasty/InheritedClass1.tasty")
    val inspector = ScalaBeansInspector()
    TastyInspector.inspectTastyFiles(tastyFiles)(inspector)

    val a = SoftAssertions()

    val r: scala.collection.mutable.Map[String, _Class] = inspector.classesByFullName
    a.assertThat(r).isNotNull()
    a.assertThat(r.asJava)
      .hasSize(1)
      .containsKey("com.mvv.scala.temp.tests.tasty.InheritedClass1")

    a.assertAll()
  }

  @Test
  def inheritanceTest(): Unit = {
    import scala.language.unsafeNulls
    import scala.jdk.CollectionConverters.*

    val classesDir = "/home/vmelnykov/projects/study-project-05-orders/hello-world-samples/scala01/target/classes"

    val tastyFiles = List(s"$classesDir/com/mvv/scala/temp/tests/tasty/InheritedClass1.tasty")
    val inspector = ScalaBeansInspector()
    TastyInspector.inspectTastyFiles(tastyFiles)(inspector)

    val a = SoftAssertions()

    val r: scala.collection.mutable.Map[String, _Class] = inspector.classesByFullName
    a.assertThat(r).isNotNull()
    a.assertThat(r.asJava)
      //.hasSize(4)
      .containsOnlyKeys(
        "com.mvv.scala.temp.tests.tasty.Trait1",
        "com.mvv.scala.temp.tests.tasty.Trait2",
        "com.mvv.scala.temp.tests.tasty.BaseClass1",
        "com.mvv.scala.temp.tests.tasty.InheritedClass1",
      )

    val _class = r("com.mvv.scala.temp.tests.tasty.InheritedClass1")

    a.assertThat(_class.fields.keys.asJava).containsExactlyInAnyOrder(
      "trait1Val", "trait2Val",
      "trait1Var", "trait2Var",
      "privateValField0", "protectedValField0", "publicValField0",
      "privateVarField0", "protectedVarField0", "publicVarField0",
      "privateValField1", "protectedValField1", "publicValField1",
      "privateVarField1", "protectedVarField1", "publicVarField1",
      "privateValField2", "protectedValField2", "publicValField2",
      "privateVarField2", "protectedVarField2", "publicVarField2",
    )

    a.assertThat(_class.methods.keys.map(_.signature).asJava).containsExactlyInAnyOrder(
      "trait1Method:java.lang.String:false",
      "trait2Method:java.lang.String:false",
      //
      "privateMethod0:java.lang.String:false",
      "protectedMethod0:java.lang.String:false",
      "publicMethod0:java.lang.String:false",
      //
      "privateMethod1:java.lang.String:false",
      "protectedMethod1:java.lang.String:false",
      "publicMethod1:java.lang.String:false",
      //
      "privateMethod2:java.lang.String:false",
      "protectedMethod2:java.lang.String:false",
      "publicMethod2:java.lang.String:false",
      // for vars
      "trait1Var_=:scala.Unit:true",
      "trait2Var_=:scala.Unit:true",

      "protectedVarField0_=:scala.Unit:true",
      "publicVarField0_=:scala.Unit:true",

      "protectedVarField1_=:scala.Unit:true",
      "publicVarField1_=:scala.Unit:true",

      "protectedVarField2_=:scala.Unit:true",
      "publicVarField2_=:scala.Unit:true"
    )

    a.assertAll()
  }

end TastyTest
