package org.mvv.scala.mapstruct

//
import javax.lang.model.`type`.TypeMirror
import javax.lang.model.element.{ExecutableElement, TypeElement, VariableElement}
//
import org.mapstruct.ap.spi.MapStructProcessingEnvironment
//
import org.mvv.mapstruct.scala.*


class ScalaMapStructAccessorNamingStrategy extends org.mapstruct.ap.spi.DefaultAccessorNamingStrategy {

  private val log = Logger(classOf[ScalaMapStructAccessorNamingStrategy])
  private val scalaBeansInspector = ScalaBeansInspector()

  override def init(processingEnvironment: MapStructProcessingEnvironment): Unit = super.init(processingEnvironment)

  override def isGetterMethod(method: ExecutableElement): Boolean =
    log.trace(s"isGetterMethod => ${method.asDump}")

    if super.isGetterMethod(method) then return true
    if method.paramCount != 0 then return false

    val beanProps: BeanProperties = getBeanPropertiesOfEnclosingClass(method)
    val isScalaGetter = beanProps.isGetter(method.methodName)

    log.trace(s"isGetterMethod => ${method.getEnclosingElement}.$method is getter [$isScalaGetter].")
    isScalaGetter


  override def isSetterMethod(method: ExecutableElement): Boolean =
    log.trace(s"isSetterMethod => ${method.asDump}")

    if super.isSetterMethod(method) then return true
    if method.paramCount != 1 then return false

    val beanProps: BeanProperties = getBeanPropertiesOfEnclosingClass(method)
    val isScalaSetter = beanProps.isSetter(method.methodName, method.firstParamTypeAsString)

    log.trace(s"isSetterMethod => ${method.getEnclosingElement}.$method is setter [$isScalaSetter].")
    isScalaSetter


  override def isFluentSetter(method: ExecutableElement): Boolean = super.isFluentSetter(method)

  override def getPropertyName(getterOrSetterMethod: ExecutableElement): String =
    log.trace(s"getPropertyName => ${getterOrSetterMethod.asDump}")

    val beanProps: BeanProperties = getBeanPropertiesOfEnclosingClass(getterOrSetterMethod)
    val propNameOption = beanProps.getPropertyNameByMethod(getterOrSetterMethod.methodName)
    val propName = propNameOption.getOrElse(super.getPropertyName(getterOrSetterMethod).nn)

    log.trace(s"getPropertyName => ${getterOrSetterMethod.getEnclosingElement}.$getterOrSetterMethod => $propName.")
    propName


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
  def firstParamTypeAsString: String =
    method.getParameters.nn.get(0).nn.typeAsString
  def asDump: AnyRef =
    s"enclosing: ${method.getEnclosingElement}, method: $method }"

extension [P <: VariableElement](param: P)
  //noinspection ScalaUnusedSymbol
  def typeAsString: String = param.asType.nn.toString
