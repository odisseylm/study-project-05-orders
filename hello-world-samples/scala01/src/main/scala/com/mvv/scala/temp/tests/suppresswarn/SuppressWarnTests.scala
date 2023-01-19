package com.mvv.scala.temp.tests.suppresswarn

import scala.annotation.nowarn
import scala.annotation.unused


def aaa(): Unit = {

  @nowarn("cat=deprecation") // !!!!! <=== it suppressed incorrect/any error ???
  val dd1 = 678

  @unused
  val dd2 = 678
}

val dpr = 678

// !!! WORKING
@nowarn("msg=pure expression does nothing") // suppressing ONLY specified warning (this msg should be part of warn message)
def f = { 1; dpr }


import scala.language.reflectiveCalls
import scala.languageFeature.reflectiveCalls
import scala.reflect.Selectable.reflectiveSelectable
def foo(x: { def get: Int }) = 123 + x.get
