package org.mvv.scala.tools.mapstruct

import javax.lang.model.`type`.TypeMirror
import javax.lang.model.element.{ExecutableElement, TypeElement, VariableElement}
//
import org.mapstruct.ap.spi.DefaultAccessorNamingStrategy
import org.mapstruct.ap.spi.MapStructProcessingEnvironment
//
import org.mvv.scala.tools.{ Logger, ConsoleLogger, LogLevel, replaceSuffix }
import org.mvv.scala.tools.beans.{ _Class, BeanProperties, ScalaBeansInspector, toBeanProperties, InspectMode }




class ScalaMapStructAccessorNamingStrategy extends DefaultAccessorNamingStrategy {

  //private val log = Logger(classOf[ScalaMapStructAccessorNamingStrategy])
  private val log = ConsoleLogger(classOf[ScalaMapStructAccessorNamingStrategy], LogLevel.TRACE)
  private val scalaBeansInspector = ScalaBeansInspector()

  override def init(processingEnvironment: MapStructProcessingEnvironment): Unit = super.init(processingEnvironment)

  override def isGetterMethod(method: ExecutableElement): Boolean =
    val mName = method.methodName
    log.trace(s"isGetterMethod ($mName) => ${method.asDump}")

    if super.isGetterMethod(method) then
      log.trace(s"isGetterMethod ($mName) => ${method.getEnclosingElement}.$method is getter [true] (by super).")
      return true

    if method.paramCount != 0 then
      log.trace(s"isGetterMethod ($mName) => ${method.getEnclosingElement}.$method is getter [false] (by method.paramCount ${method.paramCount}).")
      return false

    val beanProps: BeanProperties = getBeanPropertiesOfEnclosingClass(method)
    val isScalaGetter = beanProps.isGetter(mName)

    log.trace(s"isGetterMethod ($mName) => ${method.getEnclosingElement}.$method is getter [$isScalaGetter].")
    isScalaGetter


  override def isSetterMethod(method: ExecutableElement): Boolean =
    val mName = method.methodName
    log.trace(s"isSetterMethod ($mName) => ${method.asDump}")

    if super.isSetterMethod(method) then
      log.trace(s"isSetterMethod ($mName) => ${method.getEnclosingElement}.$method is setter [true] (by super).")
      return true

    if method.paramCount != 1 then
      log.trace(s"isSetterMethod ($mName) => ${method.getEnclosingElement}.$method is setter [false] (by method.paramCount ${method.paramCount}).")
      return false

    val beanProps: BeanProperties = getBeanPropertiesOfEnclosingClass(method)
    val firstParamaTypeStr = method.firstParamTypeAsString
    val isScalaSetter = beanProps.isSetterOneOf(allPossibleScalaSetterMethodNames(mName), firstParamaTypeStr)

    log.trace(s"isSetterMethod ($mName) => ${method.getEnclosingElement}.$method is setter [$isScalaSetter].")
    isScalaSetter


  override def isFluentSetter(method: ExecutableElement): Boolean = super.isFluentSetter(method)

  override def getPropertyName(getterOrSetterMethod: ExecutableElement): String =
    val mName = getterOrSetterMethod.methodName
    log.trace(s"getPropertyName ($mName) => ${getterOrSetterMethod.asDump}")

    val beanProps: BeanProperties = getBeanPropertiesOfEnclosingClass(getterOrSetterMethod)
    val propNameOption = beanProps.getPropertyNameByOneOfMethods(
        allPossibleScalaSetterMethodNames(mName))
    val propName = propNameOption.getOrElse(super.getPropertyName(getterOrSetterMethod).nn)

    log.trace(s"getPropertyName ($mName) => ${getterOrSetterMethod.getEnclosingElement}.$getterOrSetterMethod => $propName.")
    propName


  override def isAdderMethod(method: ExecutableElement): Boolean = super.isAdderMethod(method)
  // for adder method
  override def getElementName(adderMethod: ExecutableElement): String = super.getElementName(adderMethod).nn

  private def getBeanPropertiesOfEnclosingClass(method: ExecutableElement): BeanProperties =
    val _class: _Class = scalaBeansInspector.inspectClass(method.enclosingClassFullName)
    _class.toBeanProperties(InspectMode.AllSources)
}



// to avoid warning about code duplication
extension (method: ExecutableElement)
  def paramCount: Int = method.getParameters.nn.size

  def enclosingClassFullName: String =
    method.getEnclosingElement.asInstanceOf[TypeElement].getQualifiedName.nn.toString.nn

  def methodName: String = method.getSimpleName.nn.toString.nn

  def firstParamTypeAsString: String = method.getParameters.nn.get(0).nn.typeAsString

  def asDump: AnyRef = s"enclosing: ${method.getEnclosingElement}, method: $method }"



extension [P <: VariableElement](param: P)
  //noinspection ScalaUnusedSymbol
  def typeAsString: String = param.asType.nn.toString


private def allPossibleScalaSetterMethodNames(baseSetterMethod: String): List[String] =
  List(baseSetterMethod, baseSetterMethod.replaceSuffix("_$eq", "_="), baseSetterMethod.replaceSuffix("_=", "_$eq"))
    .distinct
