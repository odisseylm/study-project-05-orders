package org.mvv.scala.tools.props

import scala.quoted.{Expr, Quotes, Type}
import org.mvv.scala.tools.afterLastOrOrigin
import org.mvv.scala.tools.quotes.{ qClassNameOf, qMethodType, Param, symbolDetailsToString, asTreeTerm }
import org.mvv.scala.tools.quotes.{ findSpliceOwnerClass, getClassThisScopeTypeRepr, reportedFailedExprAsText }

import scala.annotation.tailrec


inline def valueExprName[T](inline valueWithName: T): String =
  ${ valueExprNameImpl[T]('{ valueWithName }) }


inline def namedValue[T](inline valueWithName: T): NamedValue[T] =
  NamedValue[T](valueExprName(valueWithName), valueWithName)


inline def readOnlyProp[T](inline valueWithName: T): ReadOnlyProp[T] =
  ReadOnlyProp[T](valueExprName(valueWithName), valueWithName)


inline def writableProp[T](inline valueWithName: T): WritableProp[T] =
  WritableProp[T](valueExprName(valueWithName), valueWithName, setterMethodOf[T](valueWithName))


inline def writableProp[T](inline valueWithName: T, inline setValueMode: SetPropValueMode): WritableProp[T] =
  WritableProp[T](valueExprName(valueWithName), valueWithName, setterMethodOf[T](valueWithName, setValueMode))


inline def setterMethodOf[T](inline valueWithName: T): T=>Unit =
  ${ setterMethodOfImpl[T]('{ valueWithName }, '{ SetPropValueMode.ByAssign }) }


inline def setterMethodOf[T](inline valueWithName: T, inline setValueMode: SetPropValueMode): T=>Unit =
  ${ setterMethodOfImpl[T]('{ valueWithName }, '{ setValueMode }) }


private def valueExprNameImpl[T]
  (using q: Quotes)(using Type[T])
  (valueWithName: Expr[T]): Expr[String] =

  import q.reflect.{ Inlined, Literal, StringConstant }
  val nameOfValueWithName = valueWithName.show
  val resInlined = Inlined(None, Nil, Literal(StringConstant(nameOfValueWithName)))
  resInlined.asExprOf[String]



//noinspection ScalaUnusedSymbol
private def setterMethodOfImpl[T]
  (using q: Quotes)(using Type[T], Type[T=>Unit])
  (valueWithNameExpr: Expr[T], setValueModeExpr: Expr[SetPropValueMode]): Expr[T=>Unit] =

  setValueModeExpr match
    case sm if sm.matches('{ SetPropValueMode.ByAssign }) => setValueByAssign[T](valueWithNameExpr)
    case sm if sm.matches('{ SetPropValueMode.ByFieldAccessorMethod }) => setValueByScalaFieldAccessor[T](valueWithNameExpr)
    case other =>
      val errMsg = s"Unexpected/unparseable setValueMode [${other.show}]."
      q.reflect.report.error(errMsg)
      throw IllegalArgumentException(errMsg)



//noinspection ScalaUnusedSymbol
private def setValueByAssign[T]
  (using q: Quotes)(using Type[T], Type[T=>Unit])
  (valueWithNameExpr: Expr[T]): Expr[T=>Unit] =

  import q.reflect.{ Symbol, Tree, TypeRepr, Flags, Lambda, Inlined, Assign, report, asTerm }

  val rhsFn: (Symbol, List[Tree]) => Tree = (s: Symbol, paramsAsTrees: List[Tree]) => {

    val varTerm = extractInlinedBody(valueWithNameExpr.asTerm)

    val isVarMutable = varTerm.symbol.flags.is(Flags.Mutable)
    if !isVarMutable then
      val errMsg = s"Variable [${varTerm.show}] is not mutable (in [${reportedFailedExprAsText(valueWithNameExpr)}])."
      report.error(errMsg)

    val assign = Assign(varTerm.asTreeTerm, paramsAsTrees.head.asTreeTerm)
    assign
  }

  inlinedSetterLambda[T](rhsFn)
end setValueByAssign



//noinspection ScalaUnusedSymbol
private def setValueByScalaFieldAccessor[T]
  (using q: Quotes)(using Type[T], Type[T=>Unit])
  (valueWithNameExpr: Expr[T]): Expr[T=>Unit] =

  import q.reflect.{ ClassDef, Symbol, Tree, Apply, This, Select, report }

  val classDefOp: Option[ClassDef] = findSpliceOwnerClass()

  val rhsFn: (Symbol, List[Tree]) => Tree = (s: Symbol, paramsAsTrees: List[Tree]) => {
    import org.mvv.scala.tools.quotes.asTreeTerm

    val varOrPropShortName = valueWithNameExpr.show.afterLastOrOrigin(".")
    val setterFullMethodName = s"${varOrPropShortName}_="

    val setterMethApplyOpt: Option[Apply] = classDefOp.flatMap { cd =>

      val this_ = This(cd.symbol)
      val methodMemberNames = this_.symbol.methodMembers.map(_.name).toSet
      val setterMethodExists = methodMemberNames.contains(setterFullMethodName)

      if setterMethodExists then
        val setterMethodSymbol: Symbol = Symbol.newMethod(this_.symbol, setterFullMethodName, setterMethodType[T])
        val setterMethodSymbolAsSelect = Select(this_, setterMethodSymbol)
        val apply: Apply = Apply(setterMethodSymbolAsSelect, List(paramsAsTrees.head.asTreeTerm))
        Option(apply)
      else
        None
    }

    setterMethApplyOpt.getOrElse {
      val errMsg = s"Class field accessor [${debugClassNameOf(classDefOp)}.$setterFullMethodName] is not found" +
        s" (in [${reportedFailedExprAsText(valueWithNameExpr)}])."
      report.error(errMsg); throw IllegalStateException(errMsg)
    }
  }

  inlinedSetterLambda[T](rhsFn)
end setValueByScalaFieldAccessor



// from Inlined(EmptyTree,List(),Inlined(EmptyTree,List(),Select(This(Ident(WritablePropTestClass)),prop1)))
@tailrec
private def extractInlinedBody(using q: Quotes)(el: q.reflect.Tree): q.reflect.Tree =
  import q.reflect.*
  el match
    case inl: Inlined => extractInlinedBody(inl.body)
    case _ => el


private def setterMethodType[T](using q: Quotes)(using Type[T]): q.reflect.MethodType =
  import q.reflect.TypeRepr
  qMethodType(Param("v", TypeRepr.of[T]))(TypeRepr.of[Unit])



// used as separated method only to avoid duplication warnings
//noinspection ScalaUnusedSymbol
private def inlinedSetterLambda[T](using q: Quotes)(using Type[T])
                                  (bodyRhsFn: (q.reflect.Symbol, List[q.reflect.Tree]) => q.reflect.Tree): Expr[T=>Unit] =
  import q.reflect.{ Symbol, Lambda, Inlined, TypeRepr }

  val anonFunLambda = Lambda(Symbol.spliceOwner, setterMethodType[T], bodyRhsFn)
  val inlined = Inlined(None, Nil, anonFunLambda)
  val asExpr = inlined.asExprOf[T => Unit]
  asExpr



private def debugClassNameOf(using q: Quotes)(classDefOp: Option[q.reflect.ClassDef]): String =
  classDefOp.map(_.symbol.name).getOrElse("UnknownClass")


//noinspection ScalaUnusedSymbol
/*
inline def namedValue[T](inline valueWithName: T): NamedValue[T] =
  ${ namedValueImpl[T]('valueWithName) }



def namedValueImpl[T]
  (using q: Quotes)(using Type[T], Type[NamedValue[T]])
  (valueWithName: Expr[T]): Expr[NamedValue[T]] =
  import q.reflect.*

  val namedValueClassTerm = qClassNameOf[NamedValue[T]] // to avoid hard-coding NamedValue package
  val namedValueApplyFunSelect = Select.unique(namedValueClassTerm, "apply")
  val typeApply = TypeApply(namedValueApplyFunSelect, List(TypeTree.of[T]))
  val nameOfValueWithName = valueWithName.show
  val namedValueApply = Apply(typeApply, List(Literal(StringConstant(nameOfValueWithName)), valueWithName.asTerm))
  val resInlined = Inlined(None, Nil, namedValueApply)
  resInlined.asExprOf[NamedValue[T]]
*/
