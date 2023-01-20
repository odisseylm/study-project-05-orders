package com.mvv.collections

import scala.collection.immutable.NumericRange


extension (v: Int)
  infix def in(range: Range): Boolean = range contains v
  infix def in(range: Range.Inclusive): Boolean = range contains v
  infix def in(range: Range.Exclusive): Boolean = range contains v

extension [T](v: T)
  infix def in(range: NumericRange[T]): Boolean = range contains v
  infix def in(range: NumericRange.Inclusive[T]): Boolean = range contains v
  infix def in(range: NumericRange.Exclusive[T]): Boolean = range contains v


/*
// TODO: try to use it instead of versions with Inclusive/Exclusive

extension [R <: Range](v: Int)
  infix def in(range: R): Boolean = range.contains(v)

extension [T, R <: NumericRange[T]](v: T)
  infix def in(range: R): Boolean = range.contains(v)
*/
