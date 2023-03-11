package com.mvv.collections

import scala.collection.immutable.NumericRange


/**
 * Do not use them die to problem with operator priority.
 * This 'in' has improper priority and you will need to use () :-(.
 */

extension (v: Int)
  infix def in(range: Range): Boolean = range contains v
  infix def in(range: Range.Inclusive): Boolean = range contains v
  infix def in(range: Range.Exclusive): Boolean = range contains v

extension [T](v: T)
  infix def in(range: NumericRange[T]): Boolean = range contains v
  infix def in(range: NumericRange.Inclusive[T]): Boolean = range contains v
  infix def in(range: NumericRange.Exclusive[T]): Boolean = range contains v
