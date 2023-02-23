package org.mvv.scala.tools


//noinspection ScalaFileName
object OptionOps :
  inline def allAreDefined(option: Option[?], other: Option[?]*): Boolean =
    option.isDefined && other.forall(_.isDefined)
