package com.mvv.scala.temp.tests.tasty

import com.mvv.scala.temp.tests.tasty._Type.toPortableType

import scala.annotation.{nowarn, tailrec}
import scala.compiletime.uninitialized
import scala.collection.mutable


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


class _Class (val classKind: ClassKind, val classSource: ClassSource, val _package: String, val simpleName: String)
             (inspector: ScalaBeansInspector) :
  def fullName: String = _Class.fullName(_package, simpleName)
  var parentClassFullNames: List[String] = Nil
  var parents: List[_Class] = Nil
  var declaredFields: Map[String, _Field] = Map()
  var declaredMethods: Map[_MethodKey, _Method] = Map()
  lazy val fields: Map[String, _Field] = { fillParentsClasses(); mergeAllFields(this.declaredFields, parents) }
  lazy val methods: Map[_MethodKey, _Method] = { fillParentsClasses(); mergeAllMethods(this.declaredMethods, parents) }
  private def fillParentsClasses(): Unit =
    if parents.size != parentClassFullNames.size then
      parents = parentClassFullNames.map(className => inspector.classDescr(className).get)

  override def toString: String = s"Class $fullName (kind: $classKind, $classSource), " +
                                  s"fields: [${fields.mkString(",")}], methods: [${methods.mkString(",")}]"

object _Class :
  def fullName(_package: String, simpleClassName: String): String = s"$_package.$simpleClassName"


enum _Modifier :
  case FieldAccessor, CustomFieldAccessor, JavaPropertyAccessor, ParamAccessor, ExtensionMethod, Transparent, Macro, Static


private enum _Visibility :
  case Private, Package, Protected, Public, Other


trait _ClassMember :
  val name: String
  val visibility: _Visibility
  val modifiers: Set[_Modifier]

case class _Field(
  override val name: String,
  override val visibility: _Visibility,
  override val modifiers: Set[_Modifier],
  _type: String,
  )(
  // noinspection ScalaUnusedSymbol , for debugging only
  val internalValue: Any
  ) extends _ClassMember :
  override def toString: String = s"Field '$name' : $_type (modifiers: $modifiers)"


/*
// it is not used right now
private class _Param (
  val name: String,
  val _type: String,
)
*/


case class _Method (
  override val name: String,
  override val visibility: _Visibility,
  override val modifiers: Set[_Modifier],
  resultType: _Type,
  mainParams: List[_Type],
  // scala has to much different kinds of params, for that reason we do not collect all them
  hasExtraScalaParams: Boolean,
  )(
  // noinspection ScalaUnusedSymbol , for debugging only
  val internalValue: Any
  ) extends _ClassMember :

  val isScalaPropertyAccessor: Boolean = modifiers.contains(_Modifier.FieldAccessor)
    || modifiers.contains(_Modifier.CustomFieldAccessor)
  //noinspection ScalaWeakerAccess
  val isPropertyAccessor: Boolean = isScalaPropertyAccessor || modifiers.contains(_Modifier.JavaPropertyAccessor)

  if isPropertyAccessor then
    require(!hasExtraScalaParams, "Property accessor cannot have additional params.")
    require(mainParams.size <= 1, s"Property accessor cannot have ${mainParams.size} params.")

  override def toString: String =
    val extraSuffix = if hasExtraScalaParams then ", hasExParams" else ""
    s"Method { $name (${mainParams.mkString(",")}) $extraSuffix }"


@nowarn("msg=cannot be checked at runtime")
private inline def equalImpl[T <: Equals](thisV: T, other: Any|Null)(inline comparing: (T, T)=>Boolean): Boolean =
  import scala.language.unsafeNulls
  if other == null || !other.isInstanceOf[T] then false
  else comparing(thisV, other.asInstanceOf[T])


class _Type (val typeName: String) extends Equals derives CanEqual :
  override def toString: String = typeName
  override def hashCode: Int = this.toPortableType.typeName.hashCode
  override def canEqual(other: Any): Boolean = other.isInstanceOf[_Type]
  // it causes warning "pattern selector should be an instance of Matchable" with Scala 3
  //override def equals(other: Any): Boolean = other match
  //  case that: _Type => that.canEqual(this) && toPortableType(this.typeName) == toPortableType(that.typeName)
  //  case _ => false
  override def equals(other: Any): Boolean =
    // it is inlined and have resulting byte code similar to code with 'match'
    equalImpl(this, other) { (v1, v2) => v1.toPortableType.typeName == v2.toPortableType.typeName }


object _Type :
  private val VoidTypeName = "void"
  private val UnitTypeName = "scala.Unit"

  private val VoidType: _Type = _Type(VoidTypeName)
  val UnitType: _Type = _Type(UnitTypeName)

  extension (t: _Type)
    def toPortableType: _Type = t.typeName match
      case VoidTypeName => UnitType
      case _ => t

    def isVoid: Boolean = { t == VoidType || t == UnitType }


case class _MethodKey (methodName: String, params: List[_Type], hasExtraScalaParams: Boolean)(method: Option[_Method] = None) :
  override def toString: String =
    //noinspection MapGetOrElseBoolean
    val isScalaPropertyAccessor = method .map(_.isScalaPropertyAccessor) .getOrElse(false)
    val resultType = method .map(_.resultType) .getOrElse(_Type.UnitType)

    val extraSuffix = if hasExtraScalaParams then " ( hasExParams )" else ""
    val paramsStr = if isScalaPropertyAccessor && params.isEmpty then "" else s"(${params.mkString(",")})"
    val resultTypeStr = if resultType.isVoid then "" else s": ${resultType.toString}"
    s"$methodName$paramsStr$resultTypeStr$extraSuffix"

object _MethodKey :
  def apply(method: _Method): _MethodKey =
    new _MethodKey(method.name, method.mainParams, method.hasExtraScalaParams)(Option(method))
  // seems default param value does not work as I expect
  def apply(methodName: String, params: List[_Type], hasExtraScalaParams: Boolean): _MethodKey =
    new _MethodKey(methodName, params, hasExtraScalaParams)(None)
extension (m: _Method)
  def toKey: _MethodKey = _MethodKey(m)

/*
def mergeAllDeclaredMembers(_class: _Class): Unit =
  val aa = _class.parents.map(_.fullName)
  //println(s"merge $aa")
  _class.parents.reverse.foreach(p /*: _Class*/ =>
    mergeFields(_class.fields, p.declaredFields)
    mergeMethods(_class.methods, p.declaredMethods)
  )
  mergeFields(_class.fields, _class.declaredFields)
  mergeMethods(_class.methods, _class.declaredMethods)
*/

def mergeAllFields(thisDeclaredFields: scala.collection.Map[String,_Field], parents: List[_Class]): Map[String,_Field] =
  val merged = mutable.Map[String,_Field]()
  parents.reverse.foreach( p => mergeFields(merged, p.fields) )
  mergeFields(merged, thisDeclaredFields)
  Map.from(merged)

def mergeAllMethods(thisDeclaredMethods: scala.collection.Map[_MethodKey,_Method], parents: List[_Class]): Map[_MethodKey,_Method] =
  val merged = mutable.Map[_MethodKey,_Method]()
  parents.reverse.foreach( p => mergeMethods(merged, p.methods) )
  mergeMethods(merged, thisDeclaredMethods)
  Map.from(merged)


private def mergeFields[K](targetFields: mutable.Map[K,_Field], toAddOrUpdate: scala.collection.Map[K,_Field]): Unit =
  mergeMembers(targetFields, toAddOrUpdate, mergeMember)
private def mergeMethods[K](targetMethods: mutable.Map[K,_Method], toAddOrUpdate: scala.collection.Map[K,_Method]): Unit =
  mergeMembers(targetMethods, toAddOrUpdate, mergeMember)
private def mergeMembers[K,M](targetMembers: mutable.Map[K,M], toAddOrUpdate: scala.collection.Map[K,M], mergeMemberFunc: (M,M)=>M): Unit =
  toAddOrUpdate.foreach { (k, v) =>
    // replacing key is needed for having proper optional key metadata (it is optional but really helps debugging & testing)
    val removed: Option[M] = targetMembers.remove(k)
    val newMember = removed.map(parentMember => mergeMemberFunc(parentMember, v)) .getOrElse(v)
    targetMembers.put(k, newMember)
  }

// TODO: try to use them as givens
private def mergeMember(parentDeclaredMember: _Field, thisDeclaredMember: _Field): _Field =
  if parentDeclaredMember.modifiers.contains(_Modifier.JavaPropertyAccessor)
    then thisDeclaredMember.copy(modifiers = thisDeclaredMember.modifiers + _Modifier.JavaPropertyAccessor)
                                (thisDeclaredMember.internalValue)
    else thisDeclaredMember
private def mergeMember(parentDeclaredMember: _Method, thisDeclaredMember: _Method): _Method =
  if parentDeclaredMember.modifiers.contains(_Modifier.JavaPropertyAccessor)
    then thisDeclaredMember.copy(modifiers = thisDeclaredMember.modifiers + _Modifier.JavaPropertyAccessor)
                                (thisDeclaredMember.internalValue)
    else thisDeclaredMember
