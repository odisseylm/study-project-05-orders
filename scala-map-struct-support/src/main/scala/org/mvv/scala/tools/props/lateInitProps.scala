package org.mvv.scala.tools.props

import scala.collection.mutable
import scala.annotation.targetName
import scala.compiletime.uninitialized
import scala.quoted.{ Expr, Quotes, Type, Varargs }
//
import org.mvv.scala.tools.{ Logger, tryDo }
import org.mvv.scala.tools.quotes.{ qClassNameOf, qClassName, qClassNameOfCompiled, qFunction, Param }
import org.mvv.scala.tools.quotes.{ topClassOrModuleFullName, qStringLiteral, getClassThisScopeTypeRepr }


/**
 * This approach expects easy isIntInitialized function accessed in scope of macro expansion
 * {{{
 *   def isInitialized(v: AnyRef): Boolean = (v != null)
 *   def isInitialized(v: Int): Boolean = (v != 0)
 * }}}
 *
 * We could probably use more flexible signatures like but I do not see big use of it.
 * {{{
 *   @targetName("isIntInitialized")
 *   def isInitialized(v: NamedValue[AnyRef]): Boolean = ???
 *   @targetName("isOptionInitialized")
 *   def isInitialized(v: NamedValue[Int]): Boolean = ???
 *   @targetName("isAnyRefInitialized")
 *   ...
 *   @targetName("isClass2Initialized")
 *   def isInitialized(v: NamedValue[Class2]): Boolean = ???
 * }}}
 */



type IsInitializedProps = List[(String, ()=>Boolean)]


inline def currentClassIsInitializedProps: IsInitializedProps =
  ${ currentClassIsInitializedPropsImpl }

def currentClassIsInitializedPropsImpl(using q: Quotes): Expr[IsInitializedProps] =
  import q.reflect.*

  val log = Logger(topClassOrModuleFullName)

  val classDef: ClassDef = find1stOwnerClass().get
  val body: List[Statement] = classDef.body

  val valDefs: List[ValDef] = body.map { stat =>
      stat match
        case vd: ValDef => Option(vd).filter(toCheckInitState)
        case _ => None // ignore
    }
    .filter(_.isDefined).map(_.get)

  val ownerFullClassName = classDef.symbol.fullName

  val tuples: List[Term] = valDefs.map(vd => termIsInitTupleEntry(ownerFullClassName, vd))
  val tuplesExprs = tuples.map(_.asExprOf[(String,()=>Boolean)])
  val exprOfTupleList = Expr.ofList(tuplesExprs)

  log.info(s"currentClassIsInitializedProps: ${exprOfTupleList.show}\n$exprOfTupleList")

  exprOfTupleList



def toCheckInitState(using q: Quotes)(valDef: q.reflect.ValDef): Boolean =
  //???
  // TODO: use annotation
  true



def qTuple2(using q: Quotes)(v1: q.reflect.Term, v2: q.reflect.Term): q.reflect.Term =
  import q.reflect.*
  val tuple2ClassTerm: Term = qClassNameOfCompiled[Tuple2[Any,Any]]
  val tuple2ApplySelect = Select.unique(tuple2ClassTerm, "apply")
  val tuple2TypeApply = TypeApply(tuple2ApplySelect, List(TypeTree.of[String], TypeTree.of[()=>Boolean]))
  val tupleApply = Apply(tuple2TypeApply, List(v1, v2))
  tupleApply



def qStringValueTuple2(using q: Quotes)(str: String, value: q.reflect.Term): q.reflect.Term =
  qTuple2(qStringLiteral(str), value)



private def find1stOwnerClass(using q: Quotes)(): Option[q.reflect.ClassDef] =
  import q.reflect.{ Symbol, ClassDef }

  var s: Symbol = Symbol.spliceOwner
  while s != Symbol.noSymbol && !s.isClassDef do
    s = s.maybeOwner

  if s.isClassDef then tryDo { s.tree match { case cd: ClassDef => cd } } else None



def qFunction22(using q: Quotes)
  (thisType: q.reflect.TypeRepr)
  //(functionName: String)
  (valueType: q.reflect.TypeRepr )
  //(params: Param[q.reflect.TypeRepr]*)
  //(returnType: q.reflect.TypeRepr)
  : q.reflect.Term =
  import q.reflect.* //{ Select, Symbol }
  import org.mvv.scala.tools.quotes.{ qMethodType, getClassThisScopeTypeRepr }
  import org.mvv.scala.tools.quotes.{ symbolToString, SymbolDetails }

  val typeTree = TypeTree.ref(thisType.typeSymbol)

  //val declaredMethods1 = thisType.typeSymbol.declaredMethods
  //println(s"%%% thisType1: $thisType")
  //println(s"%%% declaredMethods1: ${thisType.typeSymbol.declaredMethods}")

  //println(s"%%% thisType1: $thisType")
  //println(s"%%% declaredMethods1: ${typeTree.symbol.declaredMethods}")

  //val ss = Symbol.classSymbol("org.mvv.scala.tools.props.IsInitialized")
  //println(s"%%% 656565656: ${symbolToString(using q)(ss, SymbolDetails.Base, SymbolDetails.List, SymbolDetails.Tree )}")
  //println(s"%%% -------------------------------------------------------------")

  val ss2 = Symbol.classSymbol("org.mvv.scala.tools.props.IsInitialized$")
  //println(s"%%% 656565656: ${symbolToString(using q)(ss2, SymbolDetails.Base, SymbolDetails.List, SymbolDetails.Tree )}")
  //println(s"%%% -------------------------------------------------------------")

  val defDefList: List[DefDef] = ss2.methodMembers
    .filter(_.isDefDef) .map( _.tree match { case m: DefDef => m } )
  val allIsInitializedMethods = defDefList
    .filter(_.name == "isInitialized")
    .filter(defDef => isSimpleBoolMethodWithOneParamOfAnyType(defDef))

  val theBestMethod: DefDef = findTheBestMethod(allIsInitializedMethods, valueType)
  println(s"%%% valDef.type: ${valueType.show}, theBestMethod: ${theBestMethod.name}(${firstParamType(theBestMethod).show})  ${theBestMethod}")


  //println(s"%%% 656565656  m333(${allIsInitializedMethods.size}): $allIsInitializedMethods")

  //println(s"%%% 656565656  m333: isDefDef = ${allIsInitializedMethods.head.isDefDef}")


  //getClassThisScopeTypeRepr
  //val methodType = qMethodType(params*)(returnType)
  //TypeTree()

  //Symbol.newMethod(Symbol.noSymbol, methodSimpleName, methodType)

  //val (methodOwner, methodSimpleName) = extractFullMethodNameComponents(functionFullName)
  //val funSelect = Select( qClassName(methodOwner), Symbol.newMethod(Symbol.noSymbol, methodSimpleName, methodType) )
  //???//funSelect

  val _firstParamType: TypeRepr = firstParamType(theBestMethod)

  val asMethodType = qMethodType(Param("v", _firstParamType))(TypeRepr.of[Boolean])
  val mmm444 = Symbol.newMethod(Symbol.noSymbol, theBestMethod.name, asMethodType)

  //val funSelect = Select( qClassName(ss2.fullName), theBestMethod.symbol)
  //val funSelect = Select( qClassName(ss2.fullName), mmm444)
  //val funSelect = Select( qClassName("org.mvv.scala.tools.props.IsInitialized$"), mmm444)
  val funSelect = Select( qClassName("org.mvv.scala.tools.props.IsInitialized"), mmm444)
  println(s"%%% 77777 funSelect: $funSelect")
  funSelect


private def findTheBestMethod(using q: Quotes)(
  allIsInitializedMethods: List[q.reflect.DefDef], valueType: q.reflect.TypeRepr): q.reflect.DefDef =
  import q.reflect.*

  require(allIsInitializedMethods.nonEmpty, "allIsInitializedMethods are empty")

  val methodsWithSuitableTypes = allIsInitializedMethods
    .filter(m =>
      val mParamType: TypeRepr = firstParamType(m)
      val isSuitable = (valueType <:< mParamType)
      isSuitable
    )

  val withSuperTypes = mutable.Set[DefDef]()

  for m1 <- methodsWithSuitableTypes
      m2 <- methodsWithSuitableTypes
    do
      if m1 != m2 then
        val m1ParamType: TypeRepr = firstParamType(m1)
        val m2ParamType: TypeRepr = firstParamType(m2)

        if m1ParamType <:< m2ParamType then withSuperTypes.addOne(m2)
        if m2ParamType <:< m1ParamType then withSuperTypes.addOne(m1)

  val withoutSuperTypes = (methodsWithSuitableTypes.toSet -- withSuperTypes)
  require(withoutSuperTypes.nonEmpty, "Strange, all methods have type which are super-type of others...")

  if withoutSuperTypes.sizeIs != 1 then
    val valStr = s"${valueType.show}"
    val methodsAsStr = withoutSuperTypes.map(m => s"${m.symbol.fullName}(${firstParamType(m).show})")
    report.errorAndAbort(s"Ambiguous methods for $valStr $methodsAsStr")

  withoutSuperTypes.head



// TODO: test with generics
private def isSimpleBoolMethodWithOneParamOfAnyType(using q: Quotes)(defDef: q.reflect.DefDef): Boolean =
  import q.reflect.*

  import org.mvv.scala.tools.beans.hasExtraParams

  println(s"%%% isSimpleBoolMethodWithOneParamOfAnyType 01  ${defDef.name}")

  if hasExtraParams(defDef) then return false

  println(s"%%% isSimpleBoolMethodWithOneParamOfAnyType 02")

  val returnType: TypeRepr = defDef.returnTpt.tpe
  println(s"%%% isSimpleBoolMethodWithOneParamOfAnyType 02222   returnType: ${returnType.show}, expected: ${TypeRepr.of[Boolean].show}")
  println(s"%%% isSimpleBoolMethodWithOneParamOfAnyType 02223   returnType == TypeRepr.of[Boolean]: ${returnType == TypeRepr.of[Boolean]}")
  println(s"%%% isSimpleBoolMethodWithOneParamOfAnyType 02223   returnType != TypeRepr.of[Boolean]: ${returnType != TypeRepr.of[Boolean]}")
  println(s"%%% isSimpleBoolMethodWithOneParamOfAnyType 02223   returnType =:= TypeRepr.of[Boolean]: ${returnType =:= TypeRepr.of[Boolean]}")

  val isBoolReturnType = (returnType == TypeRepr.of[Boolean]) || (returnType =:= TypeRepr.of[Boolean])
  if !isBoolReturnType then return false

  println(s"%%% isSimpleBoolMethodWithOneParamOfAnyType 03")

  val paramss: List[ParamClause] = defDef.paramss
  val valDefOrTypeDefList: List[ValDef|TypeDef] = paramss.flatMap(_.params)

  println(s"%%% isSimpleBoolMethodWithOneParamOfAnyType 04 valDefOrTypeDefList(${valDefOrTypeDefList.size}): ${valDefOrTypeDefList}")

  val valDefOList: List[ValDef] = valDefOrTypeDefList
    .filter ( _ match { case _: ValDef => true; case _ => false} )
    .map { case vd: ValDef => vd }

  println(s"%%% isSimpleBoolMethodWithOneParamOfAnyType 05 valDefOList(${valDefOList.size}): ${valDefOList}")

  valDefOList.sizeIs == 1


private def firstParamType(using q: Quotes)(defDef: q.reflect.DefDef): q.reflect.TypeRepr =
  import q.reflect.*

  val paramss: List[ParamClause] = defDef.paramss
  val valDefOrTypeDefList: List[ValDef | TypeDef] = paramss.flatMap(_.params)
  val valDefOList: List[ValDef] = valDefOrTypeDefList
    .filter(_ match
      case _: ValDef => true
      case _ => false
    )
    .map { case vd: ValDef => vd }
  require(valDefOList.sizeIs >= 1, s"Method [${defDef.name}] has no params.")

  val firstParamType = valDefOList.head.tpt.tpe
  firstParamType



private def termIsInitTupleEntry(using q: Quotes)(classFullName: String, valDef: q.reflect.ValDef): q.reflect.Term =
  import q.reflect.*
  import org.mvv.scala.tools.quotes.{ qMethodType, getClassThisScopeTypeRepr }


  val classSymbol = Symbol.classSymbol(classFullName)
  val valSelect = Select.unique(This(classSymbol), valDef.name)
  val valDefType = valDef.tpt.tpe

  val scopeTypRepr: TypeRepr = getClassThisScopeTypeRepr(classSymbol)

  //val isInitializedTerRef = TermRef(scopeTypRepr, "isInitialized")
  //val isInitializedIdentTerm = Ident(isInitializedTerRef)
  //val isInitializedApply = Apply(isInitializedIdentTerm, List(valSelect))

  //val _this = getClassThisScopeTypeRepr(classSymbol)
  //qFunction22(scopeTypRepr)("isInitialized")

  //val isInitializedF = qFunction("org.mvv.scala.tools.props.LateInitPropsTest$package.isInitialized")
  //  //(Param("v", TypeRepr.of[AnyVal]))(TypeRepr.of[Unit])
  //  //(Param("v", TypeRepr.of[Option[Any]]))(TypeRepr.of[Unit])
  //  (Param("v", valDef.tpt.tpe))(TypeRepr.of[Unit])

  val _this = getClassThisScopeTypeRepr(classSymbol)
  val isInitializedF: Term = qFunction22(_this)(valDefType)//("isInitialized")
  //val isInitializedF = TermRef( qFunction22(_this)//("isInitialized")

  val isInitializedApply = Apply(isInitializedF, List(valSelect))

  val rhsFn: (Symbol, List[Tree]) => Tree = (_: Symbol, _: List[Tree]) => { isInitializedApply }
  val isInitializedAnonFunLambda = Lambda(
    Symbol.spliceOwner,
    MethodType(Nil)(_ => Nil, _ => TypeRepr.of[Boolean]),
    rhsFn
  )

  val tuple = qStringValueTuple2(valDef.name, isInitializedAnonFunLambda)
  tuple


//extension (using q: Quotes)(typeRepr: q.reflect.TypeRepr)
//  def isBool: Boolean =
