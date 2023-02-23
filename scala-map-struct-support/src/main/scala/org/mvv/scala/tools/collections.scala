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


  extension[T] (el: Iterator[T])
    inline def filterByType[V]: Iterator[V] =
      el.filter(_.isInstanceOf[V]).map(_.asInstanceOf[V])
    inline def findByType[V]: Option[V] =
      el.filterByType[V].find(_ => true)

  extension[T] (el: Iterable[T])
    inline def filterByType[V]: Iterable[V] =
      el.filter(_.isInstanceOf[V]).map(_.asInstanceOf[V])
    inline def findByType[V]: Option[V] =
      el.filterByType[V].find(_ => true)
    // In java style, without any ugly prefixes like List, so on
    def asString: String = el.mkString("[", ", ", "]")
