package org.mvv.scala.tools.beans

import scala.collection.mutable
//
import java.lang.reflect.Method as JavaMethod
import java.lang.reflect.Field  as JavaField
//
import org.mvv.scala.tools.CollectionsOps.containsOneOf
import org.mvv.scala.tools.{ Logger, nnArray, isNull, uncapitalize, tryDo }




//extension (m: _Method)
//  def isPropertyAccessor: Boolean =
//    m.modifiers.containsOneOf(
//      _Modifier.ScalaStandardFieldAccessor, _Modifier.ScalaCustomFieldAccessor, _Modifier.JavaPropertyAccessor)
//
//  def isGetterMethod: Boolean =
//    m.isPropertyAccessor && m.mainParams.isEmpty && !m.returnType.isVoid
//  def isSetterMethod: Boolean =
//    m.isPropertyAccessor && m.mainParams.sizeIs == 1
//
//  def toPropName: String = m match
//    case javaPropM if javaPropM.modifiers.containsOneOf(_Modifier.JavaPropertyAccessor) =>
//      javaPropM.name match
//        case setMName if setMName.startsWith("set") => setMName.stripPrefix("set").uncapitalize
//        case isMName  if isMName.startsWith("is")  => isMName.stripPrefix("is").uncapitalize
//        case getMName if getMName.startsWith("get") => getMName.stripPrefix("get").uncapitalize
//        case other => other
//    case scalaPropM if scalaPropM.modifiers.containsOneOf(_Modifier.ScalaStandardFieldAccessor, _Modifier.ScalaCustomFieldAccessor) =>
//      scalaPropM.name match
//        case setMName if setMName.endsWith("_=") => setMName.stripSuffix("_=")
//        case setMName if setMName.endsWith("_$eq") => setMName.stripSuffix("_$eq")
//        case other => other
//    case other => other.name
//

