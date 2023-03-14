package com.mvv.utils

import scala.annotation.nowarn


@nowarn("msg=cannot be checked at runtime")
inline def equalImpl[T <: Equals](thisV: T, other: Any|Null)(inline comparing: (T, T)=>Boolean): Boolean =
  import com.mvv.nullables.AnyCanEqualGivens.given
  if other == null || !other.isInstanceOf[T] then false
  else comparing(thisV, other.asInstanceOf[T])


extension [T](v: T)
  inline def isOneOf(values: T*): Boolean =
    values.contains(v)
  inline def isNotOneOf(values: T*): Boolean =
    !v.isOneOf(values*)
