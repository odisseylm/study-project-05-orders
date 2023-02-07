package org.mvv.mapstruct.scala

import scala.annotation.targetName


inline def checkNotNull[T](v: T|Null, msg: =>String): T =
  if (v == null) throw IllegalStateException(msg)
  v.asInstanceOf[T]

inline def requireNotNull[T](v: T|Null, msg: =>String): T =
  require(v != null, msg)
  v.asInstanceOf[T]


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

extension [T](v: Array[T|Null]|Null)
  @targetName("castArrayToNonNullable")
  //noinspection ScalaUnusedSymbol
  inline def nnArray: Array[T] = v.asInstanceOf[Array[T]]

extension [T](v: List[T|Null]|Null)
  @targetName("castArrayToNonNullable")
  //noinspection ScalaUnusedSymbol
  inline def nnList: java.util.List[T] = v.asInstanceOf[java.util.List[T]]
