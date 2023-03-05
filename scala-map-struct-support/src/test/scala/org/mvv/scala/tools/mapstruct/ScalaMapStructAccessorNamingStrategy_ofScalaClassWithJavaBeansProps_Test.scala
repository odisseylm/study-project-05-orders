package org.mvv.scala.tools.mapstruct

import scala.language.unsafeNulls
import scala.compiletime.uninitialized
import scala.annotation.meta.beanGetter
import scala.beans.BeanProperty
//
import java.beans.JavaBean
import javax.lang.model.element.VariableElement
import javax.lang.model.util.{ Elements, Types }
//
import java.util.Collections
import java.util.Collections.{ emptyList, singletonList }
//
import javax.lang.model.`type`.{ TypeKind, TypeMirror }
import javax.lang.model.element.{ Element, TypeElement, ExecutableElement }
//
import org.junit.jupiter.api.{ Test, BeforeEach }
import org.assertj.core.api.Assertions.assertThat
import org.mockito.Mockito.{ mock, when }
import org.mapstruct.ap.spi.MapStructProcessingEnvironment



class TestScalaRestOrder2WithJavaBeanProps :
  @BeanProperty
  var amount: BigDecimal = uninitialized
  @BeanProperty
  var cur1: String = uninitialized
  @BeanProperty
  var cur2: String = uninitialized
  @BeanProperty
  var buySellType: TestRestBuySellType1 = uninitialized



//noinspection AccessorLikeMethodIsUnit
class ScalaMapStructAccessorNamingStrategy_ofScalaClassWithJavaBeansProps_Test {
  private val accessorNamingStrategy = ScalaMapStructAccessorNamingStrategy()

  @BeforeEach
  def prepareTests(): Unit = {
    val env = mock(classOf[MapStructProcessingEnvironment])
    when(env.getTypeUtils).thenReturn(mock(classOf[Types]))
    when(env.getElementUtils).thenReturn(mock(classOf[Elements]))

    accessorNamingStrategy.init(env)
  }

  @Test
  def getPropertyName(): Unit = {
    assertThat(accessorNamingStrategy.getPropertyName(
      mock_TestScalaRestOrder2WithJavaBeanProps_amountGetter("amount")))
      .isEqualTo("amount")

    assertThat(accessorNamingStrategy.getPropertyName(
      mock_TestScalaRestOrder2WithJavaBeanProps_amountGetter("getAmount")))
      .isEqualTo("amount")

    assertThat(accessorNamingStrategy.getPropertyName(
      mock_TestScalaRestOrder2WithJavaBeanProps_amountSetter("amount_=")))
      .isEqualTo("amount")

    assertThat(accessorNamingStrategy.getPropertyName(
      mock_TestScalaRestOrder2WithJavaBeanProps_amountSetter("amount_$eq")))
      .isEqualTo("amount")

    assertThat(accessorNamingStrategy.getPropertyName(
      mock_TestScalaRestOrder2WithJavaBeanProps_amountSetter("setAmount")))
      .isEqualTo("amount")
  }

  @Test
  def getPropertyName1(): Unit = {
    val getterOrSetterMethod = mock_TestScalaRestOrder2WithJavaBeanProps_amountGetter("getAmount")
    assertThat(accessorNamingStrategy.getPropertyName(getterOrSetterMethod)).isEqualTo("amount")
  }

  @Test
  def isGetterMethod(): Unit = {
    val getterMethod = mock_TestScalaRestOrder2WithJavaBeanProps_amountGetter("amount")
    assertThat(accessorNamingStrategy.isGetterMethod(getterMethod)).isTrue
  }

  @Test
  def isGetterMethod2(): Unit = {
    val getterMethod = mock_TestScalaRestOrder2WithJavaBeanProps_amountGetter("getAmount")
    assertThat(accessorNamingStrategy.isGetterMethod(getterMethod)).isTrue
  }

  @Test
  def isSetterMethod(): Unit = {
    val setterMethod = mock_TestScalaRestOrder2WithJavaBeanProps_amountSetter("amount_=")
    assertThat(accessorNamingStrategy.isSetterMethod(setterMethod)).isTrue
  }

  @Test
  def isSetterMethod2(): Unit = {
    val setterMethod = mock_TestScalaRestOrder2WithJavaBeanProps_amountSetter("amount_$eq")
    assertThat(accessorNamingStrategy.isSetterMethod(setterMethod)).isTrue
  }

  @Test
  def isSetterMethod3(): Unit = {
    val setterMethod = mock_TestScalaRestOrder2WithJavaBeanProps_amountSetter("setAmount")
    assertThat(accessorNamingStrategy.isSetterMethod(setterMethod)).isTrue
  }

  private def mock_TestScalaRestOrder2WithJavaBeanProps_amountGetter(getterMethodName: String): ExecutableElement =
    mock_getter(classOf[TestScalaRestOrder2WithJavaBeanProps], getterMethodName, classOf[BigDecimal])

  private def mock_TestScalaRestOrder2WithJavaBeanProps_amountSetter(setMethodName: String): ExecutableElement =
    mock_setter(classOf[TestScalaRestOrder2WithJavaBeanProps], setMethodName, classOf[BigDecimal])
}
