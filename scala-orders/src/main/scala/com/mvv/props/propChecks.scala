package com.mvv.props

import scala.annotation.targetName
//
import org.mvv.scala.tools.props.{ NamedValue, UninitializedPropertyAccessException }
import com.mvv.nullables.{ isNull, isNotNull, isNotNone }
import com.mvv.utils.afterLastOrOrigin



extension (inline v: Any|Null)
  inline def safeValue: Any|Null =
    // If prop is uninitialized LateInit instance it will throw UninitializedPropertyAccessException by itself
    // For that reason lateInitToValueOrNull is used.
    try v catch case _: UninitializedPropertyAccessException => null
  @targetName("isPropValueInitialized")
  inline def isPropInitialized: Boolean =
    val _safeValue = v.safeValue
    _safeValue.isNotNull && _safeValue.isNotNone


//extension (inline option: Option[?])
//  @targetName("isOptionPropInitialized")
//  inline def isPropInitialized: Boolean =
//    // If prop is uninitialized LateInit instance it will throw UninitializedPropertyAccessException by itself
//    // For that reason lateInitToValueOrNull is used.
//    val safeValue =
//      try { if option.nonEmpty then option.get else null }
//      catch case _: UninitializedPropertyAccessException => null
//    safeValue.isNotNull && safeValue != None


extension [T](inline prop: NamedValue[T])
  inline def safeValue: Any | Null =
    // If prop is uninitialized LateInit instance it will throw UninitializedPropertyAccessException by itself
    // For that reason lateInitToValueOrNull is used.
    val _safeValue = try prop.value catch case _: UninitializedPropertyAccessException => null
    _safeValue

  @targetName("isNamedPropInitialized")
  inline def isPropInitialized: Boolean =
    val _safeValue = prop.safeValue
    _safeValue.isNotNull && _safeValue.isNotNone



@targetName("checkPropValueInitialized")
def checkPropInitialized[T](prop: =>T, msg: =>String): Unit =
  if !prop.isPropInitialized then
    throw UninitializedPropertyAccessException(msg)



//noinspection NoTailRecursionAnnotation // there is no recursion
def checkPropInitialized(prop: =>NamedValue[Any]): Unit =
  checkPropInitialized(prop, s"Property [${prop.name}] is not initialized.")
def checkPropInitialized(prop: =>NamedValue[Any], msg: =>String): Unit =
  if !prop.isPropInitialized then
    throw UninitializedPropertyAccessException(msg)


//noinspection NoTailRecursionAnnotation // there is no recursion
//@targetName("checkPropOptionInitialized")
//def checkPropInitialized(prop: =>NamedValue[Option[?]]): Unit =
//  checkPropInitialized[T](prop, s"Property [${prop.name}] is not initialized.")
//@targetName("checkOptionPropertyInitialized")
//def checkPropInitialized(prop: =>NamedValue[Option[?]], msg: =>String): Unit =
//  if !prop.isPropInitialized then
//    throw UninitializedPropertyAccessException(msg)
