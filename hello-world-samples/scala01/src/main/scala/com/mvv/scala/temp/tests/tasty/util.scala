package com.mvv.scala.temp.tests.tasty

import java.nio.file.{Files, Path}
import scala.annotation.{nowarn, targetName}


@nowarn("msg=cannot be checked at runtime")
inline def equalImpl[T <: Equals](thisV: T, other: Any|Null)(inline comparing: (T, T)=>Boolean): Boolean =
  import scala.language.unsafeNulls
  if other == null || !other.isInstanceOf[T] then false
    else comparing(thisV, other.asInstanceOf[T])


def tryDo[T](expr: => T): Option[T] =
  try Option[T](expr).nn catch case _: Exception => None


extension [T](v :T)
  inline def isOneOf(values: T*): Boolean =
    values.contains(v)