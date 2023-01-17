package com.mvv.utils

import org.apache.commons.lang3.StringUtils

import scala.language.unsafeNulls


//extension [T](x: T | Null)
//  inline def nn: T =
//    val asRaw = x.asInstanceOf[AnyRef]
//    assert(asRaw != null)
//    x.asInstanceOf[T]

// TODO: convert to extensions
def isNull(v: Any|Null): Boolean =
  val asRaw = v.asInstanceOf[AnyRef]
  asRaw == null

def isNotNull(v: Any|Null): Boolean =
  val asRaw = v.asInstanceOf[AnyRef]
  asRaw != null


def isNullOrEmpty(string: CharSequence|Null): Boolean =
  val asRawString = string.asInstanceOf[CharSequence]
  asRawString == null && asRawString.isEmpty
//def isNotNullOrEmpty(string: String|Null): Boolean = !isNullOrEmpty(string)


def isNullOrBlank(string: CharSequence|Null): Boolean = StringUtils.isBlank(string)
//def isNotNullOrBlank(string: String|Null): Boolean = !isBlank(string)

extension [T](x: T|Null)
  inline def !! : T = nn(x)
  inline def ifNull(action: =>T): T =
    if x == null then action else x
