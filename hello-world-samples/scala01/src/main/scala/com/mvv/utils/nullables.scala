package com.mvv.utils

import scala.language.unsafeNulls


extension [T](x: T | Null)
  inline def nn: T =
    val asRaw = x.asInstanceOf[AnyRef]
    assert(asRaw != null)
    x.asInstanceOf[T]

def isNull(v: AnyRef|Null): Boolean =
  val asRaw = v.asInstanceOf[AnyRef]
  asRaw == null

def isNotNull(v: AnyRef|Null): Boolean =
  val asRaw = v.asInstanceOf[AnyRef]
  asRaw != null

//def toNotNull[T](v: T|Null): T =
//  val asRaw = v.asInstanceOf[AnyRef]
//  asRaw.nn

