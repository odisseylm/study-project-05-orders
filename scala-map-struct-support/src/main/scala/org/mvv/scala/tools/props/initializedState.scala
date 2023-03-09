package org.mvv.scala.tools.props

import scala.annotation.targetName
import org.mvv.scala.tools.isNotNull


//noinspection ScalaFileName
trait DefaultIsInitializedMethods :
  def isInitialized(v: AnyRef): Boolean = v.isNotNull
  def isInitialized(v: LateInitProp[Any]): Boolean = v.isInitialized



/*
//noinspection ScalaFileName
trait UninitializedPrimitives :
  def isUninitialized(v: Byte): Boolean = v == 0
  @targetName("isUninitializedByteOrNull")
  def isUninitialized(v: Byte|Null): Boolean = v== null || v == 0
  def isUninitialized(v: Short): Boolean = v == 0
  @targetName("isUninitializedShortOrNull")
  def isUninitialized(v: Short|Null): Boolean = v == null || v == 0
  def isUninitialized(v: Int): Boolean = v == 0
  @targetName("isUninitializedIntOrNull")
  def isUninitialized(v: Int|Null): Boolean = v== null || v == 0
  def isUninitialized(v: Long): Boolean = v == 0
  @targetName("isUninitializedLongOrNull")
  def isUninitialized(v: Long|Null): Boolean = v == null || v == 0
  def isUninitialized(v: Float): Boolean = v == 0
  @targetName("isUninitializedFloatOrNull")
  def isUninitialized(v: Float|Null): Boolean = v== null || v == 0
  def isUninitialized(v: Double): Boolean = v == 0
  @targetName("isUninitializedDoubleOrNull")
  def isUninitialized(v: Double|Null): Boolean = v == null || v == 0
*/