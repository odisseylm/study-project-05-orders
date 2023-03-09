package org.mvv.scala.tools


private val defaultSafeLength: Int = 512

//noinspection NoTailRecursionAnnotation // no recursion at all
def asSafeStr(obj: Any|Null): String|Null = asSafeStr(obj, defaultSafeLength)

def asSafeStr(obj: Any|Null, safeLength: Int): String|Null =
  if obj == null then return null

  val str = obj.toString

  if str.length < safeLength then str
  else s"${str.substring(0, safeLength)} ..."


// version for java
extension (obj: Any|Null)
  inline def safe: String|Null = asSafeStr(obj)
  inline def safe(safeLength: Int): String|Null = asSafeStr(obj, safeLength)
