package org.mvv.scala.tools.beans

import scala.collection.mutable
//
import java.lang.reflect.Method as JavaMethod
import java.lang.reflect.Field  as JavaField
//
import org.mvv.scala.tools.CollectionsOps.containsOneOf
import org.mvv.scala.tools.{ Logger, nnArray, isNull, uncapitalize, tryDo }
import org.mvv.scala.tools.CollectionsOps.{ filterByType, findByType, asString }


//noinspection ScalaUnusedSymbol
private val log = Logger(classOf[BeanProperties])



class BeanProperties (
  val _class: _Class,
  val beanProps: Map[String, BeanProperty],
  ) :
  private lazy val propsByGetterMethodName: Map[String, BeanProperty] =
    beanProps.values.flatMap( bp => bp.getMethods.map(m => (m.name, bp) )).toMap
  private lazy val propsBySetterMethodName: Map[String, BeanProperty] =
    beanProps.values.flatMap( bp => bp.setMethods.map(m => (m.name, bp) )).toMap

  def isGetter(methodName: String): Boolean = propsByGetterMethodName.contains(methodName)

  //noinspection ScalaUnusedSymbol
  def isSetter(methodName: String, typeName: String): Boolean =
    val methodSetters = propsBySetterMethodName.get(methodName)
      .map(p => p.setMethods.filter(m => methodName == m.name || methodName.toJavaMethodName == m.name.toJavaMethodName))
      .getOrElse(Nil)

    if methodSetters.sizeIs > 1 then
      log.debug(s"There are ${methodSetters.size} method setters ${methodSetters.asString}.")

    methodSetters.nonEmpty

  def getPropertyNameByMethod(methodName: String): Option[String] =
    propsByGetterMethodName.get(methodName) .orElse(propsBySetterMethodName.get(methodName)) .map(_.name)




extension (_class: _Class)
  //def toBeanProperties: BeanProperties =
  //  toBeanProperties(InspectMode.AllSources)

  def toBeanProperties(inspectMode: InspectMode): BeanProperties =
    val propFields: Iterable[(String, _Field)]   = _class.fields.view.values
      .filter(_.isPublic)
      .map(f => (f.name, f))

    val propMethods: Iterable[(String, _Method)] = _class.methods.view.values
      .filter(m => m.isPublic && m.isPropertyAccessor)
      .map(m => (m.toPropName, m))

    val propAccessors: Iterable[(String, _Field|_Method)] = propFields.concat(propMethods)
    val propAccessorsMap: Map[String, Iterable[_Field|_Method]] = propAccessors.groupMap(v => v._1)(v => v._2)

    var beanProps = propAccessorsMap.map(p => toBeanProperty(_class, p))
    if inspectMode == InspectMode.AllSources then
      beanProps = beanProps.map(p => p.withFilledRuntimeTypes)

    val beanPropsMap: Map[String, BeanProperty] = beanProps.map(bp => (bp.name, bp)).toMap

    BeanProperties(_class, beanPropsMap)


private def toBeanProperty(_class: _Class, p: (String, Iterable[_Field|_Method])): BeanProperty =
  val propName = p._1
  val accessors = p._2

  val propField: Option[_Field] = accessors .findByType[_Field]
  val getMethods: List[_Method] = accessors .filterByType[_Method] .filter(_.isGetterMethod) .toList
  val setMethods: List[_Method] = accessors .filterByType[_Method] .filter(_.isSetterMethod) .toList
  val propType: _Type = propField.map(_._type).getOrElse(getMethods.head.returnType)

  BeanProperty(propName, propType, _class, propField, getMethods, setMethods)




extension (beanProp: BeanProperty)
  def withFilledRuntimeTypes: BeanProperty =
    val runtimeOwnerClass: Class[?] = beanProp.ownerClass.runtimeClass
      .getOrElse(loadClass(typeNameToRuntimeClassName(beanProp.ownerClass.fullName)))

    // runtime public field may present only in case of java class (or inheriting from java class)
    val runtimeField: Option[JavaField] = tryDo { beanProp.field.map(f => runtimeOwnerClass.getField(f.name).nn).get }

    val runtimeGetScalaMethod: Option[JavaMethod] = tryDo{ runtimeOwnerClass.getMethod(beanProp.name).nn }
    // I guess it is enough to have only one there
    val runtimeGetMethod: Option[JavaMethod] = runtimeGetScalaMethod
      .orElse { beanProp.getMethods.headOption.flatMap(method => tryDo( runtimeOwnerClass.getMethod(method.name).nn)) }

    val runtimePropertyType: Class[?] = runtimeField
      .map (_.getType.nn)
      .orElse { runtimeGetMethod.map(_.getReturnType.nn) }
      .getOrElse { loadClass(beanProp.propertyType.runtimeTypeName) }

    // I guess it is enough to have only one there
    val runtimeSetMethod: Option[JavaMethod] = beanProp.setMethods.headOption
      .flatMap { method => tryDo(
        runtimeOwnerClass.getMethod(method.name.toJavaMethodName, runtimePropertyType).nn ) }

    beanProp.copy(
      runtimePropertyType = Option(runtimePropertyType),
      runtimeOwnerClass = Option(runtimeOwnerClass),
      runtimeField = runtimeField,
      runtimeGetMethods = Option(runtimeGetMethod.toList),
      runtimeSetMethods = Option(runtimeSetMethod.toList),
    )
