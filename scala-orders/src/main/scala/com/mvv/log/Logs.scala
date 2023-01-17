package com.mvv.log

object Logs :
  private val safeLength: Int = 512

  //@JvmStatic
  private def trimToSafeString(obj: AnyRef|Null): String|Null = {
    if obj == null then return null

    val str = obj.toString

    if str.length < safeLength then str
    else s"${str.substring(0, safeLength)} ..."
  }

  extension (obj: AnyRef|Null)
    def safe: String|Null = Logs.trimToSafeString(obj)
