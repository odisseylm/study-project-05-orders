package com.mvv.utils

import scala.annotation.nowarn


@nowarn("msg=cannot be checked at runtime")
inline def equalImpl[T <: Equals](thisV: T, other: Any|Null)(inline comparing: (T, T)=>Boolean): Boolean =
  if other == null || !other.isInstanceOf[T] then false
  else comparing(thisV, other.asInstanceOf[T])
