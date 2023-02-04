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

