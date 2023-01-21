package com.mvv.scala.temp.tests.reflection

import scala.reflect.ClassTag
//import scala.reflect.runtime.universe.TypeTag
import scala.reflect.Selectable
import scala.reflect.NameTransformer
import scala.reflect.ScalaSignature
import scala.reflect.Enum

//import scala.language.implicitConversions
//import scala.language.reflectiveCalls
//import scala.language.dynamics
//import scala.language.existentials
//import scala.language.experimental.macros


/*
def checkLateInitPropsAreInitialized[T](obj: T)(implicit classTag: ClassTag[T]): Unit = {
  classTag.members
    .collect { case m: MethodSymbol if m.isCaseAccessor => m }.toList
}
*/

//noinspection ScalaUnusedSymbol
def aaa(): Unit = {
  //scala.reflect.runtime.
}


/*
import scala.reflect.runtime.universe.*

def classAccessors[T: TypeTag]: List[MethodSymbol] =
  typeOf[T].members.collect { case m: MethodSymbol if m.isCaseAccessor => m }.toList


Starting Scala 2.13, case classes (which are an implementation of Product)
 are now provided with a productElementNames method which returns an iterator over their field's names.


Following Andrey Tyukin solution, to get only the list of fields in Scala 2.12:
val fields: List[String] = classOf[Dummy].getDeclaredFields.map(_.getName).toList


If you are using Spark, this the easiest way to get fields:
val cols = Seq(CaseClassModel()).toDF().columns


def getMethods[T: TypeTag] = typeOf[T].members.collect {
  case m: MethodSymbol if m.isCaseAccessor => m
}.toList


import scala.collection.immutable.ListMap
import scala.reflect.runtime.universe._

/**
  * Returns a map from formal parameter names to types, containing one
  * mapping for each constructor argument.  The resulting map (a ListMap)
  * preserves the order of the primary constructor's parameter list.
  */
def caseClassParamsOf[T: TypeTag]: ListMap[String, Type] = {
  val tpe = typeOf[T]
  val constructorSymbol = tpe.decl(termNames.CONSTRUCTOR)
  val defaultConstructor =
    if (constructorSymbol.isMethod) constructorSymbol.asMethod
    else {
      val ctors = constructorSymbol.asTerm.alternatives
      ctors.map(_.asMethod).find(_.isPrimaryConstructor).get
    }

  ListMap[String, Type]() ++ defaultConstructor.paramLists.reduceLeft(_ ++ _).map {
    sym => sym.name.toString -> tpe.member(sym.name).asMethod.returnType
  }
}

*/
