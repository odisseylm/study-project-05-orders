package org.mvv.scala.tools.props

import scala.collection.mutable
import scala.annotation.targetName
import scala.compiletime.uninitialized
import scala.quoted.{ Expr, Quotes, Type, Varargs }
//
import org.mvv.scala.tools.{ Logger, tryDo }
import org.mvv.scala.tools.quotes.{ qClassNameOf, qClassName, qClassNameOfCompiled }
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

  log.trace(s"currentClassIsInitializedProps: ${exprOfTupleList.show}\n$exprOfTupleList")

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



private def termIsInitTupleEntry(using q: Quotes)(classFullName: String, valDef: q.reflect.ValDef): q.reflect.Term =
  import q.reflect.*

  val classSymbol = Symbol.classSymbol(classFullName)
  val valSelect = Select.unique(This(classSymbol), valDef.name)

  val scopeTypRepr: TypeRepr = getClassThisScopeTypeRepr(classSymbol)
  val isInitializedTerRef = TermRef(scopeTypRepr, "isInitialized")
  val isInitializedIdentTerm = Ident(isInitializedTerRef)

  val isInitializedApply = Apply(isInitializedIdentTerm, List(valSelect))

  val rhsFn: (Symbol, List[Tree]) => Tree = (_: Symbol, _: List[Tree]) => { isInitializedApply }
  val isInitializedAnonFunLambda = Lambda(
    Symbol.spliceOwner,
    MethodType(Nil)(_ => Nil, _ => TypeRepr.of[Boolean]),
    rhsFn
  )

  val tuple = qStringValueTuple2(valDef.name, isInitializedAnonFunLambda)
  tuple
