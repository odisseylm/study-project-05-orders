package com.mvv.scala.temp.tests.tasty

import java.nio.file.{Files, Path}
import scala.annotation.nowarn


extension [T](v: T|Null|Option[T])
  @nowarn @unchecked //noinspection IsInstanceOf
  def unwrapOption: T|Null =
    if v.isInstanceOf[Option[T]] then v.asInstanceOf[Option[T]].orNull else v.asInstanceOf[T|Null]

  inline def isListNullOrEmpty(list: List[?]|Null): Boolean =
    import scala.language.unsafeNulls
    list == null || list.isEmpty


extension (v: AnyRef|Null)
  inline def isNull: Boolean =
    import scala.language.unsafeNulls
    //val asRaw = v.asInstanceOf[AnyRef]
    //asRaw == null
    v.asInstanceOf[AnyRef] == null

  inline def isNotNull: Boolean =
    import scala.language.unsafeNulls
    //val asRaw = v.asInstanceOf[AnyRef]
    //asRaw != null
    v.asInstanceOf[AnyRef] != null


extension [T](v: T|Null)
  //noinspection ScalaUnusedSymbol
  inline def castToNonNullable: T = v.asInstanceOf[T]


extension (s: String)
  def replaceSuffix(oldSuffix: String, newSuffix: String): String =
    if s.endsWith(oldSuffix) then s.stripSuffix(oldSuffix) + newSuffix else s


inline def fileExists(f: String) = Files.exists(Path.of(f))


inline def checkNotNull[T](v: T|Null, msg: =>String): T =
  if (v == null) throw IllegalStateException(msg)
  v.asInstanceOf[T]

inline def requireNotNull[T](v: T|Null, msg: =>String): T =
  require(v != null, msg)
  v.asInstanceOf[T]


@nowarn("msg=cannot be checked at runtime")
inline def equalImpl[T <: Equals](thisV: T, other: Any|Null)(inline comparing: (T, T)=>Boolean): Boolean =
  import scala.language.unsafeNulls
  if other == null || !other.isInstanceOf[T] then false
    else comparing(thisV, other.asInstanceOf[T])


object CollectionsOps :
  extension [T](collection: scala.collection.Set[T])
    def containsOneOf(v: T, values: T*): Boolean =
      if collection.contains(v) then return true
      values.exists(vv => collection.contains(vv))

  extension [A, CC[_], C](collection: scala.collection.SeqOps[A,CC,C])
    def contains[A1 >: A](v: A1, values: A1*): Boolean =
      if collection.contains(v) then return true
      values.exists(vv => collection.contains(vv))
