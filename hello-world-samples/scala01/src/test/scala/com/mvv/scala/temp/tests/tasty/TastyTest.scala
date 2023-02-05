package com.mvv.scala.temp.tests.tasty

import scala.tasty.inspector.Tasty
import scala.tasty.inspector.TastyInspector
import scala.collection.immutable.Map
import scala.jdk.CollectionConverters.*
//
import org.junit.jupiter.api.Test
import org.assertj.core.api.SoftAssertions


class TastyTest :
  private val classesDir = "/home/vmelnykov/projects/study-project-05-orders/hello-world-samples/scala01/target/classes"

  @Test
  def inspectBeansNoToFailAtAll(): Unit = {
    val classesDir = "/home/vmelnykov/projects/study-project-05-orders/hello-world-samples/scala01/target/classes"
    //val tastyFiles = List(s"$classesDir/com/mvv/scala/temp/tests/tasty/CaseScalaClassSample.tasty")

    val tastyFiles = List(s"$classesDir/com/mvv/scala/temp/tests/tasty/A3.tasty")
    TastyInspector.inspectTastyFiles(tastyFiles)(ScalaBeansInspector())
  }


  @Test
  def inheritanceTest(): Unit = {
    import scala.language.unsafeNulls
    import scala.jdk.CollectionConverters.*

    val tastyFiles = List(s"$classesDir/com/mvv/scala/temp/tests/tasty/InheritedClass1.tasty")
    val inspector = ScalaBeansInspector()
    TastyInspector.inspectTastyFiles(tastyFiles)(inspector)

    val a = SoftAssertions()

    val r: Map[String, _Class] = inspector.classesDescr
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

    a.assertThat(_class.methods.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
      "trait1ValMethod: java.lang.String",
      "trait1Method(): java.lang.String",
      "trait2ValMethod: java.lang.String",
      "trait2Method(): java.lang.String",
      //
      "privateValMethod0: java.lang.String",
      "protectedValMethod0: java.lang.String",
      "publicValMethod0: java.lang.String",
      //
      "privateMethod1(): java.lang.String",
      "protectedMethod1(): java.lang.String",
      "publicMethod1(): java.lang.String",
      //
      "privateMethod2(): java.lang.String",
      "protectedMethod2(): java.lang.String",
      "publicMethod2(): java.lang.String",
      // for vars
      "trait1Var_=(java.lang.String)",
      "trait2Var_=(java.lang.String)",

      "protectedVarField0_=(java.lang.String)",
      "publicVarField0_=(java.lang.String)",

      "protectedVarField1_=(java.lang.String)",
      "publicVarField1_=(java.lang.String)",

      "protectedVarField2_=(java.lang.String)",
      "publicVarField2_=(java.lang.String)"
    )

    a.assertAll()
  }


  @Test
  def inheritanceInheritedFromJavaClass1(): Unit = {
    import scala.language.unsafeNulls
    import scala.jdk.CollectionConverters.*

    val inspector = ScalaBeansInspector()
    inspector.inspectClass(classOf[InheritedFromJavaClass1])

    val a = SoftAssertions()

    val r: Map[String, _Class] = inspector.classesDescr
    a.assertThat(r).isNotNull()
    a.assertThat(r.asJava)
      //.hasSize(4)
      .containsOnlyKeys(
        "com.mvv.scala.temp.tests.tasty.Trait1",
        "com.mvv.scala.temp.tests.tasty.Trait2",
        "com.mvv.scala.temp.tests.tasty.JavaInterface1",
        "com.mvv.scala.temp.tests.tasty.JavaInterface2",
        "com.mvv.scala.temp.tests.tasty.BaseJavaClass1",
        "com.mvv.scala.temp.tests.tasty.BaseJavaClass2",
        "com.mvv.scala.temp.tests.tasty.InheritedFromJavaClass1",
      )

    val _class = r("com.mvv.scala.temp.tests.tasty.InheritedFromJavaClass1")

    a.assertThat(_class.fields.keys.asJava).containsExactlyInAnyOrder(
      "trait1Val", "trait2Val",
      "trait1Var", "trait2Var",
      // BaseJavaClass1
      "privateField1", "packageField1", "protectedField1", "publicField1",
      // BaseJavaClass2
      //  no fields
      // InheritedFromJavaClass1
      "privateValField1", "protectedValField1", "publicValField1",
      "publicVarField1",
      "interfaceValue11",
    )

    a.assertThat(_class.methods.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
      "trait1ValMethod: java.lang.String",
      "trait1Method(): java.lang.String",
      "trait2ValMethod: java.lang.String",
      "trait2Method(): java.lang.String",
      // JavaInterface1
      "getInterfaceValue1(): java.lang.String",
      "methodInterface1(): java.lang.String",
      // they are overridden in BaseJavaClass2
      //"getInterfaceValue11(): java.lang.String",
      //"setInterfaceValue11(java.lang.String)",
      // JavaInterface2
      "getInterfaceValue2(): java.lang.String",
      "methodInterface2(): java.lang.String",
      // BaseJavaClass1
      "privateMethod1(): java.lang.String",
      "packageMethod1(): java.lang.String",
      "protectedMethod1(): java.lang.String",
      "publicMethod1(): java.lang.String",
      //
      "getPrivateProp1(): java.lang.String",
      "setPrivateProp1(java.lang.String)",
      "getProtectedProp1(): java.lang.String",
      "setProtectedProp1(java.lang.String)",
      "getPackageProp1(): java.lang.String",
      "setPackageProp1(java.lang.String)",
      "getPublicProp1(): java.lang.String",
      "setPublicProp1(java.lang.String)",
      // BaseJavaClass2
      "getPublicProp11(): java.lang.String",
      "setPublicProp11(java.lang.String)",
      "getInterfaceValue11(): java.lang.String",
      "setInterfaceValue11(java.lang.String)",
      // InheritedFromJavaClass1
      "privateValMethod2: java.lang.String",
      "privateMethod2(): java.lang.String",
      "protectedValMethod2: java.lang.String",
      "protectedMethod2(): java.lang.String",
      "publicValMethod2: java.lang.String",
      "publicMethod2(): java.lang.String",

      // for vars
      "publicVarField1_=(java.lang.String)",
      "trait1Var_=(java.lang.String)",
      "trait2Var_=(java.lang.String)",

      /*
      "protectedVarField0_=(java.lang.String)",
      "publicVarField0_=(java.lang.String)",

      "protectedVarField1_=(java.lang.String)",
      "publicVarField1_=(java.lang.String)",

      "protectedVarField2_=(java.lang.String)",
      "publicVarField2_=(java.lang.String)",
      */
    )

    a.assertAll()
  }



end TastyTest
