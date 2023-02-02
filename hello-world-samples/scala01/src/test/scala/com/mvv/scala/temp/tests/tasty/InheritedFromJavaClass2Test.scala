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
      a.assertThat(_class.fields.keys.asJava).containsExactlyInAnyOrder(
        "trait1Val",
        "trait1Var",
      )
      a.assertThat(_class.methods.keys.map(_.signature).asJava).containsExactlyInAnyOrder(
        //"trait1Val:java.lang.String:false", // it will be in fields
        //"trait1Var:java.lang.String:false", // it will be in fields
        "trait1Var_=:scala.Unit:true",
        "trait1Method:java.lang.String:false",
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
      a.assertThat(_class.fields.keys.asJava).containsExactlyInAnyOrder(
        "trait1Val",
        "trait1Var",
      )
      a.assertThat(_class.methods.keys.map(_.signature).asJava).containsExactlyInAnyOrder(
        "trait1Var_=:scala.Unit:true",
        "trait1Method:java.lang.String:false",
      ) }

    val trait2ClassFullName = "com.mvv.scala.temp.tests.tasty.Trait2"
    a.assertThat(r.asJava).containsKey(trait2ClassFullName)
    r.get(trait2ClassFullName).foreach { _class =>
      a.assertThat(_class.fields.keys.asJava).containsExactlyInAnyOrder(
        "trait2Val",
        "trait2Var",
      )
      a.assertThat(_class.methods.keys.map(_.signature).asJava).containsExactlyInAnyOrder(
        "trait2Var_=:scala.Unit:true",
        "trait2Method:java.lang.String:false",
      ) }

    val javaInterface1ClassFullName = "com.mvv.scala.temp.tests.tasty.JavaInterface1"
    a.assertThat(r.asJava).containsKey(javaInterface1ClassFullName)
    r.get(javaInterface1ClassFullName).foreach { _class =>
      a.assertThat(_class.methods.keys.map(_.signature).asJava).containsExactlyInAnyOrder(
        "getInterfaceValue1:java.lang.String:false",
        "methodInterface1:java.lang.String:false",
        "getInterfaceValue11:java.lang.String:false",
        "setInterfaceValue11:void:true",
      ) }

    val javaInterface2ClassFullName = "com.mvv.scala.temp.tests.tasty.JavaInterface2"
    a.assertThat(r.asJava).containsKey(javaInterface2ClassFullName)
    r.get(javaInterface2ClassFullName).foreach { _class =>
      a.assertThat(_class.methods.keys.map(_.signature).asJava).containsExactlyInAnyOrder(
        "getInterfaceValue2:java.lang.String:false",
        "methodInterface2:java.lang.String:false",
      ) }

    val baseJavaClass1ClassFullName = "com.mvv.scala.temp.tests.tasty.BaseJavaClass1"
    a.assertThat(r.asJava).containsKey(baseJavaClass1ClassFullName)
    r.get(baseJavaClass1ClassFullName).foreach { _class =>
      a.assertThat(_class.declaredFields.keys.asJava).containsExactlyInAnyOrder(
        "privateField1",
        "packageField1",
        "protectedField1",
        "publicField1",
      )
      a.assertThat(_class.declaredMethods.keys.map(_.signature).asJava).containsExactlyInAnyOrder(
        "privateMethod1:java.lang.String:false",
        "packageMethod1:java.lang.String:false",
        "protectedMethod1:java.lang.String:false",
        "publicMethod1:java.lang.String:false",
        //
        "getPrivateProp1:java.lang.String:false",
        "setPrivateProp1:void:true",
        "getPackageProp1:java.lang.String:false",
        "setPackageProp1:void:true",
        "getProtectedProp1:java.lang.String:false",
        "setProtectedProp1:void:true",
        "getPublicProp1:java.lang.String:false",
        "setPublicProp1:void:true",
      )
    }

    val inheritedClass2FullName = "com.mvv.scala.temp.tests.tasty.InheritedFromJavaClass2"
    a.assertThat(r.asJava).containsKey(inheritedClass2FullName)

    r.get(inheritedClass2FullName).foreach { _class =>
      a.assertThat(_class.declaredFields.keys.asJava).containsExactlyInAnyOrder(
        "privateValField1",
        "protectedValField1",
        "publicValField1",
        "interfaceValue11",
        // seems they are present in methods section
        //"privateValMethod2",
        //"protectedValMethod2",
        //"publicValMethod2",
      )
      a.assertThat(_class.declaredMethods.keys.map(_.signature).asJava).containsExactlyInAnyOrder(
        // value methods (without '()')
        "privateValMethod2:java.lang.String:false",
        "protectedValMethod2:java.lang.String:false",
        "publicValMethod2:java.lang.String:false",
        // usual methods
        "privateMethod2:java.lang.String:false",
        "protectedMethod2:java.lang.String:false",
        "publicMethod2:java.lang.String:false",
        // overriding java getters/setters
        "getInterfaceValue11:java.lang.String:false",
        "setInterfaceValue11:scala.Unit:true",
      ) }

    r.get(inheritedClass2FullName).foreach { _class =>
      a.assertThat(_class.fields.keys.asJava).containsExactlyInAnyOrder(
        // Trait
        "trait1Val",
        "trait1Var",
        // Trait2
        "trait2Val",
        "trait2Var",
        // JavaInterface1
        // JavaInterface2
        // BaseJavaClass1
        "privateField1",
        "packageField1",
        "protectedField1",
        "publicField1",
        // InheritedFromJavaClass2
        "privateValField1",
        "protectedValField1",
        "publicValField1",
        "interfaceValue11",
      )
      a.assertThat(_class.methods.keys.map(_.signature).asJava).containsExactlyInAnyOrder(
        // Trait1
        "trait1Var_=:scala.Unit:true",
        "trait1Method:java.lang.String:false",
        // Trait2
        "trait2Var_=:scala.Unit:true",
        "trait2Method:java.lang.String:false",
        // JavaInterface1
        "getInterfaceValue1:java.lang.String:false",
        "methodInterface1:java.lang.String:false",
        // these 2 are overridden
        //"getInterfaceValue11:java.lang.String:false",
        //"setInterfaceValue11:void:true",
        // JavaInterface2
        "getInterfaceValue2:java.lang.String:false",
        "methodInterface2:java.lang.String:false",
        // BaseJavaClass1
        "privateMethod1:java.lang.String:false",
        "packageMethod1:java.lang.String:false",
        "protectedMethod1:java.lang.String:false",
        "publicMethod1:java.lang.String:false",
        // java props
        "getPrivateProp1:java.lang.String:false",
        "setPrivateProp1:void:true",
        "getPackageProp1:java.lang.String:false",
        "setPackageProp1:void:true",
        "getProtectedProp1:java.lang.String:false",
        "setProtectedProp1:void:true",
        "getPublicProp1:java.lang.String:false",
        "setPublicProp1:void:true",
        // in InheritedFromJavaClass2
        // value methods (without '()')
        "privateValMethod2:java.lang.String:false",
        "protectedValMethod2:java.lang.String:false",
        "publicValMethod2:java.lang.String:false",
        // usual methods
        "privateMethod2:java.lang.String:false",
        "protectedMethod2:java.lang.String:false",
        "publicMethod2:java.lang.String:false",
        // overriding java getters/setters
        "getInterfaceValue11:java.lang.String:false",
        "setInterfaceValue11:scala.Unit:true",
      ) }

    a.assertAll()
  }

}
