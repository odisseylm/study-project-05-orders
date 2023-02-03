package com.mvv.scala.temp.tests.tasty

import scala.tasty.inspector.Tasty
import scala.tasty.inspector.TastyInspector
import scala.collection.immutable.Map
import scala.jdk.CollectionConverters.*
//
import org.junit.jupiter.api.Test
import org.assertj.core.api.SoftAssertions


class ModifierTest {
  private val classesDir = "/home/vmelnykov/projects/study-project-05-orders/hello-world-samples/scala01/target/classes"


  @Test
  def testStandardFieldAccessors(): Unit = {
    import scala.language.unsafeNulls
    import scala.jdk.CollectionConverters.*

    val tastyFile = s"$classesDir/com/mvv/scala/temp/tests/tasty/StandardScalaModifiersTesClass.tasty"
    val tastyClassFullName = "com.mvv.scala.temp.tests.tasty.StandardScalaModifiersTesClass"

    val inspector = ScalaBeansInspector()
    given _class: _Class = inspector.inspectTastyFile(tastyFile).head

    val a = SoftAssertions()

    a.assertThat(getValOrField("privateValField1").modifiers.asJava).isEmpty()
    a.assertThat(getValOrField("protectedValField1").modifiers.asJava).isEmpty()
    a.assertThat(getValOrField("publicValField1").modifiers.asJava).isEmpty()

    a.assertThat(getValOrField("privateVarField1").modifiers.asJava).isEmpty()
    a.assertThat(getValOrField("protectedVarField1").modifiers.asJava).isEmpty()
    a.assertThat(getValOrField("publicVarField1").modifiers.asJava).isEmpty()

    a.assertThat(getMethod("privateValMethod1").modifiers.asJava).contains(_Modifier.CustomFieldAccessor)
    a.assertThat(getMethod("protectedValMethod1").modifiers.asJava).contains(_Modifier.CustomFieldAccessor)
    a.assertThat(getMethod("publicValMethod1").modifiers.asJava).contains(_Modifier.CustomFieldAccessor)

    a.assertThat(getMethod("privateMethod1").modifiers.asJava).isEmpty()
    a.assertThat(getMethod("protectedMethod1").modifiers.asJava).isEmpty()
    a.assertThat(getMethod("publicMethod1").modifiers.asJava).isEmpty()

    //a.assertThat(getMethod("getInterfaceValue11").modifiers.asJava)
    //  .contains(_Modifier.CustomFieldAccessor)
    //a.assertThat(getMethod("setInterfaceValue11").modifiers.asJava)
    //  .doesNotContain(_Modifier.CustomFieldAccessor)

    a.assertAll()
  }

  @Test
  def testInheritingJavaPropAccessorModifierAfterOverriding(): Unit = {
    import scala.language.unsafeNulls
    import scala.jdk.CollectionConverters.*

    val tastyFile = s"$classesDir/com/mvv/scala/temp/tests/tasty/InheritedFromJavaClass2.tasty"
    val tastyClassFullName = "com.mvv.scala.temp.tests.tasty.InheritedFromJavaClass2"
    val _class = ScalaBeansInspector().inspectTastyFile(tastyFile).head

    val overriddenJavaGetMethod = _class.methods(_MethodKey("getInterfaceValue11", Nil, false))
    val overriddenJavaSetMethod = _class.methods(_MethodKey("setInterfaceValue11", List(_Type("java.lang.String")), false))

    val a = SoftAssertions()

    a.assertThat(overriddenJavaGetMethod.modifiers.asJava)
      .contains(_Modifier.JavaPropertyAccessor, _Modifier.CustomFieldAccessor)
    a.assertThat(overriddenJavaSetMethod.modifiers.asJava)
      .contains(_Modifier.JavaPropertyAccessor)
      .doesNotContain(_Modifier.CustomFieldAccessor)

    a.assertAll()
  }

}
