package com.mvv.utils

import org.apache.commons.lang3.StringUtils

import scala.language.unsafeNulls


//extension [T](x: T | Null)
//  inline def nn: T =
//    val asRaw = x.asInstanceOf[AnyRef]
//    assert(asRaw != null)
//    x.asInstanceOf[T]


//extension (v: AnyRef|Null)
extension [T](v: T|Null)
  inline def isNull: Boolean = v.asInstanceOf[AnyRef] == null
  inline def isNotNull: Boolean = v.asInstanceOf[AnyRef] != null


extension [T](x: T|Null)
  inline def !! : T = nn(x)
  inline def ifNull(action: =>T): T =
    if x == null then action else x
