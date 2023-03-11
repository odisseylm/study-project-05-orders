package org.mvv.scala.tools


object AnyCanEqualGivens extends NullableCanEqualGivens[Any]
object AnyRefCanEqualGivens extends NullableCanEqualGivens[AnyRef]


object CharSequenceCanEqualGivens extends NullableCanEqualGivens[java.lang.CharSequence]
object StringCanEqualGivens extends NullableCanEqualGivens[java.lang.String]
object ClassCanEqualGivens extends NullableCanEqualGivens[java.lang.Class[?]]
object ClassLoaderCanEqualGivens extends NullableCanEqualGivens[java.lang.ClassLoader]
