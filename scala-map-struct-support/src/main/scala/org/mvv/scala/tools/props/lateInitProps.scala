package org.mvv.scala.tools.props

import scala.collection.mutable
import scala.annotation.targetName
import scala.compiletime.uninitialized
import scala.quoted.{ Expr, Quotes, Type, Varargs }
//
import org.mvv.scala.tools.{ Logger, tryDo }
import org.mvv.scala.tools.quotes.{ topClassOrModuleFullName, topMethodFullName, topMethodSimpleName }
import org.mvv.scala.tools.quotes.{ qClassNameOf, qClassName, qClassNameOfCompiled, Param }
import org.mvv.scala.tools.quotes.{ qStringLiteral, qFunction }
import org.mvv.scala.tools.quotes.{ classExists, getSimpleClassName }
import org.mvv.scala.tools.quotes.{ findSpliceOwnerClass, getClassThisScopeTypeRepr }


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

  val log = Logger(topMethodFullName)

  val classDef: ClassDef = findSpliceOwnerClass()
    .getOrElse( throw IllegalStateException("Macros 'currentClassIsInitializedProps' should be placed inside class."))
  val body: List[Statement] = classDef.body

  val valDefs: List[ValDef] = body
    .flatMap { case vd: ValDef => Option(vd); case _ => None }
    .filter(toCheckInitState)

  val ownerFullClassName = classDef.symbol.fullName
  val tuples: List[Term] = valDefs.map { vd =>
    termIsInitTupleEntry (ownerFullClassName, vd) ("isInitialized", "org.mvv.scala.tools.props.IsInitialized")
  }

  val tuplesExprs = tuples.map(_.asExprOf[(String,()=>Boolean)])
  val exprOfTupleList = Expr.ofList(tuplesExprs)

  log.info(s"$topMethodSimpleName: ${exprOfTupleList.show}\n$exprOfTupleList")

  exprOfTupleList



private val SkipUninitializedCheckAnnotationNames: Set[String] = Set(
  "SkipLateInitCheck", "SkipUninitializedCheck", "SkipInitializedCheck",
  "skipLateInitCheck", "skipUninitializedCheck", "skipInitializedCheck",
)



def toCheckInitState(using q: Quotes)(valDef: q.reflect.ValDef): Boolean =
  import q.reflect.*

  val annotations: List[Term] = valDef.symbol.annotations
  val annotationNames: List[String] = annotations.map(an => getSimpleClassName(an.tpe.show))

  val hasSkipAnnotation = annotationNames.exists { anName =>
    SkipUninitializedCheckAnnotationNames.contains(anName) }
  !hasSkipAnnotation



def findIsInitializedFunction(using q: Quotes)
  (thisType: q.reflect.TypeRepr)
  (valDef: q.reflect.ValDef)
  (isInitializedFuncName: String, isInitializedOwners: String*)
  : q.reflect.Term =

  import q.reflect.{ Select, DefDef, TypeDef }
  import org.mvv.scala.tools.quotes.{ qMethodType, getClassThisScopeTypeRepr }
  import org.mvv.scala.tools.quotes.symbolDetailsToString

  val log = Logger(topMethodFullName)
  val logPrefix = topMethodSimpleName

  //val typeTree = TypeTree.ref(thisType.typeSymbol)

  val isInitializedOwnersWithCompanions = isInitializedOwners
    .flatMap(cls => List(cls, cls + "$"))
    .filter(cls => classExists(cls))

  log.trace(s"$logPrefix isInitializedOwnersWithCompanions: $isInitializedOwnersWithCompanions")

  val allIsInitializedMethods = gatherIsInitializedFunctions(isInitializedFuncName, isInitializedOwnersWithCompanions*)

  val theBestMethod: DefDef = findTheBestOfOverloadedMethods(allIsInitializedMethods, valDef.tpt.tpe)
  log.trace(s"$logPrefix valDef (${valDef.name}: ${valDef.tpt.tpe.show}), theBestMethod: ${theBestMethod.name}(${firstParamType(theBestMethod).show})")

  val theBestMethodOwner = theBestMethod.symbol.owner.fullName
  val funSelect = Select( qClassName(theBestMethodOwner.stripSuffix("$")), theBestMethod.symbol)

  log.trace(s"$logPrefix funSelect: $funSelect")
  funSelect



private def gatherIsInitializedFunctions(using q: Quotes)
  (isInitializedFuncName: String, funcOwners: String*)
  : List[q.reflect.DefDef] =
  import q.reflect.*
  import org.mvv.scala.tools.quotes.symbolDetailsToString

  val log = Logger(topMethodFullName)
  val logPrefix = topMethodSimpleName

  val functions = funcOwners.distinct
    .map(funcOwner => Symbol.classSymbol(funcOwner))
    // I do not know how to filter/distinguish ?incorrect? object/class method owner
    // since these incorrect symbols return isType/isClassDef/exists,
    // only flags=EmptyFlags but it is not reliable sign
    // for that reason I have to use there try/catch (tryDo)
    .filter(s => !s.isNoSymbol)
    .map { s => log.trace(s"$logPrefix funcOwner ${symbolDetailsToString(s)}"); s }
    .flatMap(s => tryDo{ s.methodMembers } .getOrElse(Nil))
    .filter(_.isDefDef) .map(_.tree match { case m: DefDef => m })
    .filter(_.name == isInitializedFuncName)
    .filter(defDef => isSimpleBoolMethodWithOneParamOfAnyType(defDef))
    .toList
  functions



private def findTheBestOfOverloadedMethods(using q: Quotes)(
  allIsInitializedMethods: List[q.reflect.DefDef], valueType: q.reflect.TypeRepr): q.reflect.DefDef =
  import q.reflect.*

  require(allIsInitializedMethods.nonEmpty, "allIsInitializedMethods are empty")

  val methodsWithSuitableTypes = allIsInitializedMethods
    .filter(m =>
      val mParamType: TypeRepr = firstParamType(m)
      val isSuitable = valueType <:< mParamType
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

  val withoutSuperTypes = methodsWithSuitableTypes.toSet -- withSuperTypes
  require(withoutSuperTypes.nonEmpty, s"Strange, all methods for type ${valueType.show} have type which are super-type of others...")

  if withoutSuperTypes.sizeIs != 1 then
    val valStr = s"${valueType.show}"
    val methodsAsStr = withoutSuperTypes.map(m => s"${m.symbol.fullName}(${firstParamType(m).show})")
    report.errorAndAbort(s"Ambiguous methods for $valStr $methodsAsStr")

  withoutSuperTypes.head



// T O D O: test with generics
private def isSimpleBoolMethodWithOneParamOfAnyType(using q: Quotes)(defDef: q.reflect.DefDef): Boolean =
  import q.reflect.*
  import org.mvv.scala.tools.beans.hasExtraParams
  import org.mvv.scala.tools.quotes.isBool

  if hasExtraParams(defDef) then return false

  val returnType: TypeRepr = defDef.returnTpt.tpe
  if !returnType.isBool then return false

  val paramss: List[ParamClause] = defDef.paramss
  val valOrTypeDefList: List[ValDef|TypeDef] = paramss.flatMap(_.params)

  val valDefOList: List[ValDef] = valOrTypeDefList.view
    // mthfck... how to write it really easy??
    .flatMap { case vd: ValDef => Option(vd); case _ => None }
    .toList

  valDefOList.sizeIs == 1



private def firstParamType(using q: Quotes)(defDef: q.reflect.DefDef): q.reflect.TypeRepr =
  firstParam(defDef)._2


private def firstParam(using q: Quotes)(defDef: q.reflect.DefDef): (String, q.reflect.TypeRepr) =
  import q.reflect.*

  val paramss: List[ParamClause] = defDef.paramss
  val valDefOList: List[ValDef] = paramss.flatMap(_.params)
    // mthfck... how to write it really easy??
    .flatMap { case vd: ValDef => Option(vd); case _ => None }

  require(valDefOList.sizeIs >= 1, s"Method [${defDef.name}] has no params.")

  val firstParamName = valDefOList.head.name
  val firstParamType = valDefOList.head.tpt.tpe
  (firstParamName, firstParamType)



private def termIsInitTupleEntry(using q: Quotes)
  (classFullName: String, valDef: q.reflect.ValDef)
  (isInitializedFuncName: String, isInitializedOwners: String*)
  : q.reflect.Term =
  import q.reflect.*
  import org.mvv.scala.tools.quotes.{ qMethodType, getClassThisScopeTypeRepr }

  val classSymbol = Symbol.classSymbol(classFullName)
  val valSelect = Select.unique(This(classSymbol), valDef.name)
  //val valDefType = valDef.tpt.tpe

  //val scopeTypRepr: TypeRepr = getClassThisScopeTypeRepr(classSymbol)
  //val isInitializedTerRef = TermRef(scopeTypRepr, "isInitialized")
  //val isInitializedIdentTerm = Ident(isInitializedTerRef)
  //val isInitializedApply = Apply(isInitializedIdentTerm, List(valSelect))

  val _this = getClassThisScopeTypeRepr(classSymbol)
  val isInitializedF: Term = findIsInitializedFunction(_this)(valDef)
    (isInitializedFuncName, isInitializedOwners*)

  val isInitializedApply = Apply(isInitializedF, List(valSelect))

  val rhsFn: (Symbol, List[Tree]) => Tree = (_: Symbol, _: List[Tree]) => { isInitializedApply }
  val isInitializedAnonFunLambda = Lambda(
    Symbol.spliceOwner,
    MethodType(Nil)(_ => Nil, _ => TypeRepr.of[Boolean]),
    rhsFn
  )

  val stringLiteral: Term = qStringLiteral(valDef.name)
  val funLambda: Term = isInitializedAnonFunLambda

  val tupleExpr: Expr[(String, ()=>Boolean)] = Expr.ofTuple( // qStringValueTuple2(valDef.name, isInitializedAnonFunLambda)
    (stringLiteral.asExprOf[String], funLambda.asExprOf[()=>Boolean]) )
  tupleExpr.asTerm