package org.mvv.scala.tools.quotes

import org.mvv.scala.tools.isOneOf


extension (typeName: String)
  def isTuple2Type: Boolean =
    typeName.isOneOf("Tuple2", "scala.Tuple2")

