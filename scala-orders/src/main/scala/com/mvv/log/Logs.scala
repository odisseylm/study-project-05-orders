package com.mvv.log


object Logs :
  private val safeLength: Int = 512

  //@JvmStatic
  def trimToSafeString(obj: Any|Null): String|Null = {
    import com.mvv.nullables.AnyCanEqualGivens.given
    if obj == null then return null

    val str = obj.toString

    if str.length < safeLength then str
    else s"${str.substring(0, safeLength)} ..."
  }
  // version for java
  extension (obj: Any|Null)
    def safe: String|Null = Logs.trimToSafeString(obj)


// version for scala
extension (obj: Any|Null)
  def safe: String|Null = Logs.trimToSafeString(obj)
