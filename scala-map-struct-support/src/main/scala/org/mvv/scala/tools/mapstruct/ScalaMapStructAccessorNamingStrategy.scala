package org.mvv.scala.tools.mapstruct

import org.mvv.scala.tools.inspection.ClassKind

import javax.lang.model.`type`.TypeMirror
import javax.lang.model.element.{ExecutableElement, TypeElement, VariableElement}
//
import org.mapstruct.ap.spi.{ DefaultAccessorNamingStrategy, MapStructProcessingEnvironment }
//
import org.mvv.scala.tools.{ Logger, ConsoleLogger, LogLevel, replaceSuffix }
import org.mvv.scala.tools.beans.{ BeanProperties, toBeanProperties }
import org.mvv.scala.tools.quotes.topClassOrModuleFullName
import org.mvv.scala.tools.inspection.InspectMode
import org.mvv.scala.tools.inspection._Class
import org.mvv.scala.tools.inspection.light.ScalaBeanInspector



class ScalaMapStructAccessorNamingStrategy extends DefaultAccessorNamingStrategy {

  //private val log = Logger(topClassOrModuleFullName)
  private val log = ConsoleLogger(topClassOrModuleFullName, LogLevel.TRACE)
  private val scalaBeanInspector = ScalaBeanInspector()

  override def init(processingEnvironment: MapStructProcessingEnvironment): Unit = super.init(processingEnvironment)

  override def isGetterMethod(method: ExecutableElement): Boolean =
    val mName = method.methodName
    log.trace(s"isGetterMethod ($mName) => ${method.asDump}")

    if method.paramCount != 0 then
      log.trace(s"isGetterMethod ($mName) => ${method.getEnclosingElement}.$method is getter [false] (by method.paramCount ${method.paramCount}).")
      return false

    val classFullName = method.enclosingClassFullName
    val _class: _Class = scalaBeanInspector.inspectClass(classFullName)

    val isGetter: Boolean = _class.classKind match
      case ClassKind.Java =>
        val isJavaGetter = super.isGetterMethod(method)
        log.trace(s"isGetterMethod ($mName) for java class => ${method.getEnclosingElement}.$method is getter [$isJavaGetter] (by super).")
        isJavaGetter
      case _ =>
        val beanProps: BeanProperties = getBeanPropertiesOfEnclosingClass(method)
        val isScalaGetter = beanProps.isGetter(mName)

        log.trace(s"isGetterMethod ($mName) => ${method.getEnclosingElement}.$method is getter [$isScalaGetter].")
        isScalaGetter

    isGetter

  override def isSetterMethod(method: ExecutableElement): Boolean =
    val mName = method.methodName
    log.trace(s"isSetterMethod ($mName) => ${method.asDump}")

    if method.paramCount != 1 then
      log.trace(s"isSetterMethod ($mName) => ${method.getEnclosingElement}.$method is setter [false] (by method.paramCount ${method.paramCount}).")
      return false

    val classFullName = method.enclosingClassFullName
    val _class: _Class = scalaBeanInspector.inspectClass(classFullName)

    val isSetter: Boolean = _class.classKind match
      case ClassKind.Java =>
        val isJavaSetter = super.isSetterMethod(method)
        log.trace(s"isSetterMethod ($mName) for java class => ${method.getEnclosingElement}.$method is setter [$isJavaSetter] (by super).")
        isJavaSetter
      case _ =>
        val beanProps: BeanProperties = getBeanPropertiesOfEnclosingClass(method)
        val firstParamaTypeStr = method.firstParamTypeAsString
        val isScalaSetter = beanProps.isSetterOneOf(allPossibleScalaSetterMethodNames(mName), firstParamaTypeStr)

        log.trace(s"isSetterMethod ($mName) => ${method.getEnclosingElement}.$method is setter [$isScalaSetter].")
        isScalaSetter

    isSetter


  override def isFluentSetter(method: ExecutableElement): Boolean = super.isFluentSetter(method)

  override def getPropertyName(getterOrSetterMethod: ExecutableElement): String =
    val mName = getterOrSetterMethod.methodName
    log.trace(s"getPropertyName ($mName) => ${getterOrSetterMethod.asDump}")

    val beanProps: BeanProperties = getBeanPropertiesOfEnclosingClass(getterOrSetterMethod)
    val propNameOption = beanProps.getPropertyNameByOneOfMethods(
        allPossibleScalaSetterMethodNames(mName))
    val propName = propNameOption .getOrElse(super.getPropertyName(getterOrSetterMethod).nn)

    log.trace(s"getPropertyName ($mName) => ${getterOrSetterMethod.getEnclosingElement}.$getterOrSetterMethod => $propName.")
    propName


  override def isAdderMethod(method: ExecutableElement): Boolean = super.isAdderMethod(method)
  // for adder method
  override def getElementName(adderMethod: ExecutableElement): String = super.getElementName(adderMethod).nn

  private def getBeanPropertiesOfEnclosingClass(method: ExecutableElement): BeanProperties =
    val _class: _Class = scalaBeanInspector.inspectClass(method.enclosingClassFullName)
    // for MapStruct it is enough only InspectMode.ScalaAST
    _class.toBeanProperties(InspectMode.ScalaAST)
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
