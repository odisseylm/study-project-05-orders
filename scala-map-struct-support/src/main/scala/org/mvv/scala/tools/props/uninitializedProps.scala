package org.mvv.scala.tools.props

import scala.collection.mutable
import scala.annotation.{nowarn, targetName}
import scala.compiletime.uninitialized
import scala.quoted.{Expr, Quotes, Type, Varargs}
//
import org.mvv.scala.tools.{ Logger, tryDo, beforeLastOr, equalImpl }
import org.mvv.scala.tools.quotes.{ topClassOrModuleFullName, topMethodFullName, topMethodSimpleName }
import org.mvv.scala.tools.quotes.{ qClassNameOf, qClassName, qClassNameOfCompiled, Param, fullClassNameOf }
import org.mvv.scala.tools.quotes.{ qStringLiteral, qFunction, qTuple2 }
import org.mvv.scala.tools.quotes.{ classExists, getSimpleClassName, getFullClassName, getCompanionClass }
import org.mvv.scala.tools.quotes.{ findSpliceOwnerClass, getClassThisScopeTypeRepr, symbolDetailsToString }


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


/** 'isInitialized' methods will be searched in companion. */
inline def currentClassIsInitializedProps: IsInitializedProps =
  ${ currentClassIsInitializedPropsImpl( '{ Nil }, '{ "isInitialized" } ) }


//noinspection ScalaUnusedSymbol
inline def currentClassIsInitializedProps(inline isInitializedMethodOwners: String*): IsInitializedProps =
  ${ currentClassIsInitializedPropsImpl('{ isInitializedMethodOwners }, '{ "isInitialized" }) }


//noinspection ScalaUnusedSymbol
inline def currentClassIsInitializedProps(
  inline isInitializedMethodOwners: List[String],
  inline isInitializedMethod: String,
  ): IsInitializedProps =
  ${ currentClassIsInitializedPropsImpl('{ isInitializedMethodOwners }, '{ isInitializedMethod }) }


/** 'isInitialized' methods will be searched in companion object 'IsInitializedPropsOwnerClass'.
 *  !!! Object must also have class (to be companion, at least now) !!!
 */
//noinspection ScalaUnusedSymbol
inline def currentClassIsInitializedPropsBy[IsInitializedPropsOwnerClass]: IsInitializedProps =
  ${ currentClassIsInitializedPropsWithOwnerGenericTypeImpl[IsInitializedPropsOwnerClass] }


//noinspection ScalaUnusedSymbol
private def currentClassIsInitializedPropsWithOwnerGenericTypeImpl[T](using q: Quotes)(using Type[T]): Expr[IsInitializedProps] =
  currentClassIsInitializedPropsImpl_(List(fullClassNameOf[T]), "isInitialized")


//noinspection NoTailRecursionAnnotation , ScalaUnusedSymbol // there is no recursion
private def currentClassIsInitializedPropsImpl(using Quotes)(
  isInitializedMethodOwnersExpr: Expr[Seq[String]],
  isInitializedMethodExpr: Expr[String],
  ): Expr[IsInitializedProps] =

  val isInitializedMethodName: String = isInitializedMethodExpr.valueOrAbort //.getOrElse("isInitialized")
  val passedIsInitializedMethodOwnerClassNames: Seq[String] = isInitializedMethodOwnersExpr.valueOrAbort
  currentClassIsInitializedPropsImpl_(passedIsInitializedMethodOwnerClassNames, isInitializedMethodName)


private def currentClassIsInitializedPropsImpl_(using q: Quotes)(
  passedIsInitializedMethodOwnerClassNames: Seq[String],
  isInitializedMethodName: String,
  ): Expr[IsInitializedProps] =
  import q.reflect.{ Term, ClassDef, ValDef, Statement }

  val log = Logger(topMethodFullName)

  val classDef: ClassDef = findSpliceOwnerClass()
    .getOrElse( throw IllegalStateException("Macros 'currentClassIsInitializedProps' should be placed inside class."))
  val body: List[Statement] = classDef.body

  val valDefs: List[ValDef] = body
    .flatMap { case vd: ValDef => Option(vd); case _ => None }
    .filter(toCheckInitState)

  val allIsInitializedMethodOwnerClassNames: List[String] =
    (passedIsInitializedMethodOwnerClassNames.toList :+ getFullClassName(classDef)).distinct

  val ownerFullClassName = classDef.symbol.fullName
  val tuples: List[Term] = valDefs.map { vd =>
    qCreateIsInitTupleEntry (ownerFullClassName, vd) (isInitializedMethodName, allIsInitializedMethodOwnerClassNames*)
  }

  val tuplesExprs = tuples.map(_.asExprOf[(String,()=>Boolean)])
  val exprOfTupleList = Expr.ofList(tuplesExprs)

  log.debug(s"$topMethodSimpleName: ${exprOfTupleList.show}\n$exprOfTupleList")
  exprOfTupleList



private val SkipUninitializedCheckAnnotationNames: Set[String] = Set(
  "SkipLateInitCheck", "SkipUninitializedCheck", "SkipInitializedCheck", "SkipUninitCheck", "SkipInitCheck",
  "skipLateInitCheck", "skipUninitializedCheck", "skipInitializedCheck", "skipUninitCheck", "skipInitCheck",
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
  : MethodEntry[q.reflect.Symbol, q.reflect.DefDef] =

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

  val allIsInitializedMethods: List[MethodEntry[Symbol, DefDef]] = gatherIsInitializedFunctions(
    isInitializedFuncName, isInitializedOwnersWithCompanions)

  val theBestMethod: MethodEntry[Symbol, DefDef] = findTheBestOfOverloadedMethod(allIsInitializedMethods, valDef)
  log.trace(s"$logPrefix valDef (${valDef.name}: ${valDef.tpt.tpe.show})," +
    s" theBestMethod: ${theBestMethod.method.name}(${firstParamType(theBestMethod.method).show})")

  theBestMethod



private def gatherIsInitializedFunctions(using q: Quotes)
  (isInitializedFuncName: String, funcOwners: List[q.reflect.Symbol])
  : List[MethodEntry[q.reflect.Symbol, q.reflect.DefDef]] =
  import q.reflect.{ Symbol, DefDef }
  import org.mvv.scala.tools.quotes.symbolDetailsToString

  val log = Logger(topMethodFullName)
  val logPrefix = topMethodSimpleName

  val functions: List[MethodEntry[Symbol, DefDef]] = funcOwners.distinct
    // I do not know how to filter/distinguish ?incorrect? object/class method owner
    // since these incorrect symbols return isType/isClassDef/exists,
    // only flags=EmptyFlags but it is not reliable sign
    // for that reason I have to use there try/catch (tryDo)
    .filter(s => !s.isNoSymbol)
    .map { s => log.trace(s"$logPrefix funcOwner ${symbolDetailsToString(s)}"); s }
    .flatMap(s => tryDo{ s.methodMembers } .getOrElse(Nil).map( (s, _) ))
    .filter(_._2.isDefDef) .map((s, methodSymbol) => MethodEntry[Symbol, DefDef](s, methodSymbol.tree match { case m: DefDef => m }) )
    .filter(me => me.method.name == isInitializedFuncName)
    .filter(me => isSimpleBoolMethodWithOneParamOfAnyType(me.method))
    .toList

  functions.foreach { me =>
    log.trace(s"gatherIsInitializedFunctions resulting func: ${me.ownerClass.fullName}" +
      s" # ${symbolDetailsToString(me.method.symbol)}") }

  functions


private def methodsTosStrings(using q: Quotes)
  (methods: Iterable[MethodEntry[q.reflect.Symbol, q.reflect.DefDef]]): Iterable[String] =
  val methodsAsStr: Iterable[String] = methods.map(me => s"${me.ownerClass.fullName}.${me.method.name}(${firstParamType(me.method).show})")
  methodsAsStr

private def methodsTosString(using q: Quotes)
  (methods: Iterable[MethodEntry[q.reflect.Symbol, q.reflect.DefDef]]): String =
  methodsTosStrings(methods).mkString("\n  ", "\n  ", "\n  ")


private def findTheBestOfOverloadedMethod(using q: Quotes)(
  allIsInitializedMethods: List[MethodEntry[q.reflect.Symbol, q.reflect.DefDef]],
  valDef: q.reflect.ValDef,
  ): MethodEntry[q.reflect.Symbol, q.reflect.DefDef] =
  import q.reflect.{ TypeRepr, DefDef, Symbol, report }

  val classOwnerName = valDef.symbol.owner.fullName
  val valueType = valDef.tpt.tpe

  require(allIsInitializedMethods.nonEmpty, "allIsInitializedMethods are empty."
    + s" Please specify isInitializedMethodOwners in 'currentClassIsInitializedProps' macros"
    + s" or add isInitialized to $classOwnerName class or companion.")

  val valueTypeStr = valueType.show
  val isInitMethName = allIsInitializedMethods.head.method.name

  val methodsWithSuitableTypes: List[MethodEntry[Symbol,DefDef]] = allIsInitializedMethods
    .filter(me =>
      val mParamType: TypeRepr = firstParamType(me.method)
      val isSuitable = valueType <:< mParamType
      isSuitable
    )

  if methodsWithSuitableTypes.isEmpty then
    throw IllegalStateException(s"No suitable methods [$isInitMethName] for type [$valueTypeStr]"
      + s"\n allIsInitializedMethods: ${methodsTosString(allIsInitializedMethods)}"
    )

  val withSuperTypes = mutable.Set[MethodEntry[Symbol,DefDef]]()

  for m1 <- methodsWithSuitableTypes
      m2 <- methodsWithSuitableTypes
    do
      //noinspection ScalaUnusedSymbol
      given CanEqual[MethodEntry[Symbol,DefDef], MethodEntry[Symbol,DefDef]] = CanEqual.derived

      if m1 != m2 then
        val m1ParamType: TypeRepr = firstParamType(m1.method)
        val m2ParamType: TypeRepr = firstParamType(m2.method)

        if m1ParamType <:< m2ParamType then withSuperTypes.addOne(m2)
        if m2ParamType <:< m1ParamType then withSuperTypes.addOne(m1)

  val withoutSuperTypes = methodsWithSuitableTypes.toSet -- withSuperTypes

  if withoutSuperTypes.isEmpty then
    throw IllegalStateException(s"Impossible to find single method for [$valueTypeStr]"
      + s" from ${methodsTosString(methodsWithSuitableTypes)}"
      + s"\n methodsWithSuitableTypes: ${methodsTosString(methodsWithSuitableTypes)}"
      + s"\n allIsInitializedMethods: ${methodsTosString(allIsInitializedMethods)}"
    )

  if withoutSuperTypes.sizeIs != 1 then
    report.errorAndAbort(s"Ambiguous methods ${methodsTosString(withoutSuperTypes)} for [$valueTypeStr]")

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
  import q.reflect.{ Symbol, Tree, Term, Select, Apply, Lambda, DefDef, MethodType, This, TypeRepr, asTerm }
  import org.mvv.scala.tools.quotes.{ qMethodType, getClassThisScopeTypeRepr }

  val classSymbol = Symbol.classSymbol(classFullName)
  val valSelect = Select.unique(This(classSymbol), valDef.name)

  val _this = getClassThisScopeTypeRepr(classSymbol)
  val isInitializedDefDef: MethodEntry[Symbol,DefDef] = findIsInitializedFunction(_this)(valDef)
    (isInitializedFuncName, isInitializedOwners*)

  val isInitializedFSelect = Select(qClassName(isInitializedDefDef.ownerClass.fullName.stripSuffix("$")), isInitializedDefDef.method.symbol)
  val isInitializedApply = Apply(isInitializedFSelect, List(valSelect))

  val rhsFn: (Symbol, List[Tree]) => Tree = (_: Symbol, _: List[Tree]) => { isInitializedApply }
  val isInitializedAnonFunLambda = Lambda(
    Symbol.spliceOwner,
    MethodType(Nil)(_ => Nil, _ => TypeRepr.of[Boolean]),
    rhsFn
  )

  qTuple2[String, ()=>Boolean](qStringLiteral(valDef.name), isInitializedAnonFunLambda)



private class MethodEntry[O,M] (
  /** It is needed because we need to have type of class but methods may be declared in base class/trait. */
  val ownerClass: O, val method: M) extends Equals derives CanEqual :
  override def hashCode: Int =
    var hash = 7
    hash = 31 * hash + ownerClass.hashCode
    hash = 31 * hash + method.hashCode
    hash

  override def canEqual(other: Any): Boolean = other.isInstanceOf[MethodEntry[?,?]]
  // in scala3 equals with 'match' causes warning "pattern selector should be an instance of Matchable"
  override def equals(other: Any): Boolean =
    import org.mvv.scala.tools.AnyCanEqualGivens.given
    // 'equalImpl' is inlined and have resulting byte code similar to code with 'match'
    equalImpl[MethodEntry[?,?]](this, other) { (v1, v2) => v1.ownerClass == v2.ownerClass && v1.method == v2.method }



/*
It does not work, as expected to not work (((
private class MethodEntry(using val q: Quotes)(
  /** It is needed because we need to have type of class but methods may be declared in base class/trait. */
  val ownerClass: q.reflect.Symbol,
  val method: q.reflect.DefDef,
  ) extends Equals derives CanEqual :
  override def hashCode: Int =
    var hash = 7
    hash = 31 * hash + q.hashCode
    hash = 31 * hash + ownerClass.hashCode
    hash = 31 * hash + method.hashCode
    hash

  override def canEqual(other: Any): Boolean = other.isInstanceOf[MethodEntry]

  // in scala3 equals with 'match' causes warning "pattern selector should be an instance of Matchable"
  override def equals(other: Any): Boolean =
    // 'equalImpl' is inlined and have resulting byte code similar to code with 'match'
    equalImpl(this, other) { (v1, v2) =>
      v1.q == v2.q && v1.ownerClass == v2.ownerClass && v1.method == v2.method
    }
*/
