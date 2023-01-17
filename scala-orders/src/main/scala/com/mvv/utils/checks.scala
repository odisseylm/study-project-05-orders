package com.mvv.utils

//import scala.Predef.nn


def requireNotNull[T](v: T|Null): T = requireNotNull[T](v, "Null value.")

def requireNotNull[T](v: T|Null, msg: =>String): T =
  if isNull(v) then throw IllegalArgumentException(msg) else v.nn

def require(expr: Boolean): Unit = require(expr, "requirement failed")

def require(expr: Boolean, msg: =>String): Unit =
  if !expr then throw IllegalArgumentException(msg)

// TODO: find better name
def requireWith(expr: Boolean)(msg: ()=>String): Unit =
  if !expr then throw IllegalArgumentException(msg())

