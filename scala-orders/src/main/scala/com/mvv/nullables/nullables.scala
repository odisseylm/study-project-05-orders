package com.mvv.nullables

import scala.language.unsafeNulls
import scala.annotation.targetName
//
import org.apache.commons.lang3.StringUtils



//extension [T](x: T | Null)
//  inline def nn: T =
//    val asRaw = x.asInstanceOf[AnyRef]
//    assert(asRaw != null)
//    x.asInstanceOf[T]


//extension (v: AnyRef|Null)
extension [T](v: T|Null)
  inline def isNull: Boolean = v.asInstanceOf[AnyRef] == null
  inline def isNotNull: Boolean = v.asInstanceOf[AnyRef] != null
  inline def isNotNone: Boolean =
    given CanEqual[T|Null, Option[?]] = CanEqual.derived
    v != None


extension [T](x: T|Null)
  inline def !! : T = nn(x)
  inline def ifNull(action: =>T): T =
    import com.mvv.nullables.AnyCanEqualGivens.given
    if x == null then action else x


// This method only for the future, possibly
// from https://stackoverflow.com/questions/48713965/scala-how-to-determine-if-a-type-is-nullable
def isNullablePureType[T](arg: T)(implicit sn: Null <:< T = null, sar: T <:< AnyRef = null): Boolean =
  sn != null && sar != null


extension [T](v: Array[T|Null]|Null)
  @targetName("castArrayToNonNullable")
  //noinspection ScalaUnusedSymbol
  inline def nnArray: Array[T] = v.nn.asInstanceOf[Array[T]]

extension [T](v: Array[T]|Null)
  @targetName("castArrayToNonNullableAlt")
  //noinspection ScalaUnusedSymbol
  inline def nnArray: Array[T] = v.nn.asInstanceOf[Array[T]]
