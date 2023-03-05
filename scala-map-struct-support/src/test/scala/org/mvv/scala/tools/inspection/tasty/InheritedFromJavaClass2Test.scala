package org.mvv.scala.tools.inspection.tasty

import scala.collection.immutable.Map
import scala.jdk.CollectionConverters.*
import scala.tasty.inspector.{ Tasty, TastyInspector }
//
import org.junit.jupiter.api.Test
import org.assertj.core.api.SoftAssertions
//
import org.mvv.scala.tools.inspection.{ _Class, _MethodKey }
import org.mvv.scala.tools.inspection.tasty.ScalaBeansInspector
import org.mvv.scala.tools.beans.testclasses.{ InheritedFromJavaClass2, Trait1 }



class InheritedFromJavaClass2Test {

  @Test
  def testTrait1(): Unit = {
    import scala.jdk.CollectionConverters.*
    import scala.language.unsafeNulls


    val cls = classOf[Trait1]

    val inspector = ScalaBeansInspector()
    inspector.inspectClass(cls)

    val r: Map[String, _Class] = inspector.classesDescr

    val a = SoftAssertions()
    a.assertThat(r).isNotNull()
    val tastyClassFullName = cls.getName
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

      a.assertThat(_class.methods.asJava)
        .containsKey( _MethodKey.setter[String]("trait1Var") )
      a.assertThat(_class.methods.asJava)
        .containsKey( _MethodKey.getter("trait1ValMethod") )
    }

    a.assertAll()
  }

  @Test
  def inheritanceInheritedFromJavaClass2(): Unit = {
    import scala.jdk.CollectionConverters.*
    import scala.language.unsafeNulls

    val cls = classOf[InheritedFromJavaClass2]
    val tastyClassFullName = cls.getName

    val inspector = ScalaBeansInspector()
    inspector.inspectClass(cls)

    val a = SoftAssertions()

    val r: Map[String, _Class] = inspector.classesDescr
    a.assertThat(r).isNotNull()
    a.assertThat(r.asJava)
      .containsOnlyKeys(
        "org.mvv.scala.tools.beans.testclasses.JavaInterface1",
        "org.mvv.scala.tools.beans.testclasses.JavaInterface2",
        "org.mvv.scala.tools.beans.testclasses.Trait1",
        "org.mvv.scala.tools.beans.testclasses.Trait2",
        "org.mvv.scala.tools.beans.testclasses.BaseJavaClass1",
        tastyClassFullName,
      )

    val trait1ClassFullName = "org.mvv.scala.tools.beans.testclasses.Trait1"
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

    val trait2ClassFullName = "org.mvv.scala.tools.beans.testclasses.Trait2"
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

    val javaInterface1ClassFullName = "org.mvv.scala.tools.beans.testclasses.JavaInterface1"
    a.assertThat(r.asJava).containsKey(javaInterface1ClassFullName)
    r.get(javaInterface1ClassFullName).foreach { _class =>
      a.assertThat(_class.methods.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
        "getInterfaceValue1(): java.lang.String",
        "methodInterface1(): java.lang.String",
        "getInterfaceValue11(): java.lang.String",
        "setInterfaceValue11(java.lang.String)",
      ) }

    val javaInterface2ClassFullName = "org.mvv.scala.tools.beans.testclasses.JavaInterface2"
    a.assertThat(r.asJava).containsKey(javaInterface2ClassFullName)
    r.get(javaInterface2ClassFullName).foreach { _class =>
      a.assertThat(_class.methods.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
        "getInterfaceValue2(): java.lang.String",
        "methodInterface2(): java.lang.String",
      ) }

    val baseJavaClass1ClassFullName = "org.mvv.scala.tools.beans.testclasses.BaseJavaClass1"
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

    val inheritedClass2FullName = "org.mvv.scala.tools.beans.testclasses.InheritedFromJavaClass2"
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
