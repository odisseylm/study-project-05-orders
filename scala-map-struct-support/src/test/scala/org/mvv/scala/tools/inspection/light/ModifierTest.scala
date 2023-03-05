package org.mvv.scala.tools.inspection.light


import scala.collection.immutable.Map
import scala.jdk.CollectionConverters.*
import scala.tasty.inspector.{Tasty, TastyInspector}
//
import org.junit.jupiter.api.{ Test, Disabled }
import org.assertj.core.api.SoftAssertions
//
import org.mvv.scala.testClassesDir
import org.mvv.scala.tools.quotes.classNameOf
import org.mvv.scala.tools.inspection.{ _Class, _MethodKey, _Modifier, _Type, getValOrField, getMethod }
import org.mvv.scala.tools.beans.testclasses.{ StandardScalaModifiersTesClass, InheritedFromJavaClass2 }



class ModifierTest {

  @Test
  def testStandardFieldAccessors(): Unit = {
    import scala.language.unsafeNulls
    import scala.jdk.CollectionConverters.*

    val inspector = ScalaBeanInspector()
    given _class: _Class = inspector.inspectClass(classOf[StandardScalaModifiersTesClass])

    val a = SoftAssertions()

    a.assertThat(_class.getValOrField("privateValField1").modifiers.asJava).isEmpty()
    a.assertThat(_class.getValOrField("protectedValField1").modifiers.asJava).isEmpty()
    a.assertThat(_class.getValOrField("publicValField1").modifiers.asJava).isEmpty()

    a.assertThat(_class.getValOrField("privateVarField1").modifiers.asJava).isEmpty()
    a.assertThat(_class.getValOrField("protectedVarField1").modifiers.asJava).isEmpty()
    a.assertThat(_class.getValOrField("publicVarField1").modifiers.asJava).isEmpty()

    a.assertThat(_class.getMethod("privateValMethod1").modifiers.asJava).contains(_Modifier.ScalaCustomFieldAccessor)
    a.assertThat(_class.getMethod("protectedValMethod1").modifiers.asJava).contains(_Modifier.ScalaCustomFieldAccessor)
    a.assertThat(_class.getMethod("publicValMethod1").modifiers.asJava).contains(_Modifier.ScalaCustomFieldAccessor)

    a.assertThat(_class.getMethod("privateMethod1").modifiers.asJava).isEmpty()
    a.assertThat(_class.getMethod("protectedMethod1").modifiers.asJava).isEmpty()
    a.assertThat(_class.getMethod("publicMethod1").modifiers.asJava).isEmpty()

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
  def testInheritingJavaPropAccessorModifierAfterOverriding(): Unit = {
    import scala.jdk.CollectionConverters.*
    import scala.language.unsafeNulls

    val _class = ScalaBeanInspector().inspectClass(classNameOf[InheritedFromJavaClass2])

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
