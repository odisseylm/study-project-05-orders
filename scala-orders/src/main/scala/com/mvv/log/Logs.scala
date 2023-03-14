package com.mvv.log

import scala.compiletime.asMatchable
import com.mvv.nullables.isNull

import scala.annotation.targetName



object Logs :
  private val safeLength: Int = 512

  //@JvmStatic
  def trimToSafeString(obj: Any|Null): String|Null =
    import com.mvv.nullables.AnyCanEqualGivens.given
    if obj == null then return null

    val str = obj.toString
    if str.length < safeLength then str
    else s"${str.substring(0, safeLength)} ..."


  // version for java
  extension (obj: Any|Null)
    def safe: String|Null = Logs.trimToSafeString(obj)


extension (obj: Any|Null)
  def safe: String|Null = Logs.trimToSafeString(obj)

extension [T](obj: T|Null)
  def underlyingSafe: String|Null = Logs.trimToSafeString(extractLoggedValue(obj))

extension [T](obj: Option[T])
  @targetName("underlyingOptionSafe")
  def underlyingSafe: String|Null = Logs.trimToSafeString(extractLoggedValue(
    // ideally there should not be null, but we should not fail there if it is really null
    if obj.isNull then null else obj.getOrElse(None)))


/** Try to extract at runtime-time if it didn't happen at compile time by some reason */
private def extractLoggedValue(obj: Any|Null): Any|Null =
  if obj.isNull then null
  else asMatchable(obj) match
    case Some(v) => v
    case other => other
