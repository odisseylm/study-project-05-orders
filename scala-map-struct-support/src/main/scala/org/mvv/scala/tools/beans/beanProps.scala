package org.mvv.scala.tools.beans

import scala.collection.mutable
//
import java.lang.reflect.Method as JavaMethod
import java.lang.reflect.Field  as JavaField
//
import org.mvv.scala.tools.CollectionsOps.containsOneOf
import org.mvv.scala.tools.{ Logger, nnArray, isNull, uncapitalize, tryDo }


//noinspection ScalaUnusedSymbol
private val log = Logger(classOf[BeanProperties])


/**
 * It probably has to much info, but it is to implement easily both MapStruct extension and java bean descriptor.
 */
case class BeanProperty (
  name: String,
  propertyType: _Type,
  ownerClass: _Class,
  field: Option[_Field],
  getMethods: List[_Method],
  setMethods: List[_Method],

  // These fields will be surely available after scala compilation
  // but if this class is used during macros expansion
  // (during scala compilation time it most probably will be inaccessible)
  runtimePropertyType: Option[Class[?]] = None,
  runtimeOwnerClass: Option[Class[?]] = None,
  runtimeField: Option[JavaField] = None,
  runtimeGetMethods: Option[List[JavaMethod]] = None,
  runtimeSetMethods: Option[List[JavaMethod]] = None,
)


enum PropAccessKind :
  case Getter, Setter


// for MapStruct
case class BeanPropAccessMethod (
  propName: String,
  accessKind: PropAccessKind,
  methodName: String,
  propertyType: _Type,
)


class BeanProperties (
  val _class: _Class,
  val beanProps: Map[String, BeanProperty],
  ) :
  private lazy val propsByGetterMethodName: Map[String, BeanProperty] =
    beanProps.values.flatMap( bp => bp.getMethods.map(m => (m.name, bp) )).toMap
  private lazy val propsBySetterMethodName: Map[String, BeanProperty] =
    beanProps.values.flatMap( bp => bp.setMethods.map(m => (m.name, bp) )).toMap

  def isGetter(methodName: String): Boolean = propsByGetterMethodName.contains(methodName)
  def isSetter(methodName: String, typeName: String): Boolean =
    val prop = propsBySetterMethodName.get(methodName)
      .iterator
      .flatMap(p => p.setMethods.iterator
        .filter(m => methodName == m.name || methodName.toJavaMethodName == m.name.toJavaMethodName))
      //.find(typeName == _.firstParamType.getName.nn)
      .find(_ => true) // get 1st
    prop.isDefined
  def getPropertyNameByMethod(methodName: String): Option[String] =
    propsByGetterMethodName.get(methodName) .orElse(propsBySetterMethodName.get(methodName)) .map(_.name)



extension (_class: _Class)
  def beanProperties: BeanProperties =
    beanProperties(false)

  // TODO: replace Boolean by enum
  def beanProperties(fillRuntimeTypes: Boolean): BeanProperties =
    val propFields: Iterable[(String, _Field)]   = _class.fields.view.values
      .filter(_.isPublic)
      .map(f => (f.name, f))

    val propMethods: Iterable[(String, _Method)] = _class.methods.view.values
      .filter(m => m.isPublic && m.isPropertyAccessor)
      .map(m => (m.toPropName, m))

    val propAccessors: Iterable[(String, _Field|_Method)] = propFields.concat(propMethods)
    val propAccessorsMap: Map[String, Iterable[_Field|_Method]] = propAccessors.groupMap(v => v._1)(v => v._2)

    var beanProps = propAccessorsMap.map(p => toBeanProperty(_class, p))
    if fillRuntimeTypes then
      beanProps = beanProps.map(p => p.withFilledRuntimeTypes)

    val beanPropsMap: Map[String, BeanProperty] = beanProps.map(bp => (bp.name, bp)).toMap

    BeanProperties(_class, beanPropsMap)


private def toBeanProperty(_class: _Class, p: (String, Iterable[_Field|_Method])): BeanProperty =
  val propName = p._1
  val accessors = p._2

  val propField: Option[_Field] = accessors .find(_.isInstanceOf[_Field]) .map(_.asInstanceOf[_Field])
  val getMethods: List[_Method] = accessors .filter(_.isInstanceOf[_Method]) .map(_.asInstanceOf[_Method]) .filter(_.isGetterMethod) .toList
  val setMethods: List[_Method] = accessors .filter(_.isInstanceOf[_Method]) .map(_.asInstanceOf[_Method]) .filter(_.isSetterMethod) .toList
  val propType: _Type = propField.map(_._type).getOrElse(getMethods.head.returnType)

  BeanProperty(propName, propType, _class, propField, getMethods, setMethods)


// TODO: move to other file
extension (m: _Method)
  private def isPropertyAccessor: Boolean =
    m.modifiers.containsOneOf(
      _Modifier.ScalaStandardFieldAccessor, _Modifier.ScalaCustomFieldAccessor, _Modifier.JavaPropertyAccessor)

  private def isGetterMethod: Boolean =
    m.isPropertyAccessor && m.mainParams.isEmpty && !m.returnType.isVoid
  private def isSetterMethod: Boolean =
    m.isPropertyAccessor && m.mainParams.sizeIs == 1

  private def toPropName: String = m match
    case javaPropM if javaPropM.modifiers.containsOneOf(_Modifier.JavaPropertyAccessor) =>
      javaPropM.name match
        case setMName if setMName.startsWith("set") => setMName.stripPrefix("set").uncapitalize
        case isMName  if isMName.startsWith("is")  => isMName.stripPrefix("is").uncapitalize
        case getMName if getMName.startsWith("get") => getMName.stripPrefix("get").uncapitalize
        case other => other
    case scalaPropM if scalaPropM.modifiers.containsOneOf(_Modifier.ScalaStandardFieldAccessor, _Modifier.ScalaCustomFieldAccessor) =>
      scalaPropM.name match
        case setMName if setMName.endsWith("_=") => setMName.stripSuffix("_=")
        case setMName if setMName.endsWith("_$eq") => setMName.stripSuffix("_$eq")
        case other => other
    case other => other.name


extension (m: _ClassMember)
  private def isPublic: Boolean = m.visibility == _Visibility.Public


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
