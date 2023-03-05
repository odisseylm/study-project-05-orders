package org.mvv.scala.tools.inspection.light

import scala.language.unsafeNulls
import scala.jdk.CollectionConverters.*
import scala.compiletime.uninitialized
import java.nio.file.Path
//
import org.junit.jupiter.api.{ Test, Disabled }
import org.assertj.core.api.Assertions.assertThat
//
import org.mvv.scala.tools.inspection.{ _Class, _MethodKey, _Modifier, _Type }
import org.mvv.scala.tools.beans.testclasses.ClassSampleInProductionSources



class ClassInTestSource1 :
  var var1: String = uninitialized

class InspectingByClassOrClassNameTest {

  class LocalClass1:
    var var1: String = uninitialized


  @Test
  def inspectByClass_forClassInProductionSources(): Unit =
    import scala.language.unsafeNulls
    val _class: _Class = ScalaBeanInspector()
      .inspectClass(classOf[ClassSampleInProductionSources])
    assertThat(_class).isNotNull
    assertThat(_class.fields.asJava).isNotEmpty


  @Test
  def inspectByClassName_forClassInProductionSources(): Unit =
    import scala.language.unsafeNulls
    val _class: _Class = ScalaBeanInspector()
      .inspectClass(classOf[ClassSampleInProductionSources].getName)
    assertThat(_class).isNotNull
    assertThat(_class.fields.asJava).isNotEmpty


  @Test
  def inspectByClass_forClassInTestSource1(): Unit =
    import scala.language.unsafeNulls
    val _class: _Class = ScalaBeanInspector()
      .inspectClass(classOf[ClassInTestSource1])
    assertThat(_class).isNotNull
    assertThat(_class.fields.asJava).isNotEmpty


  @Test
  def inspectByClassName_forClassInTestSource1(): Unit =
    import scala.language.unsafeNulls
    val _class: _Class = ScalaBeanInspector()
      .inspectClass(classOf[ClassInTestSource1].getName)
    assertThat(_class).isNotNull
    assertThat(_class.fields.asJava).isNotEmpty


  // Light scala bean inspector does not support local classes
  // java.lang.ClassCastException: class dotty.tools.dotc.core.Types$NoType$ cannot be cast to class dotty.tools.dotc.core.Types$ClassInfo
  @Test
  @Disabled
  def inspectByClass_forLocalClass1(): Unit =
    import scala.language.unsafeNulls
    val _class: _Class = ScalaBeanInspector()
      .inspectClass(classOf[LocalClass1])
    assertThat(_class).isNotNull
    assertThat(_class.fields.asJava).isNotEmpty


  // Light scala bean inspector does not support local classes
  // java.lang.ClassCastException: class dotty.tools.dotc.core.Types$NoType$ cannot be cast to class dotty.tools.dotc.core.Types$ClassInfo
  @Test
  @Disabled
  def inspectByClassName_forLocalClass1(): Unit =
    import scala.language.unsafeNulls
    val _class: _Class = ScalaBeanInspector()
      .inspectClass(classOf[LocalClass1].getName)
    assertThat(_class).isNotNull
    assertThat(_class.fields.asJava).isNotEmpty


  @Test
  def inspectByClass_loadingFromJar(): Unit =
    import scala.language.unsafeNulls
    val _class: _Class = ScalaBeanInspector()
      .inspectClass(classOf[com.mvv.scala3.samples.InheritedFromJavaClass2])
    assertThat(_class).isNotNull
    assertThat(_class.fields.asJava).isNotEmpty


  @Test
  def inspectByClassName_loadingFromJar(): Unit =
    import scala.language.unsafeNulls

    val _class: _Class = ScalaBeanInspector()
      .inspectClass(classOf[com.mvv.scala3.samples.InheritedFromJavaClass2].getName)

    assertThat(_class).isNotNull
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
