package org.mvv.mapstruct.scala

import org.mvv.mapstruct.scala.testclasses.ClassSampleInProductionSources
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

import java.nio.file.Path
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
  def inspectByClass_smokeForAllClassesInBigJar(): Unit =
    import scala.language.unsafeNulls

    val rootProjectDir = "/home/vmelnykov/projects/study-project-05-orders/"

    ScalaBeansInspector()
      .inspectJar(Path.of(s"$rootProjectDir/hello-world-samples/scala01/target/scala01-1.0-SNAPSHOT.jar"))

    // to have it successful we need to add child dependencies to classpath
    //ScalaBeansInspector()
    //  .inspectJar(Path.of(s"$rootProjectDir/hello-world-samples/scala01/target/scala01-1.0-SNAPSHOT-tests.jar"))

    ScalaBeansInspector()
      .inspectJar(Path.of(s"$rootProjectDir/hello-world-samples/scala2-samples/target/scala2-samples-1.0-SNAPSHOT.jar"))

    ScalaBeansInspector()
      .inspectJar(Path.of(s"$rootProjectDir/hello-world-samples/scala3-samples/target/scala3-samples-1.0-SNAPSHOT.jar"))

    ScalaBeansInspector()
      .inspectJar(Path.of(s"$rootProjectDir/kotlin-orders/kotlin-orders-dto/target/kotlin-bank-orders-dto.jar"))


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
