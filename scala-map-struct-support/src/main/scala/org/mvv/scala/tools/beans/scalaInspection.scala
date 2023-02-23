package org.mvv.scala.tools.beans

import scala.collection.mutable
import scala.annotation.nowarn
import scala.quoted.Quotes
//
import org.mvv.scala.tools.{ tryDo, endsWithOneOf }
import _Quotes.extractType



object _Quotes :

  // TODO: fdfd
  def typeFromString(typeName: String): _Type =
    //val runtimeClassOption = tryDo { loadClass(typeName).getName.nn }
    _Type(typeName) // , runtimeClassOption)


  def extractType(using q: Quotes)(el: q.reflect.Tree): _Type =
    val symbol = el.symbol
    val clsStr = if symbol.isType
      then symbol.typeRef.dealias.widen.dealias.show // we also can use symbol.fullName
      else symbol.fullName.stripSuffix(".<init>")
    typeFromString(clsStr)


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
      //import q.reflect.
      val valName = valDef.name
      val mod: Set[_Modifier] = generalModifiers(valDef.symbol)
      val fieldType = extractType(valDef.tpt)
      _Field(valName, visibility(valDef), mod, fieldType)(valDef)


  extension (using q: Quotes) (defDef: q.reflect.DefDef)
    def toMethod: _Method =
      //import q.reflect.??

      try defDef.name catch case _: Exception =>
        println(s"Bad element $defDef")
        //printTreeSymbolInfo(el)
        //printFields("Bad element", el)

      // 'name' cam throw error?!
      val methodName: String = tryDo(defDef.name) .getOrElse(defDef.symbol.name)
      val returnType = extractType(defDef.returnTpt)

      val paramTypes: List[_Type] = paramssToTypes(defDef.paramss)
      val trailingParamTypes: List[_Type] = paramssToTypes(defDef.trailingParamss)
      val termParamsTypes: List[_Type] = paramssToTypes(defDef.termParamss)

      val hasExtraParams =
             (!isListDeeplyEmpty(defDef.trailingParamss) && trailingParamTypes != paramTypes)
          || (!isListDeeplyEmpty(defDef.termParamss) && termParamsTypes != paramTypes)
          || defDef.leadingTypeParams.nonEmpty

      var modifiers: Set[_Modifier] = generalModifiers(defDef.symbol)
      if !modifiers.contains(_Modifier.ScalaStandardFieldAccessor) && !hasExtraParams then
        val isCustomGetter = defDef.paramss.isEmpty && returnType != Types.UnitType
        val isCustomSetter = paramTypes.sizeIs == 1 && methodName.endsWithOneOf("_=", "_$eq")
        if isCustomGetter || isCustomSetter then modifiers += _Modifier.ScalaCustomFieldAccessor

      _Method(methodName, visibility(defDef), Set.from(modifiers), returnType, paramTypes, hasExtraParams)(defDef)

  end extension




private def isListDeeplyEmpty(using q: Quotes)(paramsOfParams: List[q.reflect.ParamClause]) =
  paramsOfParams.flatMap(_.params).isEmpty

private def paramToType(using q: Quotes)(p: q.reflect.ValDef | q.reflect.TypeDef): _Type =
  import q.reflect.{ TypeDef, ValDef }
  p match
    case vd: ValDef  => extractType(vd.tpt)
    case td: TypeDef => extractType(td) // T O D O: probably it is not tested

def paramssToTypes(using q: Quotes)(paramss: List[q.reflect.ParamClause]): List[_Type] =
  if paramss.sizeIs == 1 && paramss.head.params.isEmpty
    then Nil // case with non field-accessor but without params
    else paramss .map(_.params.map(paramToType) .mkString("|")) .map(v => _Type(v))
