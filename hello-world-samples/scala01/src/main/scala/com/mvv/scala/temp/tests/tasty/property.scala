package com.mvv.scala.temp.tests.tasty

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
  // for MapStruct
  //val beanPropsByMethod: Map[PropAccessKind, BeanProperty],
  )

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

    //val beanProps: Map[String, BeanProperty] = props.map(vv => (vv._1, toBeanProperty(vv))).toMap
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
  BeanPropAccessMethod(member.name, PropAccessKind.Getter, method.getName.nn, method.getReturnType.nn)

private def setterBeanPropAccessMethod(m_m: (_ClassMember, Option[JavaMethod]) ): BeanPropAccessMethod =
  val member = m_m._1;  val method = m_m._2.get
  BeanPropAccessMethod(member.name, PropAccessKind.Setter, method.getName.nn, method.getParameterTypes.nnArray(0).nn)

private def findGetterMethod(cls: Class[?], methodName: String): Option[JavaMethod] =
  try { Option(cls.getMethod(methodName).nn) } catch case _: Exception => None

// actually there can be several overloaded methods...
// for that reason we really use type of getters as property type
private def findSetterMethod(cls: Class[?], methodName: String): Option[JavaMethod] =
  val methods: Array[JavaMethod] = cls.getMethods.nnArray
  methods.find(m => m.getName.nn == methodName && m.getParameterCount == 1)

private def findSetterMethod(cls: Class[?], methodName: String, propType: Class[?]): Option[JavaMethod] =
  val methods: Array[JavaMethod] = cls.getMethods.nnArray
  methods.view
    .filter(m => m.getName.nn == methodName && m.getParameterCount == 1)
    .find { m =>
      val firstParamType: Class[?] = m.getParameterTypes.nnArray(0)
      firstParamType.isAssignableFrom(firstParamType)
    }

extension (t: _Type)
  def toClass: Class[?] = loadClass(t.runtimeTypeName.getOrElse(t.declaredTypeName))