package org.mvv.mapstruct.scala

import org.junit.jupiter.api.Disabled
import org.mvv.mapstruct.scala.testclasses.StandardScalaModifiersTesClass

import scala.tasty.inspector.Tasty
import scala.tasty.inspector.TastyInspector
import scala.collection.immutable.Map
import scala.jdk.CollectionConverters.*
//
import org.junit.jupiter.api.Test
import org.assertj.core.api.SoftAssertions


class ModifierTest {

  @Test
  def testStandardFieldAccessors(): Unit = {
    import scala.language.unsafeNulls
    import scala.jdk.CollectionConverters.*

    val inspector = ScalaBeansInspector()
    given _class: _Class = inspector.inspectClass(classOf[StandardScalaModifiersTesClass])

    val a = SoftAssertions()

    a.assertThat(getValOrField("privateValField1").modifiers.asJava).isEmpty()
    a.assertThat(getValOrField("protectedValField1").modifiers.asJava).isEmpty()
    a.assertThat(getValOrField("publicValField1").modifiers.asJava).isEmpty()

    a.assertThat(getValOrField("privateVarField1").modifiers.asJava).isEmpty()
    a.assertThat(getValOrField("protectedVarField1").modifiers.asJava).isEmpty()
    a.assertThat(getValOrField("publicVarField1").modifiers.asJava).isEmpty()

    a.assertThat(getMethod("privateValMethod1").modifiers.asJava).contains(_Modifier.ScalaCustomFieldAccessor)
    a.assertThat(getMethod("protectedValMethod1").modifiers.asJava).contains(_Modifier.ScalaCustomFieldAccessor)
    a.assertThat(getMethod("publicValMethod1").modifiers.asJava).contains(_Modifier.ScalaCustomFieldAccessor)

    a.assertThat(getMethod("privateMethod1").modifiers.asJava).isEmpty()
    a.assertThat(getMethod("protectedMethod1").modifiers.asJava).isEmpty()
    a.assertThat(getMethod("publicMethod1").modifiers.asJava).isEmpty()

    //a.assertThat(getMethod("getInterfaceValue11").modifiers.asJava)
    //  .contains(_Modifier.ScalaCustomFieldAccessor)
    //a.assertThat(getMethod("setInterfaceValue11").modifiers.asJava)
    //  .doesNotContain(_Modifier.ScalaCustomFieldAccessor)

    //a.assertThat(_class.methods(_MethodKey("privateVarField1_=", List(_Type.StringType), false)).modifiers.asJava)
    //  .contains(_Modifier.ScalaStandardFieldAccessor)

    //a.assertThat(_class.methods(_MethodKey("protectedVarField1", Nil, false)).modifiers.asJava)
    //  .contains(_Modifier.ScalaStandardFieldAccessor)
    a.assertThat(_class.methods(_MethodKey("protectedVarField1_=", List(_Type.StringType), false)).modifiers.asJava)
      .contains(_Modifier.ScalaStandardFieldAccessor)

    //a.assertThat(_class.methods(_MethodKey("publicVarField1", Nil, false)).modifiers.asJava)
    //  .contains(_Modifier.ScalaStandardFieldAccessor)
    a.assertThat(_class.methods(_MethodKey("publicVarField1_=", List(_Type.StringType), false)).modifiers.asJava)
      .contains(_Modifier.ScalaStandardFieldAccessor)

    a.assertAll()
  }

  @Test
  @Disabled
  def testInheritingJavaPropAccessorModifierAfterOverriding(): Unit = {
    import scala.language.unsafeNulls
    import scala.jdk.CollectionConverters.*

    val classesDir = "/home/vmelnykov/projects/study-project-05-orders/scala-map-struct-support/target/test-classes"
    val tastyFile = s"$classesDir/org/mvv/mapstruct/scala/testclasses/InheritedFromJavaClass2.tasty"
    //val tastyClassFullName = "com.mvv.scala.temp.tests.tasty.testclasses.InheritedFromJavaClass2"

    val _class = ScalaBeansInspector().inspectTastyFile(tastyFile).head

    val overriddenJavaGetMethod = _class.methods(_MethodKey("getInterfaceValue11", Nil, false))
    val overriddenJavaSetMethod = _class.methods(_MethodKey("setInterfaceValue11", List(_Type("java.lang.String")), false))

    val a = SoftAssertions()

    a.assertThat(overriddenJavaGetMethod.modifiers.asJava)
      .contains(_Modifier.JavaPropertyAccessor, _Modifier.ScalaCustomFieldAccessor)
    a.assertThat(overriddenJavaSetMethod.modifiers.asJava)
      .contains(_Modifier.JavaPropertyAccessor)
      .doesNotContain(_Modifier.ScalaCustomFieldAccessor)

    a.assertAll()
  }

}
