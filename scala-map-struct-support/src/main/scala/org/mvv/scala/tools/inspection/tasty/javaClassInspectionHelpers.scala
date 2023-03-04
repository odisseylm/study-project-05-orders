package org.mvv.scala.tools.inspection.tasty

import scala.annotation.targetName
import scala.collection.mutable
//
import java.lang.reflect.{ Field, Member, Method }
//
import org.mvv.scala.tools.{ isNotNull, nnArray }
import org.mvv.scala.tools.inspection.tasty.ClassSource.classKind
import org.mvv.scala.tools.inspection.{ _Modifier, _Visibility }
import org.mvv.scala.tools.inspection.{ _Field, _Method, _Type }
import org.mvv.scala.tools.inspection._Type.{ isBool, isVoid }
import org.mvv.scala.tools.inspection.tasty.loadClass



def visibilityFromModifiers(modifiers: Int): _Visibility =
  import java.lang.reflect.Modifier
  modifiers match
    case mod if Modifier.isPublic(mod)    => _Visibility.Public
    case mod if Modifier.isPrivate(mod)   => _Visibility.Private
    case mod if Modifier.isProtected(mod) => _Visibility.Protected
    case _ => _Visibility.Package


def visibilityOf(f: Field): _Visibility = visibilityFromModifiers(f.getModifiers)
def visibilityOf(m: Method): _Visibility = visibilityFromModifiers(m.getModifiers)


def fieldModifiers(field: Field): Set[_Modifier] =
  generalModifiers(field)

def methodModifiers(m: Method): Set[_Modifier] =
  import JavaInspectionHelper.nameHasPrefix

  var mod: Set[_Modifier] = generalModifiers(m)
  val paramCount = m.getParameterCount
  val returnType = m.getReturnType.nn

  val isGetAccessor = m.nameHasPrefix("get") && paramCount == 0 && !returnType.isVoid
  val isIsAccessor  = m.nameHasPrefix("is")  && paramCount == 0 && returnType.isBool
  val isSetAccessor = m.nameHasPrefix("set") && paramCount == 1 && returnType.isVoid

  if isGetAccessor || isIsAccessor || isSetAccessor then mod += _Modifier.JavaPropertyAccessor
  mod


object JavaInspectionHelper :
  extension (m: Method)
    @targetName("toMethodFromReflection")
    def toMethod: _Method =
      import scala.language.unsafeNulls
      val paramTypes = m.getParameterTypes.map(_.getName).map(_Type(_)).toList
      _Method(m.getName, visibilityOf(m), methodModifiers(m), _Type(m.getReturnType.getName), paramTypes, false)(m)

    def nameHasPrefix(prefix: String): Boolean =
      val methodName = m.getName.nn
      methodName.startsWith(prefix) && methodName.length > prefix.length


  extension (f: Field)
    @targetName("toFieldFromReflection")
    def toField: _Field =
      _Field(f.getName.nn, visibilityOf(f), fieldModifiers(f), _Type(f.getType.nn.getName.nn))(f)


private def generalModifiers(member: java.lang.reflect.Member): Set[_Modifier] =
  import java.lang.reflect.Modifier
  member.getModifiers match
    case m if Modifier.isStatic(m) => Set(_Modifier.Static)
    case _ => Set()



// T O D O: try to rewrite using recursion
private def getClassesAndInterfacesImpl(cls: Class[?], interfaces: Array[Class[?]]): List[Class[?]] =
  val all = mutable.ArrayBuffer[Class[?]]()
  import scala.language.unsafeNulls
  var c: Class[?]|Null = cls
  while c != null && c != classOf[Object] && c != classOf[Any] && c != classOf[AnyRef] do
    all.addOne(cls)
    c = c.getSuperclass.nn

  interfaces.nn.foreach { i => all.addOne(i.nn) }
  all.distinct.toList


def getAllSubClassesAndInterfaces(cls: Class[?]): List[Class[?]] =
  import scala.language.unsafeNulls
  getClassesAndInterfacesImpl(cls.getSuperclass, cls.getInterfaces): List[Class[?]]


val StandardTypes = Set(
  "byte", "Byte", "java.lang.Byte",
  "char", "Character", "java.lang.Character",
  "short", "Short", "java.lang.Short",
  "int", "Integer", "java.lang.Integer",
  "long", "Long", "java.lang.Long",
  "String", "java.lang.String",
)

def typeExists(_type: _Type): Boolean =
  if StandardTypes.contains(_type.runtimeTypeName) then return true
  try { loadClass(_type.runtimeTypeName); true }
  catch case _: Exception => false


def findJavaField(cls: Class[?], name: String): Option[java.lang.reflect.Field] =
  try return Option(cls.getField(name).nn) catch case _: Exception => { }

  var f: Option[java.lang.reflect.Field] = None
  var c: Class[?]|Null = cls
  while f.isEmpty && c.isNotNull && c != classOf[Object] && c != classOf[AnyRef] do
    f = try Option(c.nn.getDeclaredField(name).nn) catch case _: Exception => None
    c = c.nn.getSuperclass
  f


def findJavaMethod(cls: Class[?], name: String): Option[java.lang.reflect.Method] =
  findJavaMethodImpl(cls, name) .orElse( findJavaMethodImpl(cls, name.toJavaMethodName))


private def findJavaMethodImpl(cls: Class[?], name: String): Option[java.lang.reflect.Method] =
  try return Option(cls.getMethod(name).nn) catch case _: Exception => { }

  var m: Option[java.lang.reflect.Method] = None
  var c: Class[?]|Null = cls
  while m.isEmpty && c.isNotNull && c != classOf[Object] && c != classOf[AnyRef] do
    m = try Option(c.nn.getDeclaredMethod(name).nn) catch case _: Exception => None
    c = c.nn.getSuperclass
  m


def findJavaMethodWithOneParam(cls: Class[?], name: String): Option[java.lang.reflect.Method] =
  findJavaMethodWithOneParamImpl(cls, name)
    .orElse(findJavaMethodWithOneParamImpl(cls, name.toJavaMethodName))

private def findJavaMethodWithOneParamImpl(cls: Class[?], name: String): Option[java.lang.reflect.Method] =
  var m = findMethodWithOneParamFrom(cls.getMethods, name)
  if m.isDefined then return m

  var c: Class[?]|Null = cls
  while m.isEmpty && c.isNotNull && c != classOf[Object] && c != classOf[AnyRef] do
    m = findMethodWithOneParamFrom(cls.getDeclaredMethods, name)
    c = c.nn.getSuperclass
  m


private def findMethodWithOneParamFrom(methods: Array[java.lang.reflect.Method|Null]|Null, methodName: String): Option[java.lang.reflect.Method] =
  val methodsWithOneParam = methods.nnArray.iterator
    .filter(m => m.getParameterCount == 1)
    .filter(_.getName == methodName)
    .toList
  if methodsWithOneParam.length == 1
  then Option(methodsWithOneParam.head)
  // Now I don't know how to choose one of several methods
  else None


extension (methodName: String)
  def toJavaMethodName: String = methodName.replace("_=", "_$eq").nn
