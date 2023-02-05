package com.mvv.scala.temp.tests.tasty

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
//
import com.mvv.scala.temp.tests.tasty._FieldKey as fk
import com.mvv.scala.temp.tests.tasty._MethodKey as mk

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

    a.assertThat(_class.fields(fk("privateVal")).visibility).isEqualTo(_Visibility.Private)
    a.assertThat(_class.fields(fk("protectedVal")).visibility).isEqualTo(_Visibility.Protected)
    a.assertThat(_class.fields(fk("publicVal")).visibility).isEqualTo(_Visibility.Public)

    a.assertThat(_class.methods(mk("privateMethod", List[_Type](), false)).visibility).isEqualTo(_Visibility.Private)
    a.assertThat(_class.methods(mk("protectedMethod", List[_Type](), false)).visibility).isEqualTo(_Visibility.Protected)
    a.assertThat(_class.methods(mk("publicMethod", List[_Type](), false)).visibility).isEqualTo(_Visibility.Public)

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
    a.assertThat(_class.fields(fk("privateField1")).visibility).isEqualTo(_Visibility.Private)
    a.assertThat(_class.fields(fk("packageField1")).visibility).isEqualTo(_Visibility.Package)
    a.assertThat(_class.fields(fk("protectedField1")).visibility).isEqualTo(_Visibility.Protected)
    a.assertThat(_class.fields(fk("publicField1")).visibility).isEqualTo(_Visibility.Public)

    // methods from BaseJavaClass1
    a.assertThat(_class.methods(mk("privateMethod1", List[_Type](), false)).visibility).isEqualTo(_Visibility.Private)
    a.assertThat(_class.methods(mk("packageMethod1", List[_Type](), false)).visibility).isEqualTo(_Visibility.Package)
    a.assertThat(_class.methods(mk("protectedMethod1", List[_Type](), false)).visibility).isEqualTo(_Visibility.Protected)
    a.assertThat(_class.methods(mk("publicMethod1", List[_Type](), false)).visibility).isEqualTo(_Visibility.Public)

    a.assertAll()
  }

}
