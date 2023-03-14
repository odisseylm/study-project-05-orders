package com.mvv.scala.temp.tests

import scala.annotation.nowarn


// !!! These samples from scala-dic really works !!!

def f1__2 = {
  1: @nowarn // don't warn "a pure expression does nothing in statement position"
  2
}

@nowarn def f2__2 = { 1; deprecated() } // don't warn

@nowarn("msg=pure expression does nothing")
def f3__2 = { 1; deprecated() } // show deprecation warning


