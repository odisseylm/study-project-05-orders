package org.mvv.scala.tools.inspection.tasty

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
//
import org.mvv.scala.tools.inspection.tasty.TastyScalaBeansInspector as ScalaBeansInspector
import org.mvv.scala.tools.inspection.{_Class, _Type, _Visibility, _FieldKey as fk, _MethodKey as mk}
import org.mvv.scala.tools.beans.testclasses.{AccessVisibilityTestClass, InheritedFromJavaClass2}


class VisibilityTest {

  @Test
  def testScala(): Unit = {
    import scala.jdk.CollectionConverters.*
    import scala.language.unsafeNulls

    val inspector = ScalaBeansInspector()
    val _class: _Class = inspector.inspectClass(classOf[AccessVisibilityTestClass])

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
    import scala.jdk.CollectionConverters.*
    import scala.language.unsafeNulls

    val inspector = ScalaBeansInspector()
    val _class: _Class = inspector.inspectClass(classOf[InheritedFromJavaClass2])

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
