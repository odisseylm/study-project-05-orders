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
import org.mvv.scala.tools.{ equalImpl, isOneOf, nnArray, beforeLast, beforeFirst }
import org.mvv.scala.tools.beans._Type.toPortableType




class _Class (val _package: String, val simpleName: String,
              val classKind: ClassKind, val classSource: Option[ClassSource],
              val runtimeClass: Option[Class[?]],
              // with current impl it possibly can have duplicates
              val parentTypes: List[_Type] = Nil,
              val declaredFields: Map[_FieldKey, _Field] = Map(),
              val declaredMethods: Map[_MethodKey, _Method] = Map(),
             ) (inspector: ScalaBeansInspector) :
  def fullName: String = org.mvv.scala.tools.fullName(_package, simpleName)

  // with current impl it possibly can have duplicates
  private var _parentClasses: List[_Class] = Nil
  lazy val parentClasses: List[_Class] =
    if _parentClasses.sizeIs != parentTypes.size then
      _parentClasses = parentTypes.map(_type => inspector.classDescr(_type.runtimeTypeName).get)
    _parentClasses

  lazy val fields:  Map[_FieldKey, _Field]   = { mergeAllFields(this,  this.declaredFields,  parentClasses) }
  lazy val methods: Map[_MethodKey, _Method] = { mergeAllMethods(this, this.declaredMethods, parentClasses) }

  override def toString: String = s"Class $fullName (kind: $classKind, $classSource), " +
                                  s"fields: [${fields.mkString(",")}], methods: [${methods.mkString(",")}]"



case class _TypeParam (name: String) :
  override def toString: String = name


def typeNameToRuntimeClassName(typeName: String): String =
  // if it is generics
  typeName.beforeFirst('[').getOrElse(typeName)



class _Type (
  val declaredTypeName: String,
  val runtimeTypeName: String,
  ) extends Equals derives CanEqual :

  def this(declaredTypeName: String) = this(declaredTypeName, typeNameToRuntimeClassName(declaredTypeName))

  // TODO: use alternative approach if at this time java class is not accessible yet
  //def toRuntimeClass: Class[?] = Class.forName(runtimeTypeName).nn
  def withRuntimeType(newRuntimeTypeName: String): _Type = _Type(this.declaredTypeName, newRuntimeTypeName)
  def withRuntimeType(newRuntimeType: Class[?]): _Type = withRuntimeType(newRuntimeType.getName.nn)
  override def toString: String =
    if runtimeTypeName != declaredTypeName then s"$declaredTypeName/$runtimeTypeName" else declaredTypeName

  override def hashCode: Int =
    val portable = this.toPortableType
    31 * portable.declaredTypeName.hashCode + portable.runtimeTypeName.hashCode
  override def canEqual(other: Any): Boolean = other.isInstanceOf[_Type]

  // in scala3 it causes warning "pattern selector should be an instance of Matchable"
  //override def equals(other: Any): Boolean = other match
  //  case that: _Type => that.canEqual(this) && toPortableType(this.typeName) == toPortableType(that.typeName)
  //  case _ => false
  override def equals(other: Any): Boolean =
    // 'equalImpl' is inlined and have resulting byte code similar to code with 'match'
    equalImpl(this, other) { (v1, v2) =>
      v1.toPortableType.declaredTypeName == v2.toPortableType.declaredTypeName &&
      v1.toPortableType.runtimeTypeName  == v2.toPortableType.runtimeTypeName
    }
end _Type


object Types :
  val VoidTypeName = "void"        /*private[_Type]*/
  val UnitTypeName = "scala.Unit"  /*private[_Type]*/

  //noinspection ScalaWeakerAccess
  val VoidType: _Type = _Type(VoidTypeName)
  val UnitType: _Type = _Type(UnitTypeName)
  val ObjectType: _Type = _Type("java.lang.Object")
  val StringType: _Type = _Type("java.lang.String")



object _Type :
  import Types.*

  extension (t: _Type)
    private def toTypeName: String = t.runtimeTypeName //.getOrElse(t.declaredTypeName)

    def toPortableType: _Type = t.declaredTypeName match
      case VoidTypeName => UnitType
      case _ => t

    def isVoid: Boolean =
      t.toTypeName.isOneOf("void", "Void", "Unit", "scala.Unit")

    def isBool: Boolean =
      t.toTypeName.isOneOf("boolean", "Boolean", "java.lang.Boolean", "scala.Boolean")


sealed trait _ClassMember :
  val name: String
  val visibility: _Visibility
  val modifiers: Set[_Modifier]
  def withAddedModifiers(newModifiers: _Modifier*): _ClassMember
  def toKey: AnyRef
  // it is used for replacing generic type name (like A, T, etc) with runtime type
  // (in most cases it will be java.lang.Object)
  //def fixResultingType(resultingClass: Class[?]): _ClassMember


case class _Field (
  override val name: String,
  override val visibility: _Visibility,
  override val modifiers: Set[_Modifier],
  _type: _Type,
  )(
  // noinspection ScalaUnusedSymbol , for debugging only
  val internalValue: Any
  ) extends _ClassMember :
  override def toString: String = s"Field '$name' : $_type (modifiers: $modifiers)"
  def withAddedModifiers(newModifiers: _Modifier*): _Field =
    this.copy(modifiers = this.modifiers ++ newModifiers)(internalValue)
  override def toKey: _FieldKey = _FieldKey(this)
  //override def fixResultingType(resultingClass: Class[?]): _Field = fixFieldType(resultingClass, this)



case class _FieldKey(fieldName: String)(field: Option[_Field] = None) :
  override def toString: String =
    val resultTypeStr = field .map(s": ${_._type}") .getOrElse("")
    s"$fieldName$resultTypeStr"

object _FieldKey :
  def apply(field: _Field): _FieldKey = new _FieldKey(field.name)(Option(field))
  def apply(fieldName: String): _FieldKey = new _FieldKey(fieldName)(None)


case class _Method (
  override val name: String,
  override val visibility: _Visibility,
  override val modifiers: Set[_Modifier],
  returnType: _Type,
  mainParams: List[_Type],
  // scala has to much different kinds of params, for that reason we do not collect all them
  hasExtraScalaParams: Boolean,
  )(
  // noinspection ScalaUnusedSymbol , for debugging only
  val internalValue: Any
  ) extends _ClassMember :

  // TODO: seems it is duplicated
  val isScalaPropertyAccessor: Boolean = modifiers.containsOneOf(_Modifier.ScalaStandardFieldAccessor, _Modifier.ScalaCustomFieldAccessor)
  //noinspection ScalaWeakerAccess
  // TODO: seems it is duplicated
  val isPropertyAccessor: Boolean = isScalaPropertyAccessor || modifiers.contains(_Modifier.JavaPropertyAccessor)

  validate()

  private def validate(): Unit =
    if isPropertyAccessor then
      require(!hasExtraScalaParams, "Property accessor cannot have additional params.")
      require(mainParams.size <= 1, s"Property accessor cannot have ${mainParams.size} params.")

  override def toString: String =
    val extraSuffix = if hasExtraScalaParams then ", hasExParams" else ""
    s"Method { $name (${mainParams.mkString(",")}) $extraSuffix }"

  def withAddedModifiers(newModifiers: _Modifier*): _Method =
    this.copy(modifiers = this.modifiers ++ newModifiers)(internalValue)
  override def toKey: _MethodKey = _MethodKey(this)
  //override def fixResultingType(resultingClass: Class[?]): _Method = fixMethodType(resultingClass, this)



case class _MethodKey (methodName: String, params: List[_Type], hasExtraScalaParams: Boolean)(method: Option[_Method] = None) :
  override def toString: String =
    //noinspection MapGetOrElseBoolean
    val isScalaPropertyAccessor = method .map(_.isScalaPropertyAccessor) .getOrElse(false)
    val returnType = method .map(_.returnType) .getOrElse(Types.UnitType)

    val extraSuffix = if hasExtraScalaParams then " ( hasExParams )" else ""
    val paramsStr = if isScalaPropertyAccessor && params.isEmpty then "" else s"(${params.mkString(",")})"
    val resultTypeStr = if returnType.isVoid then "" else s": $returnType"
    s"$methodName$paramsStr$resultTypeStr$extraSuffix"

object _MethodKey :
  def apply(method: _Method): _MethodKey =
    new _MethodKey(method.name, method.mainParams, method.hasExtraScalaParams)(Option(method))
  // seems default param value does not work as I expect
  def apply(methodName: String, params: List[_Type], hasExtraScalaParams: Boolean): _MethodKey =
    new _MethodKey(methodName, params, hasExtraScalaParams)(None)
  //def getter[T](name: String)(implicit ct: ClassTag[T]): _MethodKey = apply(name, List(_Type(ct.runtimeClass.toString)), false)
  def getter(propName: String): _MethodKey = apply(propName, Nil, false)
  def setter[T](propName: String)(implicit ct: ClassTag[T]): _MethodKey = apply(s"${propName}_=", List(_Type(ct.toString)), false)



def mergeAllFields(thisClass: _Class, thisDeclaredFields: BaseMap[_FieldKey,_Field], parents: List[_Class]): Map[_FieldKey,_Field] =
  val merged = mutable.Map[_FieldKey,_Field]()
  parents.distinct.reverse.foreach( p => mergeMembers(thisClass, merged, p.fields) )
  mergeMembers(thisClass, merged, thisDeclaredFields)
  Map.from(merged)

def mergeAllMethods(thisClass: _Class, thisDeclaredMethods: BaseMap[_MethodKey,_Method], parents: List[_Class]): Map[_MethodKey,_Method] =
  val merged = mutable.Map[_MethodKey,_Method]()
  parents.distinct.reverse.foreach( p => mergeMembers(thisClass, merged, p.methods) )
  mergeMembers(thisClass, merged, thisDeclaredMethods)
  Map.from(merged)


private def mergeMembers[K,M <: _ClassMember](
            thisClass: _Class, targetMembers: mutable.Map[K,M], toAddOrUpdate: BaseMap[K,M] ): Unit =
  toAddOrUpdate.foreach { (k, v) =>
    // replacing key is needed for having proper optional key metadata (it is optional but really helps debugging & testing)
    val removed: Option[M] = targetMembers.remove(k)
    val newMember: M = removed
      .map { parentDeclaredMember =>
        // we need to inherit 'java property' modifier from super java class
        if parentDeclaredMember.modifiers.contains(_Modifier.JavaPropertyAccessor)
          then v.withAddedModifiers(_Modifier.JavaPropertyAccessor).asInstanceOf[M] else v }
      .getOrElse(v)
    //val fixed = newMember.fixResultingType(thisClass).asInstanceOf[M]
    val fixed = newMember
    targetMembers.put(fixed.toKey.asInstanceOf[K], fixed)
  }


/*
private def fixFieldType(cls: _Class, field: _Field): _Field =
  if typeExists(field._type) then return field
  // no sense to process private fields in scope of 'java beans' (at least now)
  //if field.visibility == _Visibility.Private then field

  val foundJavaMethod: Option[JavaMethod] = findJavaMethod(cls, field.name)
  if foundJavaMethod.isDefined /*&& foundJavaMethod.get.getReturnType != classOf[Object]*/ then
    return foundJavaMethod.map(javaMethod => changeFieldType(field, javaMethod)).get

  val foundJavaField: Option[JavaField] = findJavaField(cls, field.name)
  if foundJavaField.isDefined /*&& foundJavaField.get.getType != classOf[Object]*/ then
    return foundJavaField.map(javaField => changeFieldType(field, javaField)).get

  field


private def fixMethodType(cls: Class[?], method: _Method): _Method =
  // no sense to process private fields in scope of 'java beans' (at least now)
  if method.visibility == _Visibility.Private
     || method.mainParams.isEmpty && method.returnType == Types.VoidType
    then return method

  if method.mainParams.isEmpty && !method.returnType.isVoid && !method.hasExtraScalaParams then
    if typeExists(method.returnType) then return method
    val m = findJavaMethod(cls, method.name)
    if m.isDefined then return changeReturnType(method, m.get)

  if method.mainParams.size == 1 && !method.hasExtraScalaParams then
    if typeExists(method.mainParams.head) then return method
    val m = findJavaMethodWithOneParam(cls, method.name)
    if m.isDefined then return changeFirstParamType(method, m.get)

  //if !method.modifiers.containsOneOf(_Modifier.ScalaStandardFieldAccessor,
  //  _Modifier.ScalaCustomFieldAccessor,
  //  _Modifier.JavaPropertyAccessor)
  //  // we need to have fixed only bean properties (since it is enough complicated in general)
  //  then return method

  method


private def changeFieldType(field: _Field, jf: JavaField): _Field =
  field.copy(_type = field._type.withRuntimeType(jf.getType.nn))(field.internalValue)

private def changeFieldType(field: _Field, jm: JavaMethod): _Field =
  field.copy(_type = field._type.withRuntimeType(jm.getReturnType.nn))(field.internalValue)

private def changeReturnType(method: _Method, jm: JavaMethod): _Method =
  method.copy(returnType = method.returnType.withRuntimeType(jm.getReturnType.nn))(method.internalValue)

private def changeFirstParamType(method: _Method, jm: JavaMethod): _Method =
  require(method.mainParams.size == 1 && jm.getParameterCount == 1)
  val paramTypes = jm.getParameterTypes.nnArray
  method.copy(mainParams = List(method.mainParams.head.withRuntimeType(paramTypes(0))))(method.internalValue)
*/

