package org.mvv.mapstruct.scala

import org.mvv.mapstruct.scala.testclasses.ClassSampleInProductionSources
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

import scala.compiletime.uninitialized
import scala.jdk.CollectionConverters.*


class ClassInTestSource1 :
  var var1: String = uninitialized

class InspectingByClassOrClassNameTest {

  class LocalClass1:
    var var1: String = uninitialized


  @Test
  def inspectByClass_forClassInProductionSources(): Unit =
    import scala.language.unsafeNulls
    val _class: _Class = ScalaBeansInspector()
      .inspectClass(classOf[ClassSampleInProductionSources])
    assertThat(_class).isNotNull
    assertThat(_class.fields.asJava).isNotEmpty


  @Test
  def inspectByClassName_forClassInProductionSources(): Unit =
    import scala.language.unsafeNulls
    val _class: _Class = ScalaBeansInspector()
      .inspectClass(classOf[ClassSampleInProductionSources].getName)
    assertThat(_class).isNotNull
    assertThat(_class.fields.asJava).isNotEmpty


  @Test
  def inspectByClass_forClassInTestSource1(): Unit =
    import scala.language.unsafeNulls
    val _class: _Class = ScalaBeansInspector()
      .inspectClass(classOf[ClassInTestSource1])
    assertThat(_class).isNotNull
    assertThat(_class.fields.asJava).isNotEmpty


  @Test
  def inspectByClassName_forClassInTestSource1(): Unit =
    import scala.language.unsafeNulls
    val _class: _Class = ScalaBeansInspector()
      .inspectClass(classOf[ClassInTestSource1].getName)
    assertThat(_class).isNotNull
    assertThat(_class.fields.asJava).isNotEmpty


  @Test
  def inspectByClass_forLocalClass1(): Unit =
    import scala.language.unsafeNulls
    val _class: _Class = ScalaBeansInspector()
      .inspectClass(classOf[LocalClass1])
    assertThat(_class).isNotNull
    assertThat(_class.fields.asJava).isNotEmpty


  @Test
  def inspectByClassName_forLocalClass1(): Unit =
    import scala.language.unsafeNulls
    val _class: _Class = ScalaBeansInspector()
      .inspectClass(classOf[LocalClass1].getName)
    assertThat(_class).isNotNull
    assertThat(_class.fields.asJava).isNotEmpty


  @Test
  def inspectByClass_loadingFromJar(): Unit =
    import scala.language.unsafeNulls
    val _class: _Class = ScalaBeansInspector()
      .inspectClass(classOf[com.mvv.scala3.samples.InheritedFromJavaClass2])
    assertThat(_class).isNotNull
    assertThat(_class.classSource).isInstanceOf(classOf[JarClassSource])
    assertThat(_class.fields.asJava).isNotEmpty


  @Test
  def inspectByClassName_loadingFromJar(): Unit =
    import scala.language.unsafeNulls
    val _class: _Class = ScalaBeansInspector()
      .inspectClass(classOf[com.mvv.scala3.samples.InheritedFromJavaClass2].getName)

    assertThat(_class).isNotNull
    // it does not make to verify this class if it loaded from file
    assertThat(_class.classSource).isInstanceOf(classOf[JarClassSource])

    assertThat(_class.fields.asJava).isNotEmpty

    assertThat(_class.fields.keys.map(_.toString).asJava)
      .contains("publicVarField1: java.lang.String")
    assertThat(_class.methods.keys.map(_.toString).asJava)
      .contains("publicVarField1_=(java.lang.String)")

    val scalaSetterMethod = _class.methods(_MethodKey("publicVarField1_=", List(_Type("java.lang.String")), false))
    assertThat(scalaSetterMethod.modifiers.asJava).contains(_Modifier.ScalaStandardFieldAccessor)

    val scalaGetterMethod = _class.methods(_MethodKey("privateValMethod2", Nil, false))
    assertThat(scalaGetterMethod.modifiers.asJava).contains(_Modifier.ScalaCustomFieldAccessor)

    // the same using _MethodKey.getter
    assertThat(_class.methods(_MethodKey.getter("privateValMethod2")).modifiers.asJava)
      .contains(_Modifier.ScalaCustomFieldAccessor)

}
