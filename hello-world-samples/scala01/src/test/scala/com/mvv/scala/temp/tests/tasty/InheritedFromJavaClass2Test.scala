package com.mvv.scala.temp.tests.tasty

import scala.tasty.inspector.Tasty
import scala.tasty.inspector.TastyInspector
import scala.collection.immutable.Map
import scala.jdk.CollectionConverters.*
//
import org.junit.jupiter.api.Test
import org.assertj.core.api.SoftAssertions


class InheritedFromJavaClass2Test {
  private val classesDir = "/home/vmelnykov/projects/study-project-05-orders/hello-world-samples/scala01/target/classes"


  @Test
  def testTrait1(): Unit = {
    import scala.language.unsafeNulls
    import scala.jdk.CollectionConverters.*

    val tastyFile = s"$classesDir/com/mvv/scala/temp/tests/tasty/Trait1.tasty"
    val tastyClassFullName = "com.mvv.scala.temp.tests.tasty.Trait1"

    val inspector = ScalaBeansInspector()
    inspector.inspectTastyFile(tastyFile)

    val r: Map[String, _Class] = inspector.classesDescr

    val a = SoftAssertions()
    a.assertThat(r).isNotNull()
    a.assertThat(r.asJava)
      .containsOnlyKeys(tastyClassFullName)

    r.get(tastyClassFullName).foreach { _class =>
      a.assertThat(_class.fields.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
        "trait1Val: java.lang.String",
        "trait1Var: java.lang.String",
      )
      a.assertThat(_class.methods.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
        //"trait1Val: java.lang.String", // it will be in fields
        //"trait1Var: java.lang.String", // it will be in fields
        "trait1Var_=(java.lang.String)",
        "trait1ValMethod: java.lang.String",
        "trait1Method(): java.lang.String",
      )
    }

    a.assertAll()
  }

  @Test
  def inheritanceInheritedFromJavaClass2(): Unit = {
    import scala.language.unsafeNulls
    import scala.jdk.CollectionConverters.*

    val tastyFile = s"$classesDir/com/mvv/scala/temp/tests/tasty/InheritedFromJavaClass2.tasty"
    val tastyClassFullName = "com.mvv.scala.temp.tests.tasty.InheritedFromJavaClass2"

    val inspector = ScalaBeansInspector()
    inspector.inspectTastyFile(tastyFile)

    val a = SoftAssertions()

    val r: Map[String, _Class] = inspector.classesDescr
    a.assertThat(r).isNotNull()
    a.assertThat(r.asJava)
      .containsOnlyKeys(
        "com.mvv.scala.temp.tests.tasty.JavaInterface1",
        "com.mvv.scala.temp.tests.tasty.JavaInterface2",
        "com.mvv.scala.temp.tests.tasty.Trait1",
        "com.mvv.scala.temp.tests.tasty.Trait2",
        "com.mvv.scala.temp.tests.tasty.BaseJavaClass1",
        tastyClassFullName,
      )

    val trait1ClassFullName = "com.mvv.scala.temp.tests.tasty.Trait1"
    a.assertThat(r.asJava).containsKey(trait1ClassFullName)
    r.get(trait1ClassFullName).foreach { _class =>
      a.assertThat(_class.fields.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
        "trait1Val: java.lang.String",
        "trait1Var: java.lang.String",
      )
      a.assertThat(_class.methods.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
        "trait1Var_=(java.lang.String)",
        "trait1ValMethod: java.lang.String",
        "trait1Method(): java.lang.String",
      ) }

    val trait2ClassFullName = "com.mvv.scala.temp.tests.tasty.Trait2"
    a.assertThat(r.asJava).containsKey(trait2ClassFullName)
    r.get(trait2ClassFullName).foreach { _class =>
      a.assertThat(_class.fields.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
        "trait2Val: java.lang.String",
        "trait2Var: java.lang.String",
      )
      a.assertThat(_class.methods.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
        "trait2Var_=(java.lang.String)",
        "trait2ValMethod: java.lang.String",
        "trait2Method(): java.lang.String",
      ) }

    val javaInterface1ClassFullName = "com.mvv.scala.temp.tests.tasty.JavaInterface1"
    a.assertThat(r.asJava).containsKey(javaInterface1ClassFullName)
    r.get(javaInterface1ClassFullName).foreach { _class =>
      a.assertThat(_class.methods.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
        "getInterfaceValue1(): java.lang.String",
        "methodInterface1(): java.lang.String",
        "getInterfaceValue11(): java.lang.String",
        "setInterfaceValue11(java.lang.String)",
      ) }

    val javaInterface2ClassFullName = "com.mvv.scala.temp.tests.tasty.JavaInterface2"
    a.assertThat(r.asJava).containsKey(javaInterface2ClassFullName)
    r.get(javaInterface2ClassFullName).foreach { _class =>
      a.assertThat(_class.methods.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
        "getInterfaceValue2(): java.lang.String",
        "methodInterface2(): java.lang.String",
      ) }

    val baseJavaClass1ClassFullName = "com.mvv.scala.temp.tests.tasty.BaseJavaClass1"
    a.assertThat(r.asJava).containsKey(baseJavaClass1ClassFullName)
    r.get(baseJavaClass1ClassFullName).foreach { _class =>
      a.assertThat(_class.declaredFields.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
        "privateField1: java.lang.String",
        "packageField1: java.lang.String",
        "protectedField1: java.lang.String",
        "publicField1: java.lang.String",
      )
      a.assertThat(_class.declaredMethods.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
        "privateMethod1(): java.lang.String",
        "packageMethod1(): java.lang.String",
        "protectedMethod1(): java.lang.String",
        "publicMethod1(): java.lang.String",
        //
        "getPrivateProp1(): java.lang.String",
        "setPrivateProp1(java.lang.String)",
        "getPackageProp1(): java.lang.String",
        "setPackageProp1(java.lang.String)",
        "getProtectedProp1(): java.lang.String",
        "setProtectedProp1(java.lang.String)",
        "getPublicProp1(): java.lang.String",
        "setPublicProp1(java.lang.String)",
      )
    }

    val inheritedClass2FullName = "com.mvv.scala.temp.tests.tasty.InheritedFromJavaClass2"
    a.assertThat(r.asJava).containsKey(inheritedClass2FullName)

    r.get(inheritedClass2FullName).foreach { _class =>
      a.assertThat(_class.declaredFields.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
        "privateValField1: java.lang.String",
        "protectedValField1: java.lang.String",
        "publicValField1: java.lang.String",
        "javaInterfaceValue11Var: java.lang.String",
        // seems they are present in methods section
        //"privateValMethod2: java.lang.String",
        //"protectedValMethod2: java.lang.String",
        //"publicValMethod2: java.lang.String",
      )
      a.assertThat(_class.declaredMethods.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
        // value methods (without '()')
        "privateValMethod2: java.lang.String",
        "protectedValMethod2: java.lang.String",
        "publicValMethod2: java.lang.String",
        // usual methods
        "privateMethod2(): java.lang.String",
        "protectedMethod2(): java.lang.String",
        "publicMethod2(): java.lang.String",
        // overriding java getters/setters
        "getInterfaceValue11: java.lang.String",
        "setInterfaceValue11(java.lang.String)",
      ) }

    r.get(inheritedClass2FullName).foreach { _class =>
      a.assertThat(_class.fields.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
        // Trait
        "trait1Val: java.lang.String",
        "trait1Var: java.lang.String",
        // Trait2
        "trait2Val: java.lang.String",
        "trait2Var: java.lang.String",
        // JavaInterface1
        // JavaInterface2
        // BaseJavaClass1
        "privateField1: java.lang.String",
        "packageField1: java.lang.String",
        "protectedField1: java.lang.String",
        "publicField1: java.lang.String",
        // InheritedFromJavaClass2
        "privateValField1: java.lang.String",
        "protectedValField1: java.lang.String",
        "publicValField1: java.lang.String",
        "javaInterfaceValue11Var: java.lang.String",
      )
      a.assertThat(_class.methods.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
        // Trait1
        "trait1Var_=(java.lang.String)",
        "trait1ValMethod: java.lang.String",
        "trait1Method(): java.lang.String",
        // Trait2
        "trait2Var_=(java.lang.String)",
        "trait2ValMethod: java.lang.String",
        "trait2Method(): java.lang.String",
        // JavaInterface1
        "getInterfaceValue1(): java.lang.String",
        "methodInterface1(): java.lang.String",
        // these 2 are overridden
        //"getInterfaceValue11:java.lang.String",
        //"setInterfaceValue11:void:true",
        // JavaInterface2
        "getInterfaceValue2(): java.lang.String",
        "methodInterface2(): java.lang.String",
        // BaseJavaClass1
        "privateMethod1(): java.lang.String",
        "packageMethod1(): java.lang.String",
        "protectedMethod1(): java.lang.String",
        "publicMethod1(): java.lang.String",
        // java props
        "getPrivateProp1(): java.lang.String",
        "setPrivateProp1(java.lang.String)",
        "getPackageProp1(): java.lang.String",
        "setPackageProp1(java.lang.String)",
        "getProtectedProp1(): java.lang.String",
        "setProtectedProp1(java.lang.String)",
        "getPublicProp1(): java.lang.String",
        "setPublicProp1(java.lang.String)",
        // in InheritedFromJavaClass2
        // value methods (without '()')
        "privateValMethod2: java.lang.String",
        "protectedValMethod2: java.lang.String",
        "publicValMethod2: java.lang.String",
        // usual methods
        "privateMethod2(): java.lang.String",
        "protectedMethod2(): java.lang.String",
        "publicMethod2(): java.lang.String",
        // overriding java getters/setters
        "getInterfaceValue11: java.lang.String",
        "setInterfaceValue11(java.lang.String)",
      ) }

    a.assertAll()
  }

}
