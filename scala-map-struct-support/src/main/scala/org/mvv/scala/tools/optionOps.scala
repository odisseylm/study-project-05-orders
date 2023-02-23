package org.mvv.scala.tools


//noinspection ScalaFileName
object OptionOps :
  inline def allAreDefined(option: Option[?], other: Option[?]*): Boolean =
    option.isDefined && other.forall(_.isDefined)


  //extension [T](option: Option[T])
  //  // TODO: would be nice to name it as filterOptionByType but there is ambiguous conlicts with similar fro Iterator
  //  inline def filterOptionByType[V]: Option[V] =
  //    option.filter(_.isInstanceOf[V]).map(_.asInstanceOf[V])
