package org.mvv.scala.tools



inline def allAreDefined(option: Option[?], other: Option[?]*): Boolean =
  option.isDefined && other.forall(_.isDefined)

