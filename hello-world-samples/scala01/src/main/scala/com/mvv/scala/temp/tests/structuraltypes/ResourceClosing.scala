//noinspection ScalaUnusedSymbol
package com.mvv.scala.temp.tests.structuraltypes

import scala.language.reflectiveCalls
import scala.reflect.Selectable.reflectiveSelectable


// study sample (for myself only!!!)
// from https://github.com/Baeldung/scala-tutorials/blob/master/scala-core-4/src/main/scala/com/baeldung/scala/structuraltypes/ResourceClosing.scala

import scala.io.Source


trait ResourceClosing :
  //noinspection ScalaWeakerAccess
  type Closable = { def close(): Unit }

  def using(resource: Closable)(fn: () => Unit): Unit = try fn() finally resource.close()
  def using(file: Source)(fn: () => Unit): Unit = try fn() finally file.close()

