package com.mvv.scala.temp.tests.tasty

import scala.annotation.targetName
import scala.collection.mutable
//
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method
//
import ClassKind.classKind
//import _FieldOps.*
//import _MethodOps.*


class JavaBeansInspectorInternal :
  //private val classesByFullName: mutable.Map[String, _Class] = mutable.HashMap()

  //def classesDescr: Map[String, _Class] = classesByFullName.toMap
  //def classDescr(classFullName: String): Option[_Class] = classesByFullName.get(classFullName)

  //def inspect(klass: Class[?]): _Class = inspect(klass.getName.nn)

  def inspectJavaClass(_cls: Class[?], scalaBeansInspector: ScalaBeansInspector): _Class =
    import ReflectionHelper.*

    val _class: _Class = _Class(
      // TODO: temp
      ClassKind.Java, TempStubClassSource(),
      _cls.getPackageName.nn, _cls.getSimpleName.nn)(scalaBeansInspector)

    val classChain: List[Class[?]] = getAllSubClassesAndInterfaces(_cls)

    val parentClassFullNames = classChain.map(_.getName.nn)
    _class.parentTypeNames = parentClassFullNames.map(_Type(_))

    classChain.foreach { c =>
      if toInspectParentClass(c) then
        _class.parents :+= scalaBeansInspector.inspectClass(c)
    }

    _class.declaredFields = _cls.getDeclaredFields.nn.map { f =>
      val _f = toField(f.nn)
      (_f.toKey, _f) }.toMap
    _class.declaredMethods = _cls.getDeclaredMethods.nn.map { m =>
      val _m = toMethod(m.nn)
      (_m.toKey, _m) }.toMap

    _class
  end inspectJavaClass

end JavaBeansInspectorInternal


private def visibilityFromModifiers(modifiers: Int): _Visibility =
  import java.lang.reflect.Modifier
  modifiers match
    case mod if Modifier.isPublic(mod) => _Visibility.Public
    case mod if Modifier.isPrivate(mod)   => _Visibility.Private
    case mod if Modifier.isProtected(mod) => _Visibility.Protected
    case _ => _Visibility.Package


def visibilityOf(f: Field): _Visibility = visibilityFromModifiers(f.getModifiers)
def visibilityOf(m: Method): _Visibility = visibilityFromModifiers(m.getModifiers)


def fieldModifiers(field: Field): Set[_Modifier] =
  generalModifiers(field)

def methodModifiers(m: Method): Set[_Modifier] =
  //val mod: MutableSet[_Modifier] = scala.collection.mutable.Set.from(generalModifiers(m))
  val mod: mutable.Set[_Modifier] = mutable.Set.from(generalModifiers(m))
  val mName = m.getName.nn
  val paramCount = m.getParameterCount
  val returnType = m.getReturnType

  val isGetAccessor = mName.startsWith("get") && mName.length > 3
    && paramCount == 0
    && returnType != Void.TYPE && returnType != classOf[Unit]

  val isIsAccessor = mName.startsWith("is") && mName.length > 2 && paramCount == 0
    && (returnType == Boolean || returnType == classOf[Boolean]
    || returnType == java.lang.Boolean.TYPE || returnType == classOf[java.lang.Boolean])
  val isSetAccessor = mName.startsWith("set") && mName.length> 3 && paramCount == 1
    && (returnType == Void.TYPE && returnType == classOf[Unit])

  if isGetAccessor || isIsAccessor || isSetAccessor then mod.add(_Modifier.JavaPropertyAccessor)

  mod.toSet
end methodModifiers


object ReflectionHelper :
  extension (m: Method)
    @targetName("toMethodFromReflection")
    def toMethod: _Method =
      val paramTypes = m.getParameterTypes.nn.map(_.nn.getName.nn).map(_Type(_)).toList
      _Method(m.getName.nn, visibilityOf(m), methodModifiers(m), _Type(m.getReturnType.nn.getName.nn), paramTypes, false)(m)


  extension (f: Field)
    @targetName("toFieldFromReflection")
    def toField: _Field =
      _Field(f.getName.nn, visibilityOf(f), fieldModifiers(f), _Type(f.getType.nn.getName.nn))(f)


private def getClassesAndInterfacesImpl(
                                         cls: Class[?], interfaces: Array[Class[?]]): List[Class[?]] =
  val all = mutable.ArrayBuffer[Class[?]]()
  import scala.language.unsafeNulls
  var c: Class[?]|Null = cls
  while c != null && c != classOf[Object] && c != classOf[Any] && c != classOf[AnyRef] do
    all.addOne(cls)
    c = cls.getSuperclass.nn

  interfaces.nn.foreach { i => all.addOne(i.nn) }
  all.distinct.toList


def getAllSubClassesAndInterfaces(cls: Class[?]): List[Class[?]] =
  import scala.language.unsafeNulls
  getClassesAndInterfacesImpl(cls.getSuperclass, cls.getInterfaces): List[Class[?]]


private def generalModifiers(member: java.lang.reflect.Member): Set[_Modifier] =
  import java.lang.reflect.Modifier
  member.getModifiers match
    case m if Modifier.isStatic(m) => Set(_Modifier.Static)
    case _ => Set()


def toInspectParentClass(_class: Class[?]): Boolean =
  _class.classKind match
    case ClassKind.Java => true
    case ClassKind.Scala2 => true
    case ClassKind.Scala3 => getClassLocationUrl(_class).getProtocol != "jar"

