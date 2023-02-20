package org.mvv.scala.tools.quotes

import scala.annotation.targetName
import scala.quoted.*
import scala.reflect.ClassTag
import scala.reflect.Manifest
import scala.reflect.ClassManifest
//
import org.mvv.scala.tools.{ lastAfter, isImplClass }



//noinspection ScalaUnusedSymbol , // it is used in macros by name
def isQuotesTypeByName(el: Any, typeName: String): Boolean =
  val shortTypeName = typeName.lastAfter('.').getOrElse(typeName)
  //el.isOneOfImplClasses(shortTypeName)
  el.isImplClass(shortTypeName)
