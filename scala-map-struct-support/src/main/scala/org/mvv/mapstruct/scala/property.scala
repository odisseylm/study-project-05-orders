package org.mvv.mapstruct.scala

import scala.collection.mutable
//
import java.lang.reflect.Method as JavaMethod
//
import CollectionsOps.containsOneOf

//enum PropertyOwnerKindType :
//  case Java, Scala


/**
 * It probably has to much info, but it is to implement easily both MapStruct extension and java bean descriptor.
 */
class BeanProperty (
  val name: String,
  val propertyType: Class[?],
  // it can be in java but later in jav
  //val ownerKind: PropertyOwnerKindType,
  val ownerClass: Class[?],
  val javaGetMethods: List[JavaMethod],
  val javaSetMethods: List[JavaMethod],
  val owner: _Class,
)


enum PropAccessKind :
  case Getter, Setter


// for MapStruct
case class BeanPropAccessMethod (propName: String, accessKind: PropAccessKind, methodName: String, propertyType: Class[?])

class BeanProperties (
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
      .flatMap(p => p.javaSetMethods.iterator.map{ m => println(s"||| isSetter($methodName, $typeName)"); m }.filter(methodName == _.getName.nn))
      .map { m => println(s"||| meth444 ${m.getName} ${m.firstParamType.getName} needed typeName $typeName"); m }
      .find(typeName == _.firstParamType.getName.nn)
    println(s"%%% setter prop: $prop")
    prop.isDefined
  def getPropertyNameByMethod(methodName: String): Option[String] =
    propsByGetterMethodName.get(methodName) .orElse(propsBySetterMethodName.get(methodName)) .map(_.name)


extension (cls: _Class)
  def beanProperties: BeanProperties =
    val allPropMethods = mutable.ArrayBuffer[BeanPropAccessMethod]()

    // TODO: refactor all this to make less code

    val gettersForValAndVars = cls.fields.values
      .filter(_.visibility == _Visibility.Public)
      .map(f => (f, findGetterMethod(cls.runtimeClass, f.name)))
      .filter(m_m => m_m._2.isDefined)
      .map(m => getterBeanPropAccessMethod(m))
    allPropMethods.addAll(gettersForValAndVars)

    val customScalaGetters = cls.methods.values
      .filter(_.visibility == _Visibility.Public)
      .filter(_.mainParams.isEmpty)
      .filter(_.modifiers.containsOneOf(_Modifier.ScalaStandardFieldAccessor, _Modifier.ScalaCustomFieldAccessor))
      .map(m => (m, findGetterMethod(cls.runtimeClass, m.name)))
      .filter(m_m => m_m._2.isDefined)
      .map(m => getterBeanPropAccessMethod(m))
    allPropMethods.addAll(customScalaGetters)

    val javaGetGetters = cls.methods.values
      .filter(_.visibility == _Visibility.Public)
      .filter(_.mainParams.isEmpty)
      .filter(_.modifiers.containsOneOf(_Modifier.JavaPropertyAccessor))
      .map(m => (m, findGetterMethod(cls.runtimeClass, "get" + m.name.capitalize)))
      .filter(m_m => m_m._2.isDefined)
      .map(m => getterBeanPropAccessMethod(m))
    allPropMethods.addAll(javaGetGetters)

    val javaIsGetters = cls.methods.values
      .filter(_.visibility == _Visibility.Public)
      .filter(_.mainParams.isEmpty)
      .filter(_.modifiers.containsOneOf(_Modifier.JavaPropertyAccessor))
      .filter(_.returnType.isBool)
      .map(m => (m, findGetterMethod(cls.runtimeClass, "is" + m.name.capitalize)))
      .filter(m_m => m_m._2.isDefined)
      .map(m => getterBeanPropAccessMethod(m))
    allPropMethods.addAll(javaIsGetters)

    val scalaSetters = cls.methods.values
      .filter(_.visibility == _Visibility.Public)
      .filter(_.mainParams.size == 1)
      .filter(_.modifiers.containsOneOf(_Modifier.ScalaStandardFieldAccessor, _Modifier.ScalaCustomFieldAccessor))
      .map(m => (m, findSetterMethod(cls.runtimeClass, m.name)))
      .filter(m_m => m_m._2.isDefined)
      .map(m => setterBeanPropAccessMethod(m))
    allPropMethods.addAll(scalaSetters)

    val javaSetters = cls.methods.values
      .filter(_.visibility == _Visibility.Public)
      .filter(_.mainParams.size == 1)
      .filter(_.modifiers.containsOneOf(_Modifier.JavaPropertyAccessor))
      .map(m => (m, findSetterMethod(cls.runtimeClass, "set" + m.name.capitalize)))
      .filter(m_m => m_m._2.isDefined)
      .map(m => setterBeanPropAccessMethod(m))
    allPropMethods.addAll(javaSetters)

    val props: Map[String, collection.Iterable[BeanPropAccessMethod]] = allPropMethods.groupBy(_.propName)

    val beanProps: Map[String, BeanProperty] = props.map(vv => toBeanProperty(cls, vv))
                                                    .filter(_.isDefined)
                                                    .map(bp => (bp.get.name, bp.get)).toMap
    BeanProperties(beanProps)


private def toBeanProperty(cls: _Class, nameAndMethods: (String, collection.Iterable[BeanPropAccessMethod]) ): Option[BeanProperty] =
  val (propName, methods) = nameAndMethods

  val gettersAndSetters = methods.groupBy(_.accessKind)
  val getters = gettersAndSetters.getOrElse(PropAccessKind.Getter, Nil)
  if getters.isEmpty then
    println(s"WARN property [$propName] does not have any get methods.")
    return None

  val propType = getters.head.propertyType
  // TODO: refactor to avoid finding it twice
  val getterMethods = getters
    .map(v => findGetterMethod(cls.runtimeClass, v.methodName).get)
    .toList

  val setterMethods = gettersAndSetters.getOrElse(PropAccessKind.Setter, Nil)
    // TODO: refactor to avoid finding it twice
    .map(v => findSetterMethod(cls.runtimeClass, v.methodName, propType))
    .filter(_.isDefined)
    .map(_.get)
    .toList

  Option(BeanProperty (
    name = propName,
    propertyType = propType,
    ownerClass = cls.runtimeClass,
    javaGetMethods = getterMethods,
    javaSetMethods= setterMethods,
    owner = cls,
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
      println(s"WARN probably method/methods ${cls.getName}.$methodName has incorrect type.")
      // I hope that problem is connected with using generics or something like that
      // but really everything will work fine.
      Option(methodsWithTheSameName.head)

extension (member: _ClassMember)
  def toBeanPropertyName: String = member match
      case f: _Field => f.name
      case m: _Method => m.name match
        case getName if getName.startsWith("get") => getName.stripPrefix("get").capitalize
        case setName if setName.startsWith("set") => setName.stripPrefix("set").uncapitalize
        case scalaSetterName if scalaSetterName.endsWith("_=") => scalaSetterName.stripSuffix("_=")
        case scalaSetterName if scalaSetterName.endsWith("_$eq") => scalaSetterName.stripSuffix("_$eq")
        case other => other


extension (s: String)
  def uncapitalize: String =
    if s.isNull || s.isEmpty || !s.charAt(0).isUpper then s
    else s"${s.charAt(0).toUpper}${s.substring(1)}"


extension (m: JavaMethod)
  def firstParamType: Class[?] =
    require(m.getParameterCount >= 1)
    m.getParameterTypes.nnArray(0)
