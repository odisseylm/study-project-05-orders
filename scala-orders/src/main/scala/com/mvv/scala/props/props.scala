package com.mvv.scala.props

import org.mvv.scala.tools.props.{NamedValue, UninitializedPropertyAccessException}
import com.mvv.nullables.{ isNull, isNotNull }
import com.mvv.utils.afterLastOrOrigin

import scala.annotation.targetName
import scala.reflect.ClassTag



def checkRequiredPropsAreInitialized[T](propsOwner: T, isInitProps: List[(String, () => Boolean)])(implicit classTag: ClassTag[T]): Unit =
  val uninitializedPropNames: Seq[String] = isInitProps.filter(!_._2()).map(_._1).sorted
  if uninitializedPropNames.nonEmpty then
    val propsNamesStr = uninitializedPropNames.mkString(", ")
    val simpleClassName = if propsOwner.isNotNull then propsOwner.getClass.nn.getSimpleName
                          else classTag.runtimeClass.getSimpleName.nn
    throw UninitializedPropertyAccessException(s"$simpleClassName has uninitialized properties [$propsNamesStr].")


extension (prop: NamedValue[?])
  def simpleName: String = prop.name.afterLastOrOrigin(".")
