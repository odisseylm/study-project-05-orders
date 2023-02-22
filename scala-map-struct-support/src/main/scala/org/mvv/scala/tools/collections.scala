package org.mvv.scala.tools


//noinspection ScalaFileName
object CollectionsOps :
  extension [T](collection: scala.collection.Set[T])
    def containsOneOf(v: T, values: T*): Boolean =
      if collection.contains(v) then return true
      values.exists(vv => collection.contains(vv))

  extension [A, CC[_], C](collection: scala.collection.SeqOps[A,CC,C])
    def containsOnOf[A1 >: A](v: A1, values: A1*): Boolean =
      if collection.contains(v) then return true
      values.exists(vv => collection.contains(vv))

  extension[T] (v: T|Null|Option[T])
    inline def isListNullOrEmpty(list: List[?] | Null): Boolean =
      import scala.language.unsafeNulls
      list == null || list.isEmpty

  //extension[T] (collection: scala.collection.List[T])
  //  def containsOneOf(v: T, values: T*): Boolean =
  //    if collection.contains(v) then return true
  //    values.exists(vv => collection.contains(vv))

