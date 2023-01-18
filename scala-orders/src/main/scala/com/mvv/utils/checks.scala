package com.mvv.utils

//import scala.Predef.nn


def requireNotNull[T](v: T|Null): T = requireNotNull[T](v, "Null value.")

def requireNotNull[T](v: T|Null, msg: =>String): T =
  if isNull(v) then throw IllegalArgumentException(msg) else v.nn

def requireNotBlank(s: CharSequence|Null): CharSequence = requireNotBlank(s, "Blank value.")
def requireNotBlank(s: CharSequence|Null, msg: =>String): CharSequence =
  if s.isNullOrBlank then throw IllegalArgumentException(msg) else s.nn

// T O D O: Uncomment later - it compiled OK, but Idea shows errors
//def requireNotBlank(v: String|Null): String = requireNotBlank(v, "Blank value.")
//def requireNotBlank(v: String|Null, msg: =>String): String =
//  if isNullOrBlank(v) then throw IllegalArgumentException(msg) else v.nn


def require(expr: Boolean): Unit = require(expr, "requirement failed")

def require(expr: Boolean, msg: =>String): Unit =
  if !expr then throw IllegalArgumentException(msg)

// TODO: find better name
def requireWith(expr: Boolean)(msg: ()=>String): Unit =
  if !expr then throw IllegalArgumentException(msg())

