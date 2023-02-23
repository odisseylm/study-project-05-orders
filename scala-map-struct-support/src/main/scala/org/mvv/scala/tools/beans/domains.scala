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
              val declaredFields:  Map[_FieldKey,  _Field]  = Map(),
              val declaredMethods: Map[_MethodKey, _Method] = Map(),
             ) (inspector: ScalaBeansInspector) :
  def fullName: String = org.mvv.scala.tools.fullName(_package, simpleName)

  // with current impl it possibly can have duplicates
  lazy val parentClasses: List[_Class] =
     parentTypes.map(_type => inspector.classDescr(_type.runtimeTypeName).get)

  lazy val fields:  Map[_FieldKey, _Field]   = { mergeAllMembers(this.declaredFields,  parentClasses, cls => cls.fields) }
  lazy val methods: Map[_MethodKey, _Method] = { mergeAllMembers(this.declaredMethods, parentClasses, cls => cls.methods) }

  override def toString: String = s"Class $fullName (kind: $classKind, $classSource), " +
                                  s"fields: [${fields.mkString(",")}], methods: [${methods.mkString(",")}]"




def mergeAllMembers[M <: _ClassMember, MK](thisDeclaredFields: BaseMap[MK,M], parents: List[_Class], membersF: _Class=>Map[MK,M]): Map[MK,M] =
  val merged = mutable.Map[MK,M]()
  parents.distinct.reverse.foreach( p => mergeWithParentMembersImpl(merged, membersF(p)) )
  mergeWithParentMembersImpl(merged, thisDeclaredFields)
  Map.from(merged)


private def mergeWithParentMembersImpl[K,M <: _ClassMember](
            targetMembers: mutable.Map[K,M], toAddOrUpdate: BaseMap[K,M] ): Unit =

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

