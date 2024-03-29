package org.mvv.scala.mapstruct

import scala.collection.mutable
//
import java.lang.reflect.Method as JavaMethod
//
import org.mvv.scala.tools.CollectionsOps.containsOneOf
import org.mvv.scala.tools.{ Logger, nnArray, isNull }


private val log = Logger(classOf[BeanProperties])


/**
 * It probably has to much info, but it is to implement easily both MapStruct extension and java bean descriptor.
 */
class BeanProperty (
  val name: String,
  val propertyType: Class[?],
  // it can be in java but later in jav
  //val ownerKind: PropertyOwnerKindType,
  val owner: _Class,
  val ownerClass: Class[?],
  val javaGetMethods: List[JavaMethod],
  val javaSetMethods: List[JavaMethod],
)


enum PropAccessKind :
  case Getter, Setter


// for MapStruct
case class BeanPropAccessMethod (propName: String, accessKind: PropAccessKind, methodName: String, propertyType: Class[?])

class BeanProperties (
  val _class: _Class,
  val beanProps: Map[String, BeanProperty],
  ) :
  private lazy val propsByGetterMethodName: Map[String, BeanProperty] =
    beanProps.values.flatMap( bp => bp.javaGetMethods.map(m => (m.getName.nn, bp) )).toMap
  private lazy val propsBySetterMethodName: Map[String, BeanProperty] =
    beanProps.values.flatMap( bp => bp.javaSetMethods.map(m => (m.getName.nn, bp) )).toMap

  def isGetter(methodName: String): Boolean = propsByGetterMethodName.contains(methodName)
  def isSetter(methodName: String, typeName: String): Boolean =
    val prop = propsBySetterMethodName.get(methodName)
      .iterator
      .flatMap(p => p.javaSetMethods.iterator.filter(methodName == _.getName.nn))
      .find(typeName == _.firstParamType.getName.nn)
    prop.isDefined
  def getPropertyNameByMethod(methodName: String): Option[String] =
    propsByGetterMethodName.get(methodName) .orElse(propsBySetterMethodName.get(methodName)) .map(_.name)


extension (_class: _Class)
  def beanProperties: BeanProperties =
    val allPropMethods = mutable.ArrayBuffer[BeanPropAccessMethod]()

    val gettersForValAndVars = _class.fields.values
      .filter(_.isPublic)
      .map(f => (f, findGetterMethod(_class.runtimeClass, f.name)))
      .filter(_._2.isDefined) // if method is found
      .map(m => getterBeanPropAccessMethod(m))
    allPropMethods.addAll(gettersForValAndVars)

    val getters = _class.methods.values
      .filter(m => m.isPublic && m.isPropertyAccessor && m.mainParams.isEmpty)
      .map(m => (m, findGetterMethod(_class.runtimeClass, m.name)))
      .filter(_._2.isDefined) // if method is found
      .map(m => getterBeanPropAccessMethod(m))
    allPropMethods.addAll(getters)

    val setters = _class.methods.values
      .filter(m => m.isPublic && m.isPropertyAccessor && m.mainParams.size == 1)
      .map(m => (m, findSetterMethod(_class.runtimeClass, m.name)))
      .filter(_._2.isDefined) // if method is found
      .map(m => setterBeanPropAccessMethod(m))
    allPropMethods.addAll(setters)

    val props: Map[String, collection.Iterable[BeanPropAccessMethod]] = allPropMethods.groupBy(_.propName)

    val beanProps: Map[String, BeanProperty] = props.map(vv => toBeanProperty(_class, vv))
                                                    .filter(_.isDefined)
                                                    .map(bp => (bp.get.name, bp.get)).toMap
    BeanProperties(_class, beanProps)


private def toBeanProperty(_class: _Class, nameAndMethods: (String, collection.Iterable[BeanPropAccessMethod]) ): Option[BeanProperty] =
  val (propName, methods) = nameAndMethods

  val gettersAndSetters = methods.groupBy(_.accessKind)
  val getters = gettersAndSetters.getOrElse(PropAccessKind.Getter, Nil)
  if getters.isEmpty then
    log.warn(s"Property [$propName] does not have any getters.")
    return None

  val propType = getters.head.propertyType
  // T O D O: refactor to avoid finding method twice
  val getterMethods = getters
    .map(v => findGetterMethod(_class.runtimeClass, v.methodName).get)
    .toList

  val setterMethods = gettersAndSetters.getOrElse(PropAccessKind.Setter, Nil)
    // T O D O: refactor to avoid finding method twice
    .map(v => findSetterMethod(_class.runtimeClass, v.methodName, propType))
    .filter(_.isDefined)
    .map(_.get)
    .toList

  Option(BeanProperty (
    name = propName,
    propertyType = propType,
    owner = _class,
    ownerClass = _class.runtimeClass,
    javaGetMethods = getterMethods,
    javaSetMethods= setterMethods,
  ))


private def getterBeanPropAccessMethod(m_m: (_ClassMember, Option[JavaMethod]) ): BeanPropAccessMethod =
  val member = m_m._1;  val method = m_m._2.get
  BeanPropAccessMethod(member.toBeanPropertyName, PropAccessKind.Getter, method.getName.nn, method.getReturnType.nn)

private def setterBeanPropAccessMethod(m_m: (_ClassMember, Option[JavaMethod]) ): BeanPropAccessMethod =
  val member = m_m._1;  val method = m_m._2.get
  BeanPropAccessMethod(member.toBeanPropertyName, PropAccessKind.Setter, method.getName.nn, method.getParameterTypes.nnArray(0).nn)

private def findGetterMethod(cls: Class[?], methodName: String): Option[JavaMethod] =
  try { Option(cls.getMethod(methodName).nn) } catch case _: Exception => None

// actually there can be several overloaded methods...
// for that reason we really use type of getters as property type
private def findSetterMethod(cls: Class[?], methodName: String): Option[JavaMethod] =
  findSetterMethodImpl(cls, methodName)
    .orElse(findSetterMethodImpl(cls, scalaMethodNameToJava(methodName)))
private def findSetterMethodImpl(cls: Class[?], methodName: String): Option[JavaMethod] =
  val methods: Array[JavaMethod] = cls.getMethods.nnArray
  methods.find(m => m.getName.nn == methodName && m.getParameterCount == 1)

private def findSetterMethod(cls: Class[?], methodName: String, propType: Class[?]): Option[JavaMethod] =
  findSetterMethodImpl(cls, methodName, propType)
    .orElse(findSetterMethodImpl(cls, scalaMethodNameToJava(methodName), propType))
private def findSetterMethodImpl(cls: Class[?], methodName: String, propType: Class[?]): Option[JavaMethod] =
  val methods: Array[JavaMethod] = cls.getMethods.nnArray
  val methodsWithTheSameName = methods.view
    .filter(m => m.getName.nn == methodName && m.getParameterCount == 1)
    .toList

  if methodsWithTheSameName.isEmpty then return None

  val methodWithExpectedType = methodsWithTheSameName.find { m => propType.isAssignableFrom(m.firstParamType) }

  if methodWithExpectedType.isDefined then methodWithExpectedType
    else
      log.warn(s"Probably method/methods ${cls.getName}.$methodName has incorrect type.")
      // I hope that problem is connected with improper processing generics (or something like that)
      // but really everything will work fine.
      Option(methodsWithTheSameName.head)

extension (member: _ClassMember)
  private def toBeanPropertyName: String = member match
      case f: _Field => f.name
      case m: _Method => m.name match
        case getName if getName.startsWith("is") => getName.stripPrefix("is").uncapitalize
        case getName if getName.startsWith("get") => getName.stripPrefix("get").uncapitalize
        case setName if setName.startsWith("set") => setName.stripPrefix("set").uncapitalize
        case scalaSetterName if scalaSetterName.endsWith("_=") => scalaSetterName.stripSuffix("_=")
        case scalaSetterName if scalaSetterName.endsWith("_$eq") => scalaSetterName.stripSuffix("_$eq")
        case other => other


extension (s: String)
  private def uncapitalize: String =
    if s.isNull || s.isEmpty || !s.charAt(0).isUpper then s
    else s"${s.charAt(0).toLower}${s.substring(1)}"


extension (m: JavaMethod)
  private def firstParamType: Class[?] =
    require(m.getParameterCount >= 1)
    m.getParameterTypes.nnArray(0)

extension (m: _Method)
  private def isPropertyAccessor: Boolean =
    m.modifiers.containsOneOf(
      _Modifier.ScalaStandardFieldAccessor, _Modifier.ScalaCustomFieldAccessor, _Modifier.JavaPropertyAccessor)

extension (m: _ClassMember)
  private def isPublic: Boolean = m.visibility == _Visibility.Public
