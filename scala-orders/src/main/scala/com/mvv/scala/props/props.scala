package com.mvv.scala.props

import org.mvv.scala.tools.props.{NamedValue, UninitializedPropertyAccessException}
import com.mvv.nullables.isNull
import com.mvv.utils.afterLastOrOrigin

import scala.annotation.targetName
import scala.reflect.ClassTag

/*
private val internalMethods = Set("getClass", "hashCode", "toString")
private val internalTypes: Set[Class[?]] = Set(classOf[com.mvv.log.Logger], classOf[org.slf4j.Logger])

def collectRequiredProperties[T](interfaceOrTrait: Class[T]): List[String] = {
  import com.mvv.nullables.AnyCanEqualGivens.given

  val methods = interfaceOrTrait.getMethods.nnArray

  methods.nn
    .toSeq
    //.map( _.nn )
    .filter(m => /*m != null*/ && !m.isSynthetic && !m.isBridge && !m.isDefault
      && m.getParameterCount == 0 && m.getReturnType != Void.TYPE && m.getReturnType != classOf[Unit])
    .filter( method => !internalMethods.contains(method.getName.nn) && !internalTypes.contains(method.getReturnType.nn) )
    .map( _.getName.nn )
    .filter( !_.contains('$'))
    .map( _.removePrefix("_") )
    .map( methodName => if methodName.startsWith("get") then methodName.removePrefix("get").uncapitalize else methodName )
    .distinct
    .toList
}


def checkRequiredPropsAreInitialized[T](obj: T)(implicit classTag: ClassTag[T]): Unit = {
  import com.mvv.nullables.AnyCanEqualGivens.given
  val requiredPropNames: List[String] = collectRequiredProperties(obj.getClass)
  val notInitializedProps = requiredPropNames
    .filter( obj.getClass.getMethod(_).nn.invoke(obj) == null )

  check(notInitializedProps.isEmpty, s"The following props are not initialized $notInitializedProps.")
}


case class BeanProp[T](value: T, propName: String = "")
*/


// TODO: add support of Option too



def checkRequiredPropsAreInitialized[T](obj: T, isInitProps: List[(String, () => Boolean)])(implicit classTag: ClassTag[T]): Unit =
  val uninitializedPropNames: Seq[String] = isInitProps.filter(!_._2()).map(_._1).sorted
  if uninitializedPropNames.nonEmpty then
    val propsNamesStr = uninitializedPropNames.mkString(", ")
    val simpleClassName = classTag.runtimeClass.getSimpleName.nn
    throw UninitializedPropertyAccessException(s"$simpleClassName has uninitialized properties [$propsNamesStr].")


extension (prop: NamedValue[?])
  def simpleName: String = prop.name.afterLastOrOrigin(".")
