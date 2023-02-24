package org.mvv.scala.tools.quotes

import scala.quoted.*
//
import org.mvv.scala.tools.afterLastOr


case class Param[T] (paramName: String, paramType: T)



/** Returns tuple with method owner (package/class) and simple method name */
//noinspection DuplicatedCode
def extractFullMethodNameComponents (fullMethodName: String): (String, String) =
  val index = fullMethodName.lastIndexOf('.')
  if index == -1 then ("", fullMethodName)
  else (fullMethodName.substring(0, index).nn, fullMethodName.substring(index + 1).nn)



def qMethodType(using q: Quotes)
  (params: Param[q.reflect.TypeRepr]*)
  (returnType: q.reflect.TypeRepr)
  : q.reflect.MethodType =

  val paramNames = params.map(_.paramName).toList
  val paramTypes = params.map(_.paramType).toList

  val methodType = q.reflect.MethodType(paramNames) (
    _ => paramTypes,
    _ => returnType)
  methodType



// for global functions (not methods)
def qFunction(using q: Quotes)
  (functionFullName: String)
  (params: Param[q.reflect.TypeRepr]*)
  (returnType: q.reflect.TypeRepr)
  : q.reflect.Term =
  import q.reflect.{ Select, Symbol }

  val methodType = qMethodType(params*)(returnType)
  val (methodOwner, methodSimpleName) = extractFullMethodNameComponents(functionFullName)
  val funSelect = Select( qClassName(methodOwner), Symbol.newMethod(Symbol.noSymbol, methodSimpleName, methodType) )
  funSelect






