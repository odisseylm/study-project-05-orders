package org.mvv.scala.tools.inspection.light

import scala.collection.immutable.Map
import scala.jdk.CollectionConverters.*
import scala.tasty.inspector.{ Tasty, TastyInspector }
//
import org.junit.jupiter.api.{ Test, Disabled }
import org.assertj.core.api.SoftAssertions
//
import org.mvv.scala.tools.inspection._Class
import org.mvv.scala.tools.beans.testclasses.{ InheritedClass1, InheritedFromJavaClass1 }
import org.mvv.scala.tools.inspection.light.LightScalaBeanInspector as ScalaBeanInspector



class TastyTest :


  @Test
  def inheritanceTest(): Unit = {
    import scala.jdk.CollectionConverters.*
    import scala.language.unsafeNulls

    val inspector = ScalaBeanInspector()
    inspector.inspectClass(classOf[InheritedClass1])

    val a = SoftAssertions()

    val r: Map[String, _Class] = inspector.classesDescr
    a.assertThat(r).isNotNull()
    a.assertThat(r.asJava)
      //.hasSize(4)
      .containsOnlyKeys(
        //"org.mvv.scala.tools.beans.testclasses.Trait1",
        //"org.mvv.scala.tools.beans.testclasses.Trait2",
        //"org.mvv.scala.tools.beans.testclasses.BaseClass1",
        "org.mvv.scala.tools.beans.testclasses.InheritedClass1",
      )

    val _class = r("org.mvv.scala.tools.beans.testclasses.InheritedClass1")

    a.assertThat(_class.fields.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
      "trait1Val: java.lang.String", "trait2Val: java.lang.String",
      "trait1Var: java.lang.String", "trait2Var: java.lang.String",
      // seems with scala reflect private fields are not visible??
      //"privateValField0: java.lang.String",
      "protectedValField0: java.lang.String",
      "publicValField0: java.lang.String",
      // seems with scala reflect private fields are not visible??
      //"privateVarField0: java.lang.String",
      "protectedVarField0: java.lang.String", "publicVarField0: java.lang.String",
      "privateValField1: java.lang.String", "protectedValField1: java.lang.String", "publicValField1: java.lang.String",
      "privateVarField1: java.lang.String", "protectedVarField1: java.lang.String", "publicVarField1: java.lang.String",
      "privateValField2: java.lang.String", "protectedValField2: java.lang.String", "publicValField2: java.lang.String",
      "privateVarField2: java.lang.String", "protectedVarField2: java.lang.String", "publicVarField2: java.lang.String",
    )

    a.assertThat(_class.methods.keys.map(_.toString).asJava).contains( //.containsExactlyInAnyOrder(
      "trait1ValMethod: java.lang.String",
      "trait1Method(): java.lang.String",
      "trait2ValMethod: java.lang.String",
      "trait2Method(): java.lang.String",
      //
      // seems with scala reflect private fields are not visible??
      //"privateValMethod0: java.lang.String",
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
    import scala.jdk.CollectionConverters.*
    import scala.language.unsafeNulls

    val inspector = ScalaBeanInspector()
    inspector.inspectClass(classOf[InheritedFromJavaClass1])

    val a = SoftAssertions()

    val r: Map[String, _Class] = inspector.classesDescr
    a.assertThat(r).isNotNull()
    a.assertThat(r.asJava)
      //.hasSize(4)
      .containsOnlyKeys(
        //"org.mvv.scala.tools.beans.testclasses.Trait1",
        //"org.mvv.scala.tools.beans.testclasses.Trait2",
        //"org.mvv.scala.tools.beans.testclasses.JavaInterface1",
        //"org.mvv.scala.tools.beans.testclasses.JavaInterface2",
        //"org.mvv.scala.tools.beans.testclasses.BaseJavaClass1",
        //"org.mvv.scala.tools.beans.testclasses.BaseJavaClass2",
        "org.mvv.scala.tools.beans.testclasses.InheritedFromJavaClass1",
      )

    val _class = r("org.mvv.scala.tools.beans.testclasses.InheritedFromJavaClass1")

    a.assertThat(_class.fields.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
      "trait1Val: java.lang.String", "trait2Val: java.lang.String",
      "trait1Var: java.lang.String", "trait2Var: java.lang.String",
      // BaseJavaClass1
      // seems with scala reflect private fields are not visible??
      //"privateField1: java.lang.String",
      "packageField1: java.lang.String",
      "protectedField1: java.lang.String", "publicField1: java.lang.String",
      // BaseJavaClass2
      //  no fields
      // InheritedFromJavaClass1
      "privateValField1: java.lang.String",
      "protectedValField1: java.lang.String", "publicValField1: java.lang.String",
      "publicVarField1: java.lang.String",
      // Inherited from BaseJavaClass2
      // seems with scala reflect private fields are not visible??
      //"interfaceValue11: java.lang.String",
    )

    a.assertThat(_class.methods.keys.map(_.toString).asJava).contains( //.containsExactlyInAnyOrder(
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
      // seems with scala reflect private fields are not visible??
      //"privateMethod1(): java.lang.String",
      "packageMethod1(): java.lang.String",
      "protectedMethod1(): java.lang.String",
      "publicMethod1(): java.lang.String",
      //
      // seems with scala reflect private fields are not visible??
      //"getPrivateProp1(): java.lang.String",
      //"setPrivateProp1(java.lang.String)",
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
      // seems with scala reflect private fields are not visible??
      //"privateValMethod2: java.lang.String",
      //"privateMethod2(): java.lang.String",
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
