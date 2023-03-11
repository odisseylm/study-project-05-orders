package org.mvv.scala.tools.quotes

import scala.quoted.Quotes
import org.mvv.scala.tools.isOneOf



extension (using q: Quotes)(typeRepr: q.reflect.TypeRepr)
  def isBool: Boolean =
    import q.reflect.TypeRepr
    //noinspection ScalaUnusedSymbol
    given CanEqual[TypeRepr, TypeRepr] = CanEqual.derived

    val scalaBoolean = TypeRepr.of[Boolean]
    val javaBoolean  = TypeRepr.of[java.lang.Boolean]

    (typeRepr == scalaBoolean) || (typeRepr =:= scalaBoolean)
      || (typeRepr == javaBoolean) || (typeRepr =:= javaBoolean)

  def isNullType: Boolean =
    import q.reflect.TypeRepr
    //noinspection ScalaUnusedSymbol
    given CanEqual[TypeRepr, TypeRepr] = CanEqual.derived

    val scalaNullType = TypeRepr.of[Null]
    (typeRepr == scalaNullType) || (typeRepr =:= scalaNullType)



extension (typeName: String)
  def isTuple2Type: Boolean =
    typeName.isOneOf("Tuple2", "scala.Tuple2")

  def isNullType: Boolean =
    typeName.isOneOf("Null", "scala.Null")
