package com.mvv.utils


extension [T](x: T | Null)
  inline def nn: T =
    assert(x != null)
    x.asInstanceOf[T]

