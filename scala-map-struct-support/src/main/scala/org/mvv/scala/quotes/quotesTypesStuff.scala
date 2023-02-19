package org.mvv.scala.quotes

import scala.quoted.*
import scala.reflect.ClassTag
import scala.reflect.Manifest
import scala.reflect.ClassManifest
//
import org.mvv.scala.mapstruct.lastAfter
import org.mvv.scala.mapstruct.isImplClass


def isQuotesType(el: Any, typeName: String): Boolean =
  val shortTypeName = typeName.lastAfter('.').getOrElse(typeName)
  //el.isOneOfImplClasses(shortTypeName)
  el.isImplClass(shortTypeName)
