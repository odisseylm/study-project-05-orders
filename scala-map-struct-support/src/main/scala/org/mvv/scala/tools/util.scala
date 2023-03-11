package org.mvv.scala.tools

import java.nio.file.{Files, Path}
import scala.annotation.{nowarn, targetName}


inline def equalImpl[T <: Equals](thisV: T, other: Any|Null)(inline comparing: (T, T)=>Boolean): Boolean =
  import scala.language.unsafeNulls
  if other.isNull || !other.isInstanceOf[T] then false
    else comparing(thisV, other.asInstanceOf[T])


// use it only for hacking approach when any unexpected error can happen
def tryDo[T](expr: => T): Option[T] =
  try Option[T](expr).nn catch case _: Throwable => None


extension [T](v: T)
  inline def isOneOf(values: T*): Boolean =
    values.contains(v)

extension [T](l: List[T])
  def isSingleItemList(v: T): Boolean =
    //noinspection ScalaUnusedSymbol
    given CanEqual[T, T] = CanEqual.derived
    l.sizeIs == 1 && l.head == v

extension [T](x: T|Null)
  inline def !! : T = nn(x)
  inline def ifNull(action: =>T): T =
    import org.mvv.scala.tools.AnyCanEqualGivens.given
    if x == null then action else x
