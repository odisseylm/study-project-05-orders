package org.mvv.scala.tools.quotes

import scala.annotation.targetName
import scala.quoted.*
import scala.reflect.ClassTag
import scala.reflect.Manifest
import scala.reflect.ClassManifest
//
import org.mvv.scala.tools.{ isNotNull, lastAfter, isImplClass, isOneOfImplClasses }



//noinspection ScalaUnusedSymbol , // it is used in macros by name
def isQuotesTypeByName(el: Any, typeName: String): Boolean =
  val shortTypeName = typeName.lastAfter('.').getOrElse(typeName)
  shortTypeName match
    case "Constant" => isConstantByClassName(el)
    // case "ClassDef" => ??? TODO: impl it, there is no class ClassDef (in scala3-compiler_3-3.2.2-sources.jar!/dotty/tools/dotc/ast/Trees.scala)
    // case "TypeDef"  => ???
    // Example of overriding behavior for some types
    //case "Apply" => el.isOneOfImplClasses("Apply0", "Apply1", "Apply2")
    case _ => el.isImplClass(shortTypeName)


def isConstantByClassName(el: Any): Boolean = el.isNotNull && el.isOneOfImplClasses(
  "Constant",
  "BooleanConstant", "CharConstant",
  "ByteConstant", "ShortConstant", "IntConstant", "LongConstant",
  "FloatConstant", "DoubleConstant",
  "CharConstant", "StringConstant",
  "UnitConstant", "NullConstant",
  "ClassOfConstant",
)
