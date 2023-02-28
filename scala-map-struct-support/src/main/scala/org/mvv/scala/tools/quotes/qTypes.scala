package org.mvv.scala.tools.quotes

import scala.quoted.Quotes


extension (using q: Quotes)(typeRepr: q.reflect.TypeRepr)
  def isBool: Boolean =
    import q.reflect.TypeRepr
    val scalaBoolean = TypeRepr.of[Boolean]
    val javaBoolean  = TypeRepr.of[java.lang.Boolean]

    (typeRepr == scalaBoolean) || (typeRepr =:= scalaBoolean)
      || (typeRepr == javaBoolean) || (typeRepr =:= javaBoolean)
