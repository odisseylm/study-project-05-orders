package com.mvv.scala.temp.tests.tasty

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test

class VisibilityTest {
  private val classesDir = "/home/vmelnykov/projects/study-project-05-orders/hello-world-samples/scala01/target/classes"


  @Test
  def testScala(): Unit = {
    import scala.language.unsafeNulls
    import scala.jdk.CollectionConverters.*

    // TODO: use class for test instead of path
    val tastyFile = s"$classesDir/com/mvv/scala/temp/tests/tasty/AccessVisibilityTestClass.tasty"
    //val tastyClassFullName = "com.mvv.scala.temp.tests.tasty.AccessVisibilityTestClass"

    val inspector = ScalaBeansInspector()
    val _class: _Class = inspector.inspectTastyFile(tastyFile).head

    val a = SoftAssertions()

    a.assertThat(_class.fields("privateVal").visibility).isEqualTo(_Visibility.Private)
    a.assertThat(_class.fields("protectedVal").visibility).isEqualTo(_Visibility.Protected)
    a.assertThat(_class.fields("publicVal").visibility).isEqualTo(_Visibility.Public)

    a.assertThat(_class.methods(_MethodKey("privateMethod", List[_Type](), false)).visibility).isEqualTo(_Visibility.Private)
    a.assertThat(_class.methods(_MethodKey("protectedMethod", List[_Type](), false)).visibility).isEqualTo(_Visibility.Protected)
    a.assertThat(_class.methods(_MethodKey("publicMethod", List[_Type](), false)).visibility).isEqualTo(_Visibility.Public)

    a.assertAll()
  }


  @Test
  def testJavaClass(): Unit = {
    import scala.language.unsafeNulls
    import scala.jdk.CollectionConverters.*

    // TODO: use class for test instead of path
    val tastyFile = s"$classesDir/com/mvv/scala/temp/tests/tasty/InheritedFromJavaClass2.tasty"
    //val tastyClassFullName = "com.mvv.scala.temp.tests.tasty.InheritedFromJavaClass2"

    val inspector = ScalaBeansInspector()
    val _class: _Class = inspector.inspectTastyFile(tastyFile).head

    val a = SoftAssertions()

    // fields from BaseJavaClass1
    a.assertThat(_class.fields("privateField1").visibility).isEqualTo(_Visibility.Private)
    a.assertThat(_class.fields("packageField1").visibility).isEqualTo(_Visibility.Package)
    a.assertThat(_class.fields("protectedField1").visibility).isEqualTo(_Visibility.Protected)
    a.assertThat(_class.fields("publicField1").visibility).isEqualTo(_Visibility.Public)

    // methods from BaseJavaClass1
    a.assertThat(_class.methods(_MethodKey("privateMethod1", List[_Type](), false)).visibility).isEqualTo(_Visibility.Private)
    a.assertThat(_class.methods(_MethodKey("packageMethod1", List[_Type](), false)).visibility).isEqualTo(_Visibility.Package)
    a.assertThat(_class.methods(_MethodKey("protectedMethod1", List[_Type](), false)).visibility).isEqualTo(_Visibility.Protected)
    a.assertThat(_class.methods(_MethodKey("publicMethod1", List[_Type](), false)).visibility).isEqualTo(_Visibility.Public)

    a.assertAll()
  }

}
