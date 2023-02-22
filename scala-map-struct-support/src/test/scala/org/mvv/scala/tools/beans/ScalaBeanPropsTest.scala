package org.mvv.scala.tools.beans

import scala.language.unsafeNulls
import scala.jdk.CollectionConverters.*
import java.beans.PropertyDescriptor
//
import org.junit.jupiter.api.Test
import org.assertj.core.api.SoftAssertions
//
import org.mvv.scala.tools.nnArray
import testclasses.InheritedFromJavaClass2


class ScalaBeanPropsTest {

  @Test
  def createScalaBeanProperties_usingTastyOnly(): Unit = {
    val inspector = ScalaBeansInspector()
    val _class: _Class = inspector.inspectClass(classOf[InheritedFromJavaClass2])
    val beanProps = _class.beanProperties

    val p: BeanProperty = beanProps.beanProps("trait1Var")
    val a = SoftAssertions()

    a.assertThat(p.name).isEqualTo("trait1Var")
    a.assertThat(p.propertyType).isEqualTo(Types.StringType)
    a.assertThat(p.ownerClass).isEqualTo(_class)
    a.assertThat(p.ownerClass).isEqualTo(_class)

    a.assertThat(p.runtimePropertyType).isEqualTo(None)
    a.assertThat(p.runtimeOwnerClass).isEqualTo(None)
    a.assertThat(p.runtimeField).isEqualTo(None)
    a.assertThat(p.runtimeGetMethods).isEqualTo(None)
    a.assertThat(p.runtimeSetMethods).isEqualTo(None)

    a.assertAll()
  }

  @Test
  def createScalaBeanProperties_usingJavaReflectionToo(): Unit = {
    val inspector = ScalaBeansInspector()
    val _class: _Class = inspector.inspectClass(classOf[InheritedFromJavaClass2])
    val beanProps = _class.beanProperties(true)

    val p: BeanProperty = beanProps.beanProps("trait1Var")
    val a = SoftAssertions()

    a.assertThat(p.name).isEqualTo("trait1Var")
    a.assertThat(p.propertyType).isEqualTo(Types.StringType)
    a.assertThat(p.ownerClass).isEqualTo(_class)
    a.assertThat(p.ownerClass).isEqualTo(_class)
    a.assertThat(p.ownerClass.simpleName).isEqualTo("InheritedFromJavaClass2")

    a.assertThat(p.runtimeOwnerClass).isEqualTo(Option(classOf[InheritedFromJavaClass2]))
    a.assertThat(p.runtimePropertyType).isEqualTo(Option(classOf[String]))
    a.assertThat(p.runtimeGetMethods.get.asJava).hasSize(1)
    a.assertThat(p.runtimeSetMethods.get.asJava).hasSize(1)

    a.assertAll()
  }

  @Test
  def createScalaBeanPropertyDescriptors(): Unit = {

    val beanProps = ScalaBeanProps(classOf[InheritedFromJavaClass2])
    val pdMap: Map[String, PropertyDescriptor] = beanProps.getPropertyDescriptors.nnArray
      .map(pd => (pd.getName.nn, pd) )
      .toMap

    val a = SoftAssertions()
    import scala.language.unsafeNulls

    a.assertThat(pdMap.keys.asJava).containsExactlyInAnyOrder(
      "publicValField1", "publicValMethod2",
      //
      "interfaceValue11",
      //
      "publicProp1",
      //
      "interfaceValue1", "interfaceValue2",
      //
      "trait1Val", "trait1Var", "trait1ValMethod",
      "trait2Val", "trait2Var", "trait2ValMethod",
    )
    a.assertThat(pdMap.keys.asJava).doesNotContain(
      "publicMethod2",
      "javaInterfaceValue11Var",
      // it is plain field, now it is not considered as property because does not have access methods
      "publicField1",
      //
      "privateMethod1", "packageMethod1", "protectedMethod1", "publicMethod1",
      "privateProp1", "packageProp1", "protectedProp1",
      //
      "methodInterface1", "methodInterface2",
    )

    a.assertAll()
  }

}