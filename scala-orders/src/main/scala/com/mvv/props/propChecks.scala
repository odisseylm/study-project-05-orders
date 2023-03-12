package com.mvv.props

import scala.annotation.targetName
//
import org.mvv.scala.tools.props.{ readOnlyProp, NamedValue, UninitializedPropertyAccessException }
import com.mvv.nullables.{ isNull, isNotNull, isNotNone }
import com.mvv.utils.{ isNullOrEmpty, afterLastOrOrigin }



extension (inline v: Any|Null)
  inline def safeValue: Any|Null =
    // If prop is uninitialized LateInit instance it will throw UninitializedPropertyAccessException by itself
    // For that reason lateInitToValueOrNull is used.
    try v catch case _: UninitializedPropertyAccessException => null

  @targetName("isPropValueInitialized")
  inline def isPropInitialized: Boolean =
    val _safeValue = v.safeValue
    _safeValue.isNotNull && _safeValue.isNotNone



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


/** namedValue should be just name of variable or field. */
inline def checkNamedValueInitialized[T](inline namedValue: =>T): Unit =
  checkPropInitialized(readOnlyProp[T](namedValue))


@targetName("checkPropValueInitialized")
def checkPropValueInitialized[T](prop: =>T, msg: =>String): Unit =
  if !prop.isPropInitialized then
    throw UninitializedPropertyAccessException(msg)



//noinspection NoTailRecursionAnnotation // there is no recursion
def checkPropInitialized[T](prop: =>NamedValue[T]): Unit =
  checkPropInitialized(prop, s"Property [${prop.name}] is not initialized.")
def checkPropInitialized[T](prop: =>NamedValue[T], msg: =>String): Unit =
  if !prop.isPropInitialized then
    throw UninitializedPropertyAccessException(msg)



def checkRequiredPropsAreInitialized(propsIsInit: List[(String, () => Boolean)]): Unit =
  checkRequiredPropsAreInitialized("", propsIsInit)

def checkRequiredPropsAreInitialized(ownerName: String, propsIsInit: List[(String, () => Boolean)]): Unit =
  val uninitializedPropNames: List[String] = propsIsInit
    .filter(_._2())
    .map(_._1)
    .sorted

  if uninitializedPropNames.nonEmpty then
    val ownerStr = if !ownerName.isNullOrEmpty then s" of [$ownerName]" else ""
    throw IllegalStateException(s"The following properties$ownerStr are uninitialized [${uninitializedPropNames.mkString(", ")}].")
