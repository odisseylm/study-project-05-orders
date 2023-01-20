package com.mvv.utils

//import scala.Predef.nn

import com.mvv.nullables.isNull

import scala.annotation.targetName


def requireNotNull[T](v: T|Null): T = requireNotNull[T](v, "Null value.")

def requireNotNull[T](v: T|Null, msg: =>String): T =
  if v.isNull then throw IllegalArgumentException(msg) else v.nn


inline def requireNotBlankCS(s: CharSequence|Null): CharSequence = requireNotBlankCSImpl(s, "Blank value.")
inline def requireNotBlankCS(s: CharSequence|Null, msg: =>String): CharSequence = requireNotBlankCSImpl(s, msg)
private def requireNotBlankCSImpl(s: CharSequence|Null, msg: =>String): CharSequence =
  if s.isNullOrBlank then throw IllegalArgumentException(msg) else s.nn


inline def requireNotBlank(s: String|Null): String = requireNotBlankStringImpl(s, "Blank value.")
inline def requireNotBlank(s: String|Null, msg: =>String): String = requireNotBlankStringImpl(s, msg)
private def requireNotBlankStringImpl(s: String|Null, msg: =>String): String =
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

