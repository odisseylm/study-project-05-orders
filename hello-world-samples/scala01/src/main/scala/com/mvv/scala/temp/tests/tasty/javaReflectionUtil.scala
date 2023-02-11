package com.mvv.scala.temp.tests.tasty

import scala.collection.mutable


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
  if StandardTypes.contains(_type.className) then return true
  try { loadClass(_type.className); true }
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
  findJavaMethodImpl(cls, name) .orElse( findJavaMethodImpl(cls, scalaMethodNameToJava(name)))

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
    .orElse(findJavaMethodWithOneParamImpl(cls, scalaMethodNameToJava(name)))

private def findJavaMethodWithOneParamImpl(cls: Class[?], name: String): Option[java.lang.reflect.Method] =
  var m = findMethodWithOneParamFrom(cls.getMethods, name)
  if m.isDefined then return m

  var c: Class[?] | Null = cls
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


def scalaMethodNameToJava(methodName: String): String =
  methodName.replace("_=", "_$eq").nn

