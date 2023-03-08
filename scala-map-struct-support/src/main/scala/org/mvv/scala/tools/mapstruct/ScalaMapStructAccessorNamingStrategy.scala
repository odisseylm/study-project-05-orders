package org.mvv.scala.tools.mapstruct

import scala.collection.concurrent
//
import javax.lang.model.`type`.TypeMirror
import javax.lang.model.element.{ExecutableElement, TypeElement, VariableElement}
//
import org.mapstruct.ap.spi.{ DefaultAccessorNamingStrategy, MapStructProcessingEnvironment }
//
import org.mvv.scala.tools.{ Logger, ConsoleLogger, LogLevel, replaceSuffix, StopWatch }
import org.mvv.scala.tools.CollectionsOps.containsOneOf
import org.mvv.scala.tools.beans.{ BeanProperties, toBeanProperties }
import org.mvv.scala.tools.quotes.topClassOrModuleFullName
import org.mvv.scala.tools.inspection.{ ClassKind, InspectMode, _Class, ScalaBeanInspector }



private val toSkipGetters: Set[String] = Set(
  "scala.Product.productIterator",
  "scala.Product.productElementNames",
  "productArity",
  "productPrefix",
  "toString",
)

class ScalaMapStructAccessorNamingStrategy extends DefaultAccessorNamingStrategy {

  private val log = Logger(topClassOrModuleFullName)
  //private val log = ConsoleLogger(topClassOrModuleFullName, LogLevel.TRACE)
  private val scalaBeanInspector = ScalaBeanInspector.createLight()
  private val beanPropertiesCache: concurrent.Map[String, BeanProperties] = concurrent.TrieMap()

  override def init(processingEnvironment: MapStructProcessingEnvironment): Unit = super.init(processingEnvironment)

  override def isGetterMethod(method: ExecutableElement): Boolean =
    val stopWatch = StopWatch.started()
    val mName = method.methodName
    val clsMName = method.classAndMethodName
    log.trace(s"isGetterMethod ($clsMName) => ${method.asDump}")

    if method.paramCount != 0 then
      log.trace(s"isGetterMethod ($clsMName) => ${method.getEnclosingElement}.$method is getter [false] (by method.paramCount ${method.paramCount}).")
      return false

    val classFullName = method.enclosingClassFullName
    val returnType = method.getReturnType.toString
    val _class: _Class = scalaBeanInspector.inspectClass(classFullName)

    val isGetter: Boolean = _class.classKind match
      case ClassKind.Java =>
        val isJavaGetter = super.isGetterMethod(method)

        log.trace(s"isGetterMethod ($clsMName) for java class => ${method.getEnclosingElement}.$method ($returnType) is getter [$isJavaGetter] (by super) (took ${stopWatch.elapsed}).")
        if isJavaGetter then
          log.debug(s"isGetterMethod ($clsMName) for java class => ${method.getEnclosingElement}.$method ($returnType) is getter [$isJavaGetter] (by super) (took ${stopWatch.elapsed}).")

        isJavaGetter
      case _ =>
        val beanProps: BeanProperties = getBeanPropertiesOfEnclosingClass(method)
        val fullMethodName =s"${method.getEnclosingElement}.$mName"

        val toSkip = toSkipGetters.containsOneOf(mName, fullMethodName)
          || mName.contains("$default$")
          || method.isComponentMethod

        val isScalaGetter = !toSkip && beanProps.isGetter(mName)

        log.trace(s"isGetterMethod ($clsMName) => ${method.getEnclosingElement}.$method ($returnType) is getter [$isScalaGetter] (took ${stopWatch.elapsed}).")
        if isScalaGetter then
          log.debug(s"isGetterMethod ($clsMName) => ${method.getEnclosingElement}.$method ($returnType) is getter [$isScalaGetter] (took ${stopWatch.elapsed}).")
        isScalaGetter

    isGetter

  override def isSetterMethod(method: ExecutableElement): Boolean =
    val stopWatch = StopWatch.started()
    val mName = method.methodName
    val clsMName = method.classAndMethodName
    log.trace(s"isSetterMethod ($mName) => ${method.asDump}")

    if method.paramCount != 1 then
      log.trace(s"isSetterMethod ($clsMName) => ${method.getEnclosingElement}.$method is setter [false] (by method.paramCount ${method.paramCount}).")
      return false

    val classFullName = method.enclosingClassFullName
    val _class: _Class = scalaBeanInspector.inspectClass(classFullName)
    val firstParamTypeStr = method.firstParamTypeAsString

    val isSetter: Boolean = _class.classKind match
      case ClassKind.Java =>
        val isJavaSetter = super.isSetterMethod(method)

        log.trace(s"isSetterMethod ($clsMName) for java class => ${method.getEnclosingElement}.$method is setter [$isJavaSetter] (by super) (took ${stopWatch.elapsed}).")
        if isJavaSetter then
          log.debug(s"isSetterMethod ($clsMName) for java class => ${method.getEnclosingElement}.$method is setter [$isJavaSetter] (by super) (took ${stopWatch.elapsed}).")

        isJavaSetter
      case _ =>
        val beanProps: BeanProperties = getBeanPropertiesOfEnclosingClass(method)
        val isScalaSetter = beanProps.isSetterOneOf(allPossibleScalaSetterMethodNames(mName), firstParamTypeStr)

        log.trace(s"isSetterMethod ($clsMName) => ${method.getEnclosingElement}.$method is setter [$isScalaSetter] (took ${stopWatch.elapsed}).")
        if isScalaSetter then
          log.debug(s"isSetterMethod ($clsMName) => ${method.getEnclosingElement}.$method is setter [$isScalaSetter] (took ${stopWatch.elapsed}).")

        isScalaSetter

    isSetter


  override def isFluentSetter(method: ExecutableElement): Boolean = super.isFluentSetter(method)

  override def getPropertyName(getterOrSetterMethod: ExecutableElement): String =
    val stopWatch = StopWatch.started()
    val mName = getterOrSetterMethod.methodName
    val clsMName = getterOrSetterMethod.classAndMethodName
    log.trace(s"getPropertyName ($clsMName) => ${getterOrSetterMethod.asDump}")

    val beanProps: BeanProperties = getBeanPropertiesOfEnclosingClass(getterOrSetterMethod)
    val propNameOption = beanProps.getPropertyNameByOneOfMethods(
        allPossibleScalaSetterMethodNames(mName))
    val propName = propNameOption .getOrElse(super.getPropertyName(getterOrSetterMethod).nn)

    log.debug(s"getPropertyName ($clsMName) => ${getterOrSetterMethod.getEnclosingElement}.$getterOrSetterMethod => [$propName] (took ${stopWatch.elapsed}).")
    propName


  override def isAdderMethod(method: ExecutableElement): Boolean = super.isAdderMethod(method)
  // for adder method
  override def getElementName(adderMethod: ExecutableElement): String = super.getElementName(adderMethod).nn

  private def getBeanPropertiesOfEnclosingClass(method: ExecutableElement): BeanProperties =
    val classFullName = method.enclosingClassFullName
    beanPropertiesCache.getOrElseUpdate( classFullName, {
      val _class: _Class = scalaBeanInspector.inspectClass(classFullName)
      // for MapStruct it is enough only InspectMode.ScalaAST
      _class.toBeanProperties(InspectMode.ScalaAST) }
    )
}



// to avoid warning about code duplication
extension (method: ExecutableElement)
  def paramCount: Int = method.getParameters.nn.size

  def enclosingClassFullName: String =
    method.getEnclosingElement.asInstanceOf[TypeElement].getQualifiedName.nn.toString.nn

  def methodName: String = method.getSimpleName.nn.toString.nn

  def classAndMethodName: String = s"${method.getEnclosingElement.nn.getSimpleName}.${method.getSimpleName}"

  // like _1, _2, _3, etc
  def isComponentMethod: Boolean = isComponentMethodName(method.methodName)

  def firstParamTypeAsString: String = method.getParameters.nn.get(0).nn.typeAsString

  def asDump: AnyRef = s"enclosing: ${method.getEnclosingElement}, method: $method }"



extension [P <: VariableElement](param: P)
  //noinspection ScalaUnusedSymbol
  def typeAsString: String = param.asType.nn.toString


private def allPossibleScalaSetterMethodNames(baseSetterMethod: String): List[String] =
  List(baseSetterMethod, baseSetterMethod.replaceSuffix("_$eq", "_="), baseSetterMethod.replaceSuffix("_=", "_$eq"))
    .distinct


// like _1, _2, _3, etc
private def isComponentMethodName(methodName: String): Boolean =
  if methodName.startsWith("_") then
    val otherPart = methodName.substring(1)
    try { Integer.parseInt(otherPart); true } catch case _: NumberFormatException => false
  else
    false