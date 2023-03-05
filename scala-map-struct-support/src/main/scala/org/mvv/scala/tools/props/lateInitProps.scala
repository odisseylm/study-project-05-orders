package org.mvv.scala.tools.props

import scala.collection.mutable
import scala.annotation.targetName
import scala.compiletime.uninitialized
import scala.quoted.{ Expr, Quotes, Type, Varargs }
//
import org.mvv.scala.tools.{ Logger, tryDo }
import org.mvv.scala.tools.quotes.{ topClassOrModuleFullName, topMethodFullName, topMethodSimpleName }
import org.mvv.scala.tools.quotes.{ qClassNameOf, qClassName, qClassNameOfCompiled, Param }
import org.mvv.scala.tools.quotes.{ qStringLiteral, qFunction, qTuple2 }
import org.mvv.scala.tools.quotes.{ classExists, getSimpleClassName, getFullClassName, getCompanionClass }
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
  ${ currentClassIsInitializedPropsImpl( '{ Nil }, '{ "isInitialized" } ) }


//noinspection ScalaUnusedSymbol
inline def currentClassIsInitializedProps(
  inline isInitializedMethodOwners: String*
  ): IsInitializedProps =
  ${ currentClassIsInitializedPropsImpl('{ isInitializedMethodOwners }, '{ "isInitialized" }) }


//noinspection ScalaUnusedSymbol
inline def currentClassIsInitializedProps(
  inline isInitializedMethodOwners: List[String],
  inline isInitializedMethod: String,
  ): IsInitializedProps =
  ${ currentClassIsInitializedPropsImpl('{ isInitializedMethodOwners }, '{ isInitializedMethod }) }


def currentClassIsInitializedPropsImpl(using q: Quotes)(
  isInitializedMethodOwnersExpr: Expr[Seq[String]],
  isInitializedMethodExpr: Expr[String],
  ): Expr[IsInitializedProps] =
  import q.reflect.{ Term, ClassDef, ValDef, Statement }

  val log = Logger(topMethodFullName)

  val classDef: ClassDef = findSpliceOwnerClass()
    .getOrElse( throw IllegalStateException("Macros 'currentClassIsInitializedProps' should be placed inside class."))
  val body: List[Statement] = classDef.body

  val valDefs: List[ValDef] = body
    .flatMap { case vd: ValDef => Option(vd); case _ => None }
    .filter(toCheckInitState)

  val isInitializedMethodName: String = isInitializedMethodExpr.valueOrAbort //.getOrElse("isInitialized")
  val passedIsInitializedMethodOwnerClassNames: Seq[String] = isInitializedMethodOwnersExpr.valueOrAbort
  val allIsInitializedMethodOwnerClassNames: List[String] =
    (passedIsInitializedMethodOwnerClassNames.toList :+ getFullClassName(classDef)).distinct

  val ownerFullClassName = classDef.symbol.fullName
  val tuples: List[Term] = valDefs.map { vd =>
    qCreateIsInitTupleEntry (ownerFullClassName, vd) (isInitializedMethodName, allIsInitializedMethodOwnerClassNames*)
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
  import q.reflect.Term

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

  import q.reflect.{ Select, DefDef, TypeDef, Symbol }
  import org.mvv.scala.tools.quotes.{ qMethodType, getClassThisScopeTypeRepr }
  import org.mvv.scala.tools.quotes.symbolDetailsToString

  val log = Logger(topMethodFullName)
  val logPrefix = topMethodSimpleName

  val isInitializedOwnersWithCompanions: List[Symbol] = isInitializedOwners.view
    .distinct
    .filter(cls => classExists(cls))
    .map(cls => Symbol.classSymbol(cls))
    .flatMap(clsS => List(Option(clsS), getCompanionClass(clsS)))
    .filter(_.isDefined).map(_.get)
    .toList

  log.trace(s"$logPrefix isInitializedOwnersWithCompanions: $isInitializedOwnersWithCompanions")

  val allIsInitializedMethods = gatherIsInitializedFunctions(isInitializedFuncName, isInitializedOwnersWithCompanions)

  val theBestMethod: DefDef = findTheBestOfOverloadedMethods(allIsInitializedMethods, valDef)
  log.trace(s"$logPrefix valDef (${valDef.name}: ${valDef.tpt.tpe.show}), theBestMethod: ${theBestMethod.name}(${firstParamType(theBestMethod).show})")

  val theBestMethodOwner = theBestMethod.symbol.owner.fullName
  val funSelect = Select( qClassName(theBestMethodOwner.stripSuffix("$")), theBestMethod.symbol)

  log.trace(s"$logPrefix funSelect: $funSelect")
  funSelect



private def gatherIsInitializedFunctions(using q: Quotes)
  (isInitializedFuncName: String, funcOwners: List[q.reflect.Symbol])
  : List[q.reflect.DefDef] =
  import q.reflect.DefDef
  import org.mvv.scala.tools.quotes.symbolDetailsToString

  val log = Logger(topMethodFullName)
  val logPrefix = topMethodSimpleName

  val functions = funcOwners.distinct
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
  allIsInitializedMethods: List[q.reflect.DefDef], valDef: q.reflect.ValDef): q.reflect.DefDef =
  import q.reflect.{ TypeRepr, DefDef, report }

  val classOwnerName = valDef.symbol.owner.fullName
  val valueType = valDef.tpt.tpe

  require(allIsInitializedMethods.nonEmpty, "allIsInitializedMethods are empty." +
    " Please specify isInitializedMethodOwners in 'currentClassIsInitializedProps' macros" +
    s" or add isInitialized to $classOwnerName class or companion.")

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
  import q.reflect.{ TypeRepr, ParamClause, ValDef, TypeDef }
  import org.mvv.scala.tools.quotes.isBool
  import org.mvv.scala.tools.inspection._Quotes.hasExtraParams

  if defDef.hasExtraParams then return false

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
  import q.reflect.{ ParamClause, ValDef }

  val paramss: List[ParamClause] = defDef.paramss
  val valDefOList: List[ValDef] = paramss.flatMap(_.params)
    // mthfck... how to write it really easy??
    .flatMap { case vd: ValDef => Option(vd); case _ => None }

  require(valDefOList.sizeIs >= 1, s"Method [${defDef.name}] has no params.")

  val firstParamName = valDefOList.head.name
  val firstParamType = valDefOList.head.tpt.tpe
  (firstParamName, firstParamType)


/** Creates tuple like {{{ ( "prop1", () => isInitialized(prop1) ) }}} */
private def qCreateIsInitTupleEntry(using q: Quotes)
  (classFullName: String, valDef: q.reflect.ValDef)
  (isInitializedFuncName: String, isInitializedOwners: String*)
  : q.reflect.Term =
  import q.reflect.{ Symbol, Tree, Term, Select, Apply, Lambda, MethodType, This, TypeRepr, asTerm }
  import org.mvv.scala.tools.quotes.{ qMethodType, getClassThisScopeTypeRepr }

  val classSymbol = Symbol.classSymbol(classFullName)
  val valSelect = Select.unique(This(classSymbol), valDef.name)

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

  qTuple2[String, ()=>Boolean](qStringLiteral(valDef.name), isInitializedAnonFunLambda)
