package com.mvv.scala.temp.tests.tasty.testclasses

import java.time.LocalTime
import scala.compiletime.uninitialized

trait GenericTrait1[A,B <: java.lang.Comparable[B]] :
  var aVar: A = uninitialized
  val bVal: B


trait GenericTrait2[C] :
  val cVal: C


abstract class GenericBaseClass1[C] extends GenericTrait2[C] :
  var baseClass1Var1: C = uninitialized

class GenericClass2 extends GenericBaseClass1[String], GenericTrait1[Long, LocalTime] :
  private var class2Var: String = uninitialized
  override val bVal: LocalTime = LocalTime.now.nn
  override val cVal: String = ""
