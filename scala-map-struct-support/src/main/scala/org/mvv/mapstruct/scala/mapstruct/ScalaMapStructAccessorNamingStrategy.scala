package org.mvv.mapstruct.scala.mapstruct

//
import javax.lang.model.`type`.TypeMirror
import javax.lang.model.element.{ExecutableElement, TypeElement, VariableElement}
//
import org.mapstruct.ap.spi.MapStructProcessingEnvironment
//
import org.mvv.mapstruct.scala._Class
import org.mvv.mapstruct.scala.ScalaBeansInspector
import org.mvv.mapstruct.scala.BeanProperties
import org.mvv.mapstruct.scala.BeanProperty
import org.mvv.mapstruct.scala.beanProperties
import org.mvv.mapstruct.scala.nnArray


// TODO: add logging
class ScalaMapStructAccessorNamingStrategy extends org.mapstruct.ap.spi.DefaultAccessorNamingStrategy {

  private val scalaBeansInspector = ScalaBeansInspector()

  override def init(processingEnvironment: MapStructProcessingEnvironment): Unit = super.init(processingEnvironment)

  override def isGetterMethod(method: ExecutableElement): Boolean =

    val enclosingAsClass = Class.forName(method.getEnclosingElement.toString).nn
    println(s"enclosingAsClass: ${enclosingAsClass.nn.getName}")

    if super.isGetterMethod(method) then return true
    if method.paramCount != 0 then return false

    val beanProps: BeanProperties = getBeanPropertiesOfEnclosingClass(method)
    val isScalaGetter = beanProps.isGetter(method.methodName)

    println(s"^^^ ${method.methodName} is getter $isScalaGetter")
    isScalaGetter

  override def isSetterMethod(method: ExecutableElement): Boolean =
    println(s"### isSetterMethod: $method ${method.getSimpleName}")

    if super.isSetterMethod(method) then return true
    if method.paramCount != 1 then return false

    val firstParamType: String = method.getParameters.nn.get(0).nn.typeAsString
    val beanProps: BeanProperties = getBeanPropertiesOfEnclosingClass(method)

    val isScalaSetter = beanProps.isSetter(method.methodName, firstParamType)
    println(s"^^^ ${method.methodName} is setter $isScalaSetter")
    isScalaSetter

  override def isFluentSetter(method: ExecutableElement): Boolean = super.isFluentSetter(method)

  override def getPropertyName(getterOrSetterMethod: ExecutableElement): String =
    println(s"### getPropertyName: $getterOrSetterMethod ${getterOrSetterMethod.getSimpleName}")

    val beanProps: BeanProperties = getBeanPropertiesOfEnclosingClass(getterOrSetterMethod)
    val propNameOption = beanProps.getPropertyNameByMethod(getterOrSetterMethod.methodName)
    propNameOption.getOrElse(super.getPropertyName(getterOrSetterMethod).nn)


  override def isAdderMethod(method: ExecutableElement): Boolean = super.isAdderMethod(method)
  // for adder method
  override def getElementName(adderMethod: ExecutableElement): String = super.getElementName(adderMethod).nn

  private def getBeanPropertiesOfEnclosingClass(method: ExecutableElement): BeanProperties =
    val _class: _Class = scalaBeansInspector.inspectClass(method.enclosingClassFullName)
    _class.beanProperties
}


// to avoid warning about code duplication
extension (method: ExecutableElement)
  def paramCount: Int = method.getParameters.nn.size
  def enclosingClassFullName: String =
    method.getEnclosingElement.asInstanceOf[TypeElement].getQualifiedName.nn.toString.nn
  def methodName: String =
    method.getSimpleName.nn.toString.nn

extension [P <: VariableElement](param: P)
  def typeAsString: String = param.asType.nn.toString