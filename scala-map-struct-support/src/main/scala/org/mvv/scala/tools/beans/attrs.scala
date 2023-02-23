package org.mvv.scala.tools.beans


import scala.annotation.{nowarn, tailrec}
import scala.compiletime.uninitialized
import scala.collection.mutable
import scala.reflect.ClassTag
import scala.collection.Map as BaseMap
//
import java.lang.reflect.Field as JavaField
import java.lang.reflect.Method as JavaMethod
//
import org.mvv.scala.tools.CollectionsOps.containsOneOf
import org.mvv.scala.tools.{ equalImpl, isOneOf, nnArray, beforeLast }
import org.mvv.scala.tools.beans._Type.toPortableType



enum _Visibility :
  case Private, Package, Protected, Public, Other



enum _Modifier :
  case ScalaStandardFieldAccessor, ScalaCustomFieldAccessor, JavaPropertyAccessor,
       // not really used now
       ParamAccessor, ExtensionMethod, Transparent, Macro, Static



enum ClassKind :
  case
      /** Inspected by reflection (any java class can be inspected separately even in jar file) */
      Java
      /** Inspection is done by inspecting tasty file.
       *  In case of jar only all files can be processed, it is impossible
       *  to analyze specific files separately (for that reason ALL files are processed and sub-classes
       *  merged in lazy way.) */
    , Scala3
      /** Now are not supported, But can be analyzed by scala scala-reflect API. */
    , Scala2


object ClassKind :
  extension (cls: Class[?])
    def classKind: ClassKind = cls match
      case scala2Class if isScala2Class(scala2Class) => ClassKind.Scala2
      case scala3Class if isScala3Class(scala3Class) => ClassKind.Scala3
      case _ => ClassKind.Java



enum TypesLoadMode :
  case ScalaAST // Runtime types will be unavailable (if they generics)
     , All      // Scala AST/Tasty + Java reflection (runtime types will be loaded)



/**
 * It probably has to much info, but it is to implement easily both MapStruct extension and java bean descriptor.
 */
case class BeanProperty (
  name: String,
  propertyType: _Type,
  ownerClass: _Class,
  field: Option[_Field],
  getMethods: List[_Method],
  setMethods: List[_Method],

  // These fields will be surely available after scala compilation
  // but if this class is used during macros expansion
  // (during scala compilation time it most probably will be inaccessible)
  runtimePropertyType: Option[Class[?]] = None,
  runtimeOwnerClass: Option[Class[?]] = None,
  runtimeField: Option[JavaField] = None,
  runtimeGetMethods: Option[List[JavaMethod]] = None,
  runtimeSetMethods: Option[List[JavaMethod]] = None,
)
