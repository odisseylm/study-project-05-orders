package com.mvv.utils



// use it only for hacking approach when any unexpected error can happen
def tryDo[T](expr: => T): Option[T] =
  try Option[T](expr).nn catch case _: Throwable => None
