package org.mvv.scala.quotes

import org.mvv.scala.mapstruct.isOneOf


extension (typeName: String)
  def isTuple2Type: Boolean =
    typeName.isOneOf("Tuple2", "scala.Tuple2")

