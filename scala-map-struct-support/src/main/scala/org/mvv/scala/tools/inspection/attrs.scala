package org.mvv.scala.tools.inspection

import scala.annotation.{nowarn, tailrec}
import scala.collection.{mutable, Map as BaseMap}
import scala.compiletime.uninitialized
import scala.reflect.ClassTag
//
import java.lang.reflect.{ Field as JavaField, Method as JavaMethod }
//
import org.mvv.scala.tools.{equalImpl, isOneOf, nnArray}
import org.mvv.scala.tools.CollectionsOps.containsOneOf
import org.mvv.scala.tools.inspection._Type.toPortableType
import org.mvv.scala.tools.inspection.tasty._ClassEx



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


// TODO: implement getting kind of class by ClassDef
//object ClassKind :
//  extension (cls: Class[?])
//    def classKind: ClassKind = cls match
//      case scala2Class if isScala2Class(scala2Class) => ClassKind.Scala2
//      case scala3Class if isScala3Class(scala3Class) => ClassKind.Scala3
//      case _ => ClassKind.Java



enum InspectMode :
  case
       /** Scala AST/Tasty + Java reflection (runtime types/classes will be inspected) */
       AllSources
       /** Only AST tree, Use it if runtime types are unavailable
        *  (but full resulting runtime type names may be unavailable in case of using generics)
        */
     , ScalaAST



trait BeanProperty :
  def name: String
  def propertyType: _Type
  def ownerClass: _Class
  def field: Option[_Field]
  def getMethods: List[_Method]
  //noinspection MutatorLikeMethodIsParameterless
  def setMethods: List[_Method]


object BeanProperty :
  def apply(
    name: String,
    propertyType: _Type,
    ownerClass: _Class,
    field: Option[_Field],
    getMethods: List[_Method],
    setMethods: List[_Method],
  ): BeanProperty = BeanPropertyImpl(
    name,
    propertyType,
    ownerClass,
    field,
    getMethods,
    setMethods,
  )

  private case class BeanPropertyImpl (
    name: String,
    propertyType: _Type,
    ownerClass: _Class,
    field: Option[_Field],
    getMethods: List[_Method],
    setMethods: List[_Method],
  ) extends BeanProperty


trait JavaBeanProperty extends BeanProperty :
  // These fields will be surely available after scala compilation
  // but if this class is used during macros expansion
  // (during scala compilation time it most probably will be inaccessible)
  def runtimePropertyType: Option[Class[?]]
  def runtimeOwnerClass: Option[Class[?]]
  def runtimeField: Option[JavaField]
  def runtimeGetMethods: Option[List[JavaMethod]]
  def runtimeSetMethods: Option[List[JavaMethod]]


object JavaBeanProperty :
  def apply (
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
   ): JavaBeanProperty = JavaBeanPropertyImpl(
    name,
    propertyType,
    ownerClass,
    field,
    getMethods,
    setMethods,
    runtimePropertyType,
    runtimeOwnerClass,
    runtimeField,
    runtimeGetMethods,
    runtimeSetMethods,
  )

  private case class JavaBeanPropertyImpl (
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
 ) extends JavaBeanProperty



