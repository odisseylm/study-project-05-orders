package org.mvv.scala.tools

import scala.annotation.targetName


inline def checkNotNull[T](v: T|Null, msg: =>String): T =
  if (v == null) throw IllegalStateException(msg)
  v.asInstanceOf[T]

inline def requireNotNull[T](v: T|Null, msg: =>String): T =
  require(v != null, msg)
  v.asInstanceOf[T]


extension [T](v: T|Null)
  inline def isNull: Boolean =
    import scala.language.unsafeNulls
    v == null

  inline def isNotNull: Boolean =
    import scala.language.unsafeNulls
    v != null

  //noinspection ScalaUnusedSymbol
  inline def castToNonNullable: T = v.asInstanceOf[T]
  inline def nnIgnore: T = v.asInstanceOf[T]

extension [T](v: Array[T|Null]|Null)
  @targetName("castArrayToNonNullable")
  //noinspection ScalaUnusedSymbol
  inline def nnArray: Array[T] = v.nn.asInstanceOf[Array[T]]

extension [T](v: Array[T]|Null)
  @targetName("castArrayToNonNullableAlt")
  //noinspection ScalaUnusedSymbol
  inline def nnArray: Array[T] = v.nn.asInstanceOf[Array[T]]


extension [T](v: List[T|Null]|Null)
  @targetName("castArrayToNonNullable")
  //noinspection ScalaUnusedSymbol
  inline def nnList: java.util.List[T] = v.nn.asInstanceOf[java.util.List[T]]

extension [T](v: List[T]|Null)
  @targetName("castArrayToNonNullableAlt")
  //noinspection ScalaUnusedSymbol
  inline def nnList: java.util.List[T] = v.nn.asInstanceOf[java.util.List[T]]
