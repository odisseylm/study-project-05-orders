package org.mvv.scala.tools.beans

import scala.tasty.inspector.{ Tasty, TastyInspector }
import scala.collection.immutable.Map
import scala.jdk.CollectionConverters.*
//
import org.junit.jupiter.api.{ Test, Disabled }
import org.assertj.core.api.SoftAssertions
//
import testclasses.StandardScalaModifiersTesClass
import org.mvv.scala.tools.inspection.{ _MethodKey, _Modifier, _Type }
import org.mvv.scala.tools.inspection.tasty.{ _Class, ScalaBeansInspector }


class ModifierTest {

  @Test
  def testStandardFieldAccessors(): Unit = {
    import scala.language.unsafeNulls
    import scala.jdk.CollectionConverters.*

    val inspector = ScalaBeansInspector()
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
  @Disabled("for manual testing since it depends on local file path")
  def testInheritingJavaPropAccessorModifierAfterOverriding(): Unit = {
    import scala.language.unsafeNulls
    import scala.jdk.CollectionConverters.*

    val userHome = System.getProperty("user.home")
    val classesDir = s"$userHome/projects/study-project-05-orders/scala-map-struct-support/target/test-classes"
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
