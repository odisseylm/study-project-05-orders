package com.mvv.scala.temp.tests.tasty

//
import scala.annotation.{nowarn, tailrec}
import scala.compiletime.uninitialized
import scala.collection.mutable
import scala.quoted.*
import scala.tasty.inspector.*
//
import java.nio.file.Path
import java.nio.file.Files
import java.net.URL
import java.lang.reflect.Modifier
import java.lang.reflect.Method as JavaMethod
import java.awt.Image
import java.beans.{BeanDescriptor, BeanInfo, EventSetDescriptor, MethodDescriptor, PropertyDescriptor}
//
import com.mvv.scala.macros.printFields

//private class _BeanProps (val beanType: Any /* TypeRepr[Any] or Type[] ??? */ ) :
//  val map: scala.collection.mutable.Map[String, _Prop] = scala.collection.mutable.HashMap()
//
//
//private class _Prop (val name: String) :
//  var propType: Any = uninitialized // TypeRepr[Any] or Type[] ???
//  var getter: Option[Any] = None
//  var setter: Option[Any] = None

