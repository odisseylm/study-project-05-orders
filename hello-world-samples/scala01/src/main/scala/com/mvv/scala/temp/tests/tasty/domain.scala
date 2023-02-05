package com.mvv.scala.temp.tests.tasty


import scala.annotation.{nowarn, tailrec}
import scala.compiletime.uninitialized
import scala.collection.mutable
import scala.reflect.ClassTag
//
import com.mvv.scala.temp.tests.tasty._Type.toPortableType
import CollectionsOps.containsOneOf


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


class _Class (val runtimeClass: Class[?], val classKind: ClassKind, val classSource: ClassSource, val _package: String, val simpleName: String)
             (inspector: ScalaBeansInspector) :
  def fullName: String = s"$_package.$simpleName"
  // with current impl it possibly can have duplicates
  var parentTypeNames: List[_Type] = Nil
  // with current impl it possibly can have duplicates
  var parents: List[_Class] = Nil
  var declaredTypeParams: List[_TypeParam] = Nil
  var declaredFields: Map[_FieldKey, _Field] = Map()
  var declaredMethods: Map[_MethodKey, _Method] = Map()
  lazy val fields: Map[_FieldKey, _Field] = { fillParentsClasses(); mergeAllFields(runtimeClass, this.declaredFields, parents) }
  lazy val methods: Map[_MethodKey, _Method] = { fillParentsClasses(); mergeAllMethods(runtimeClass, this.declaredMethods, parents) }
  private def fillParentsClasses(): Unit =
    if parents.size != parentTypeNames.size then
      parents = parentTypeNames.map(_type => inspector.classDescr(_type.className).get)

  override def toString: String = s"Class $fullName (kind: $classKind, $classSource), " +
                                  s"fields: [${fields.mkString(",")}], methods: [${methods.mkString(",")}]"

object _Class


enum _Modifier :
  case ScalaStandardFieldAccessor, ScalaCustomFieldAccessor, JavaPropertyAccessor,
  // not really used now
  ParamAccessor, ExtensionMethod, Transparent, Macro, Static


private enum _Visibility :
  case Private, Package, Protected, Public, Other


case class _TypeParam (name: String) :
  override def toString: String = name

class _Type (val typeName: String /*, typeParams: List[_TypeParam] = Nil*/) extends Equals derives CanEqual :
  // if typeName contains (in the future generics/type parameters) we need to extract only class name
  def className: String = typeName
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

  //noinspection ScalaWeakerAccess
  val VoidType: _Type = _Type(VoidTypeName)
  val UnitType: _Type = _Type(UnitTypeName)
  val StringType: _Type = _Type("java.lang.String")

  extension (t: _Type)
    def toPortableType: _Type = t.typeName match
      case VoidTypeName => UnitType
      case _ => t

    def isVoid: Boolean = { t == VoidType || t == UnitType }



trait _ClassMember :
  val name: String
  val visibility: _Visibility
  val modifiers: Set[_Modifier]


case class _Field(
  override val name: String,
  override val visibility: _Visibility,
  override val modifiers: Set[_Modifier],
  _type: _Type,
  genericType: String = "",
  )(
  // noinspection ScalaUnusedSymbol , for debugging only
  val internalValue: Any
  ) extends _ClassMember :
  override def toString: String = s"Field '$name' : $_type (modifiers: $modifiers)"


case class _FieldKey(fieldName: String)(field: Option[_Field] = None) :
  override def toString: String =
    val resultTypeStr = field.map(f => s": ${f._type}").getOrElse("")
    s"$fieldName$resultTypeStr"


object _FieldKey :
  def apply(field: _Field): _FieldKey =
    new _FieldKey(field.name)(Option(field))
  def apply(fieldName: String): _FieldKey =
    new _FieldKey(fieldName)(None)

//object _FieldOps :
extension (f: _Field)
  def toKey: _FieldKey = _FieldKey(f)



case class _Method (
  override val name: String,
  override val visibility: _Visibility,
  override val modifiers: Set[_Modifier],
  returnType: _Type,
  mainParams: List[_Type],
  // scala has to much different kinds of params, for that reason we do not collect all them
  hasExtraScalaParams: Boolean,
  returnGenericType: String = "",
  )(
  // noinspection ScalaUnusedSymbol , for debugging only
  val internalValue: Any
  ) extends _ClassMember :

  val isScalaPropertyAccessor: Boolean = modifiers.containsOneOf(_Modifier.ScalaStandardFieldAccessor, _Modifier.ScalaCustomFieldAccessor)
  //noinspection ScalaWeakerAccess
  val isPropertyAccessor: Boolean = isScalaPropertyAccessor || modifiers.contains(_Modifier.JavaPropertyAccessor)

  validate()

  private def validate(): Unit =
    if isPropertyAccessor then
      require(!hasExtraScalaParams, "Property accessor cannot have additional params.")
      require(mainParams.size <= 1, s"Property accessor cannot have ${mainParams.size} params.")

  override def toString: String =
    val extraSuffix = if hasExtraScalaParams then ", hasExParams" else ""
    s"Method { $name (${mainParams.mkString(",")}) $extraSuffix }"


case class _MethodKey (methodName: String, params: List[_Type], hasExtraScalaParams: Boolean)(method: Option[_Method] = None) :
  override def toString: String =
    //noinspection MapGetOrElseBoolean
    val isScalaPropertyAccessor = method .map(_.isScalaPropertyAccessor) .getOrElse(false)
    val returnType = method .map(_.returnType) .getOrElse(_Type.UnitType)

    val extraSuffix = if hasExtraScalaParams then " ( hasExParams )" else ""
    val paramsStr = if isScalaPropertyAccessor && params.isEmpty then "" else s"(${params.mkString(",")})"
    val resultTypeStr = if returnType.isVoid then "" else s": ${returnType.toString}"
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

//object _MethodOps :
extension (m: _Method)
  def toKey: _MethodKey = _MethodKey(m)


def mergeAllFields(thisClass: Class[?], thisDeclaredFields: scala.collection.Map[_FieldKey,_Field], parents: List[_Class]): Map[_FieldKey,_Field] =
  val merged = mutable.Map[_FieldKey,_Field]()
  parents.distinct.reverse.foreach( p => mergeFields(thisClass, merged, p.fields) )
  mergeFields(thisClass, merged, thisDeclaredFields)
  Map.from(merged)

def mergeAllMethods(thisClass: Class[?], thisDeclaredMethods: scala.collection.Map[_MethodKey,_Method], parents: List[_Class]): Map[_MethodKey,_Method] =
  val merged = mutable.Map[_MethodKey,_Method]()
  parents.distinct.reverse.foreach( p => mergeMethods(thisClass, merged, p.methods) )
  mergeMethods(thisClass, merged, thisDeclaredMethods)
  Map.from(merged)


private def mergeFields(thisClass: Class[?],
                        targetFields: mutable.Map[_FieldKey,_Field],
                        toAddOrUpdate: scala.collection.Map[_FieldKey,_Field]): Unit =
  mergeMembers(thisClass, targetFields, toAddOrUpdate, mergeMember, f => f.toKey, fixFieldType)
private def mergeMethods(thisClass: Class[?],
                         targetMethods: mutable.Map[_MethodKey,_Method],
                         toAddOrUpdate: scala.collection.Map[_MethodKey,_Method]): Unit =
  mergeMembers(thisClass, targetMethods, toAddOrUpdate, mergeMember, m => m.toKey, fixMethodType)

private def mergeMembers[K,M](
            thisClass: Class[?],
            targetMembers: mutable.Map[K,M], toAddOrUpdate: scala.collection.Map[K,M],
            mergeMemberFunc: (M,M)=>M,
            keyFunc: M=>K,
            fixTypesFunc: (Class[?],M)=>M,
            ): Unit =
  toAddOrUpdate.foreach { (k, v) =>
    // replacing key is needed for having proper optional key metadata (it is optional but really helps debugging & testing)
    val removed: Option[M] = targetMembers.remove(k)
    val newMember = removed.map(parentMember => mergeMemberFunc(parentMember, v)) .getOrElse(v)
    val fixed = fixTypesFunc(thisClass, newMember)
    targetMembers.put(keyFunc(fixed), fixed)
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


private def fixFieldType(cls: Class[?], field: _Field): _Field =
  if typeExists(field._type) then return field
  // no sense to process private fields in scope of 'java beans' (at least now)
  //if field.visibility == _Visibility.Private then field

  val foundJavaMethod: Option[java.lang.reflect.Method] = findJavaMethod(cls, field.name)
  if foundJavaMethod.isDefined /*&& foundJavaMethod.get.getReturnType != classOf[Object]*/ then
    return foundJavaMethod.map(javaMethod => changeType(field, javaMethod)).get

  val foundJavaField: Option[java.lang.reflect.Field] = findJavaField(cls, field.name)
  if foundJavaField.isDefined /*&& foundJavaField.get.getType != classOf[Object]*/ then
    return foundJavaField.map(javaField => changeType(field, javaField)).get

  field

private def fixMethodType(cls: Class[?], method: _Method): _Method =
  //if typeExists(method._type) then return method
  // no sense to process private fields in scope of 'java beans' (at least now)
  if method.visibility == _Visibility.Private
     || method.mainParams.isEmpty && method.returnType == _Type.VoidType
    then return method

  if method.mainParams.isEmpty && !method.returnType.isVoid && !method.hasExtraScalaParams then
    val m = findJavaMethod(cls, method.name)
    if m.isDefined then return changeReturnType(method, m.get)

  if method.mainParams.size == 1 && !method.hasExtraScalaParams then
    val m = findJavaMethodWithOneParam(cls, method.name)
    if m.isDefined then return changeFirstParamType(method, m.get)

  //if !method.modifiers.containsOneOf(_Modifier.ScalaStandardFieldAccessor,
  //  _Modifier.ScalaCustomFieldAccessor,
  //  _Modifier.JavaPropertyAccessor)
  //  // we need to have fixed only bean properties (since it is enough complicated in general)
  //  then return method

  method


private def changeType(field: _Field, jf: java.lang.reflect.Field): _Field =
  field.copy(
    _type = _Type(jf.getType.nn.getName.nn),
    genericType = field._type.toString,
    //genericType = jf.getGenericType.toString,
  )(field.internalValue)

private def changeType(field: _Field, jm: java.lang.reflect.Method): _Field =
  field.copy(
    _type = _Type(jm.getReturnType.nn.getName.nn),
    genericType = field._type.toString,
    //genericType = jm.getGenericReturnType.toString,
  )(field.internalValue)

private def changeReturnType(method: _Method, jm: java.lang.reflect.Method): _Method =
  method.copy(
    returnType = _Type(jm.getReturnType.nn.getName.nn),
    returnGenericType = method.returnType.toString,
    //resultGenericType = jm.getGenericReturnType.toString,
  )(method.internalValue)

private def changeFirstParamType(method: _Method, jm: java.lang.reflect.Method): _Method =
  require(method.mainParams.size == 1)
  require(jm.getParameterCount == 1)
  //method.copy(mainParams = List(_Type(jm.getParameterTypes.nnArray.apply(0).getName.nn)))(method.internalValue)
  val paramTypes = jm.getParameterTypes.nnArray
  method.copy(mainParams = List(_Type(paramTypes(0).getName.nn)))(method.internalValue)


val StandardTypes = Set(
  "byte", "Byte", "java.lang.Byte",
  "char", "Character", "java.lang.Character",
  "short", "Short", "java.lang.Short",
  "int", "Integer", "java.lang.Integer",
  "long", "Long", "java.lang.Long",
  "String", "java.lang.String",
)

private def typeExists(_type: _Type): Boolean =
  if StandardTypes.contains(_type.className) then return true
  try { Class.forName(_type.className); true }
  catch case _: Exception => false

def findJavaField(cls: Class[?], name: String): Option[java.lang.reflect.Field] =
  try return Option(cls.getField(name).nn) catch case _: Exception => { }

  var f: Option[java.lang.reflect.Field] = None
  var c: Class[?]|Null = cls
  while f.isEmpty && c.isNotNull && c != classOf[Object] && c != classOf[AnyRef] do
    f = try Option(c.nn.getDeclaredField(name).nn) catch case _: Exception => None
    c = c.nn.getSuperclass
  f


def findJavaMethod(cls: Class[?], name: String): Option[java.lang.reflect.Method] =
  val jMethodName = scalaMethodNameToJava(name)
  try return Option(cls.getMethod(jMethodName).nn) catch case _: Exception => { }

  var m: Option[java.lang.reflect.Method] = None
  var c: Class[?]|Null = cls
  while m.isEmpty && c.isNotNull && c != classOf[Object] && c != classOf[AnyRef] do
    m = try Option(c.nn.getDeclaredMethod(jMethodName).nn) catch case _: Exception => None
    c = c.nn.getSuperclass
  m


def findJavaMethodWithOneParam(cls: Class[?], name: String): Option[java.lang.reflect.Method] =
  val jMethodName = scalaMethodNameToJava(name)
  var m = findMethodWithOneParam(cls.getMethods, jMethodName)
  if m.isDefined then return m

  var c: Class[?] | Null = cls
  while m.isEmpty && c.isNotNull && c != classOf[Object] && c != classOf[AnyRef] do
    m = findMethodWithOneParam(cls.getDeclaredMethods, jMethodName)
    c = c.nn.getSuperclass
  m

private def findMethodWithOneParam(methods: Array[java.lang.reflect.Method|Null]|Null, methodName: String): Option[java.lang.reflect.Method] =
  val methodsWithOneParam = methods.nnArray.iterator
    .filter(m => m.getParameterCount == 1)
    .filter(_.getName == methodName)
    .toList
  if methodsWithOneParam.length == 1
    then Option(methodsWithOneParam.head)
    // Now I don't know how to choose one of several methods
    else None


private def scalaMethodNameToJava(methodName: String): String =
  methodName.replace("_=", "_$eq").nn
