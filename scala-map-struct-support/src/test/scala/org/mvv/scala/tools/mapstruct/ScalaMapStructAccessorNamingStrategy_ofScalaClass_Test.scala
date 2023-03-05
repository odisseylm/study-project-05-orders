package org.mvv.scala.tools.mapstruct

import scala.language.unsafeNulls
import scala.compiletime.uninitialized
import scala.annotation.meta.beanGetter
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



enum TestRestBuySellType1 :
  case Buy
  case Sell

class TestScalaRestOrder1 :
  var amount: BigDecimal = uninitialized
  var cur1: String = uninitialized
  var cur2: String = uninitialized
  var buySellType: TestRestBuySellType1 = uninitialized



//noinspection AccessorLikeMethodIsUnit
class ScalaMapStructAccessorNamingStrategy_ofScalaClass_Test {
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
      mock_TestScalaRestOrder1_amountGetter("amount")))
      .isEqualTo("amount")

    assertThat(accessorNamingStrategy.getPropertyName(
      mock_TestScalaRestOrder1_amountGetter("getAmount")))
      .isEqualTo("amount")

    assertThat(accessorNamingStrategy.getPropertyName(
      mock_TestScalaRestOrder1_amountSetter("amount_=")))
      .isEqualTo("amount")

    assertThat(accessorNamingStrategy.getPropertyName(
      mock_TestScalaRestOrder1_amountSetter("amount_$eq")))
      .isEqualTo("amount")

    assertThat(accessorNamingStrategy.getPropertyName(
      mock_TestScalaRestOrder1_amountSetter("setAmount")))
      .isEqualTo("amount")
  }

  @Test
  def getPropertyName1(): Unit = {
    val getterOrSetterMethod = mock_TestScalaRestOrder1_amountGetter("getAmount")
    assertThat(accessorNamingStrategy.getPropertyName(getterOrSetterMethod)).isEqualTo("amount")
  }

  @Test
  def isGetterMethod(): Unit = {
    val getterMethod = mock_TestScalaRestOrder1_amountGetter("amount")
    assertThat(accessorNamingStrategy.isGetterMethod(getterMethod)).isTrue
  }

  @Test
  def isGetterMethod2(): Unit = {
    val getterMethod = mock_TestScalaRestOrder1_amountGetter("getAmount")
    assertThat(accessorNamingStrategy.isGetterMethod(getterMethod)).isFalse
  }

  @Test
  def isSetterMethod(): Unit = {
    val setterMethod = mock_TestScalaRestOrder1_amountSetter("amount_=")
    assertThat(accessorNamingStrategy.isSetterMethod(setterMethod)).isTrue
  }

  @Test
  def isSetterMethod2(): Unit = {
    val setterMethod = mock_TestScalaRestOrder1_amountSetter("amount_$eq")
    assertThat(accessorNamingStrategy.isSetterMethod(setterMethod)).isTrue
  }

  @Test
  def isSetterMethod3(): Unit = {
    val setterMethod = mock_TestScalaRestOrder1_amountSetter("setAmount")
    assertThat(accessorNamingStrategy.isSetterMethod(setterMethod)).isFalse
  }

  private def mock_TestScalaRestOrder1_amountGetter(getterMethodName: String): ExecutableElement =
    mock_getter(classOf[TestScalaRestOrder1], getterMethodName, classOf[BigDecimal])

  private def mock_TestScalaRestOrder1_amountSetter(setMethodName: String): ExecutableElement =
    mock_setter(classOf[TestScalaRestOrder1], setMethodName, classOf[BigDecimal])
}


def mock_getter(cls: Class[?], getterMethodName: String, propClass: Class[?]): ExecutableElement = {
  val getterMethod = mock(classOf[ExecutableElement])
  when(getterMethod.getSimpleName).thenReturn(Name(getterMethodName))
  when(getterMethod.getParameters).thenReturn(Collections.emptyList())

  val propType = mock(classOf[TypeMirror])
  when(propType.getKind).thenReturn(TypeKind.OTHER)
  when(propType.toString).thenReturn(propClass.getName)
  when(getterMethod.getReturnType).thenReturn(propType)

  val enclosingElement: TypeElement = mock(classOf[TypeElement])
  when(enclosingElement.getQualifiedName).thenReturn(Name(cls.getName))

  when(getterMethod.getEnclosingElement).thenReturn(enclosingElement)

  getterMethod
}

private def mock_setter(cls: Class[?], setMethodName: String, propClass: Class[?]): ExecutableElement = {
  val setterMethod = mock(classOf[ExecutableElement])
  when(setterMethod.getSimpleName).thenReturn(Name(setMethodName))
  when(setterMethod.getParameters).thenReturn(Collections.emptyList())

  val returnType = mock(classOf[TypeMirror])
  when(returnType.getKind).thenReturn(TypeKind.VOID)
  when(returnType.toString).thenReturn(Void.TYPE.getName)
  when(setterMethod.getReturnType).thenReturn(returnType)

  val propType = mock(classOf[TypeMirror])
  when(propType.getKind).thenReturn(TypeKind.OTHER)
  when(propType.toString).thenReturn(propClass.getName)
  when(setterMethod.getTypeParameters).thenReturn(singletonList(propType))

  val firstParam = mock(classOf[VariableElement])
  when(firstParam.getSimpleName).thenReturn(Name("v"))
  when(firstParam.asType()).thenReturn(propType)
  when(setterMethod.getParameters).thenReturn(singletonList(firstParam))

  val enclosingElement: TypeElement = mock(classOf[TypeElement])
  when(enclosingElement.getQualifiedName).thenReturn(Name(cls.getName))
  val TestScalaRestOrder1_TypeMirror = mock(classOf[TypeMirror])
  when(enclosingElement.asType()).thenReturn(TestScalaRestOrder1_TypeMirror)

  when(setterMethod.getEnclosingElement).thenReturn(enclosingElement)

  setterMethod
}



case class Name(name: String) extends javax.lang.model.element.Name :
  override def toString: String = name
  override def contentEquals(cs: CharSequence): Boolean = name.contentEquals(cs)
  override def subSequence(start: Int, end: Int): CharSequence = name.subSequence(start, end)
  override def charAt(index: Int): Char = name.charAt(index)
  override def length(): Int = name.length
