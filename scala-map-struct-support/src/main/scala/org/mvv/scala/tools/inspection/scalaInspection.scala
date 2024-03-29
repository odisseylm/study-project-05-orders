package org.mvv.scala.tools.inspection

import scala.annotation.{nowarn, targetName}
import scala.collection.mutable
import scala.quoted.Quotes
//
import org.mvv.scala.tools.{ endsWithOneOf, tryDo }
import org.mvv.scala.tools.CollectionsOps.containsOneOf
import org.mvv.scala.tools.quotes.{ classNameOf, isNullType, asClassDef, asDefDef, activeSymbolFlags }
import org.mvv.scala.tools.inspection._Quotes.{ extractType, classKind }
import org.mvv.scala.tools.inspection.{ _Field, _Method, _Modifier, _Type, _Visibility }



object _Quotes :

  def typeFromString(typeName: String): _Type = _Type(typeName)


  def extractType(using q: Quotes)(el: q.reflect.Tree): _Type =
    el match
      case typeTree: q.reflect.TypeTree => extractTypeTreeType(typeTree)
      case _ => extractTreeType(el)


  def typeReprToRuntimeType(using q: Quotes)(typeRepr: q.reflect.TypeRepr): String =
    typeRepr.dealias.widen.dealias.show

  // TODO: TypeRepr.dealias.widen.show returns java type. Also we need to store original scala type which is returned by .show

  def extractTreeType(using q: Quotes)(el: q.reflect.Tree): _Type =
    val symbol = el.symbol
    val clsStr = if symbol.isType
      then typeReprToRuntimeType(symbol.typeRef) // we also can use symbol.fullName
      else symbol.fullName.stripSuffix(".<init>")
    typeFromString(clsStr)

  private def extractTypeTreeType(using q: Quotes)(typeTree: q.reflect.TypeTree): _Type =
    import q.reflect.*
    extractTypeOfTypeRepr(typeTree.tpe)


  //noinspection NoTailRecursionAnnotation // no need of tail-recursion
  private def extractTypeOfTypeRepr(using q: Quotes)(typeRepr: q.reflect.TypeRepr): _Type =
    import q.reflect.*
    typeRepr match
      case orType: OrType =>
        val typeStr = List(orType.left, orType.right)
          // special case of java (with explicit null) - lets ignore Null part
          .filter(t => !t.isNullType)
          .map(t => typeReprToRuntimeType(t))
          .mkString("|")
        typeFromString(typeStr)
      case andType: AndType =>
        val typeStr = List(andType.left, andType.right)
          .map(t => typeReprToRuntimeType(t))
          .mkString("&")
        typeFromString(typeStr)
      case annotType: AnnotatedType =>
        extractTypeOfTypeRepr(annotType.underlying)
      case other =>
        typeFromString(typeReprToRuntimeType(other))


  def visibility(using q: Quotes)(el: q.reflect.Tree): _Visibility =
    import q.reflect.Flags
    val flags: Flags = el.symbol.flags
    flags match
      case _ if flags.is(Flags.Private)      => _Visibility.Private
      case _ if flags.is(Flags.PrivateLocal) => _Visibility.Private
      case _ if flags.is(Flags.Local)        => _Visibility.Other
      case _ if flags.is(Flags.Protected)    => _Visibility.Protected
      case _ => _Visibility.Public


  @nowarn("msg=method Static in trait FlagsModule is deprecated")
  private def generalModifiers (using q: Quotes)
    (symbol: q.reflect.Symbol)
    : Set[_Modifier] =

    import q.reflect.Flags
    val m = mutable.Set[_Modifier]()
    val flags: Flags = symbol.flags

    if flags.is(Flags.FieldAccessor)   then m.addOne(_Modifier.ScalaStandardFieldAccessor)
    if flags.is(Flags.ParamAccessor)   then m.addOne(_Modifier.ParamAccessor)
    if flags.is(Flags.ExtensionMethod) then m.addOne(_Modifier.ExtensionMethod)
    if flags.is(Flags.Transparent)     then m.addOne(_Modifier.Transparent)
    if flags.is(Flags.Macro)           then m.addOne(_Modifier.Macro)
    if flags.is(Flags.JavaStatic) || flags.is(Flags.Static)
                                       then m.addOne(_Modifier.Static)
    Set.from(m)


  extension (using q: Quotes) (valDef: q.reflect.ValDef)
    def toField: _Field =
      val valName = valDef.name
      val fieldType = extractType(valDef.tpt)
      val mod: Set[_Modifier] = generalModifiers(valDef.symbol)
      _Field(valName, visibility(valDef), mod, fieldType)(valDef)


  extension (using q: Quotes) (defDef: q.reflect.DefDef)

    def isOverriddenFromJava: Boolean =
      import q.reflect.{ Symbol, Flags }
      val allOverriddenSymbols: List[Symbol] = defDef.symbol.allOverriddenSymbols.toList
      val javaSuperMethod: Option[Symbol] = allOverriddenSymbols.find(s => s.flags.is(Flags.JavaDefined))
      javaSuperMethod.nonEmpty

    def hasJavaBeanPropertyAnnotations: Boolean =
      import q.reflect.{ Symbol, Flags }
      val annotationsNames: List[String] = defDef.symbol.annotations.map(_.tpe.show)
      val hasJavaBeanProperty = annotationsNames.containsOneOf(
        classNameOf[scala.beans.BeanProperty], classNameOf[scala.beans.BooleanBeanProperty])
      hasJavaBeanProperty


    def isJavaDefined: Boolean =
      import q.reflect.Flags
      defDef.symbol.flags.is(Flags.JavaDefined)


    def hasExtraParams: Boolean =

      val paramTypes: List[_Type] = paramssToTypes(defDef.paramss)
      val trailingParamTypes: List[_Type] = paramssToTypes(defDef.trailingParamss)
      val termParamsTypes: List[_Type] = paramssToTypes(defDef.termParamss)

      val _hasExtraParams =
        (!isListDeeplyEmpty(defDef.trailingParamss) && trailingParamTypes != paramTypes)
          || (!isListDeeplyEmpty(defDef.termParamss) && termParamsTypes != paramTypes)
          || defDef.leadingTypeParams.nonEmpty
      _hasExtraParams


    def toMethod: _Method =
      val methodName: String = tryDo(defDef.name) .getOrElse(defDef.symbol.name)
      val returnType = extractType(defDef.returnTpt)

      val paramTypes: List[_Type] = paramssToTypes(defDef.paramss)
      val _hasExtraParams = defDef.hasExtraParams

      var modifiers: Set[_Modifier] = generalModifiers(defDef.symbol)

      if !_hasExtraParams && methodIsJavaPropertyAccessor(methodName, returnType, paramTypes.size)
        && (defDef.isJavaDefined || defDef.isOverriddenFromJava || defDef.hasJavaBeanPropertyAnnotations)
        then modifiers += _Modifier.JavaPropertyAccessor

      if !modifiers.contains(_Modifier.ScalaStandardFieldAccessor) && !_hasExtraParams then
        val isCustomGetter = defDef.paramss.isEmpty && !returnType.isVoid
        val isCustomSetter = paramTypes.sizeIs == 1 && methodName.endsWithOneOf("_=", "_$eq")
        if isCustomGetter || isCustomSetter then modifiers += _Modifier.ScalaCustomFieldAccessor

      _Method(methodName, visibility(defDef), Set.from(modifiers), returnType, paramTypes, _hasExtraParams)(defDef)

  end extension

  extension (using q: Quotes)(classDef: q.reflect.ClassDef)
    def classKind: ClassKind =
      import q.reflect.Flags
      classDef.symbol.flags match
        case f if f.is(Flags.JavaDefined) => ClassKind.Java
        case f if f.is(Flags.Scala2x) => ClassKind.Scala2
        case _  => ClassKind.Scala3


private def isListDeeplyEmpty(using q: Quotes)(paramsOfParams: List[q.reflect.ParamClause]) =
  paramsOfParams.flatMap(_.params).isEmpty

private def paramToType(using q: Quotes)(p: q.reflect.ValDef | q.reflect.TypeDef): _Type =
  import q.reflect.{TypeDef, ValDef}
  p match
    case vd: ValDef  => extractType(vd.tpt)
    case td: TypeDef => extractType(td) // T O D O: probably it is not tested

def paramssToTypes(using q: Quotes)(paramss: List[q.reflect.ParamClause]): List[_Type] =
  if paramss.sizeIs == 1 && paramss.head.params.isEmpty
    then Nil // case with non field-accessor but without params
    else paramss .map(_.params.map(paramToType) .mkString("|")) .map(v => _Type(v))



def methodIsJavaGetAccessor(methodName: String, returnType: _Type, paramCount: Int) =
  nameHasPrefix(methodName, "get") && paramCount == 0 && !returnType.isVoid

def methodIsJavaIsAccessor(methodName: String, returnType: _Type, paramCount: Int) =
  nameHasPrefix(methodName, "is") && paramCount == 0 && returnType.isBool

def methodIsJavaSetAccessor(methodName: String, returnType: _Type, paramCount: Int) =
  nameHasPrefix(methodName, "set") && paramCount == 1 && returnType.isVoid

def methodIsJavaPropertyAccessor(methodName: String, returnType: _Type, paramCount: Int) =
  methodIsJavaGetAccessor(methodName, returnType, paramCount)
    || methodIsJavaIsAccessor(methodName, returnType, paramCount)
    || methodIsJavaSetAccessor(methodName, returnType, paramCount)


def nameHasPrefix(methodName: String, prefix: String): Boolean =
  methodName.startsWith(prefix) && methodName.length > prefix.length
