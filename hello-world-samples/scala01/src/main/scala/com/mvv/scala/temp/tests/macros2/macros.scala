package com.mvv.scala.temp.tests.macros2

import scala.annotation.unused
import scala.quoted.{Expr, Quotes}
import scala.quoted.*
import scala.reflect.ClassTag


inline def asBeanValue(@unused inline expr: Any): BeanPropertyValue[Any, Any] =
  ${ asBeanValueImpl('expr) }

def asBeanValueImpl(expr: Expr[Any])(using Quotes): Expr[BeanPropertyValue[Any, Any]] = '{
  //if !$expr then
  com.mvv.scala.temp.tests.macros2.BeanPropertyValue.beanPropertyValue[Any, Any](
    "", //asText(expr), //expr.show,
    //$expr
    $expr
  )
}






inline def asBeanValue2(@unused inline expr: Any): Any =
  ${ asBeanValue2Impl('expr) }


def asBeanValue2Impl(expr: Expr[Any])(using q: Quotes): Expr[Any] = {
  val aa: String = expr.show
  println(s"\n\n=============================\nasBeanValue2Impl: $aa")
  val aa22 = Expr(expr.show)
  //val value = expr.value
  //Expr(12390) <=== Works!!!
  //Expr(com.mvv.scala.temp.tests.macros2.BeanPropertyValue.beanPropertyValue[Any, Any]("prrrrrrr", "aasssssfjdfgdfjdlj"))
  //Expr(com.mvv.scala.temp.tests.macros2.BeanPropValue("prrrrrrr", "aasssssfjdfgdfjdlj"))
  //Expr.apply("com.mvv.scala.temp.tests.macros2.BeanPropValue(\"prrrrrrr\", \"aasssssfjdfgdfjdlj\")")
  //Expr.apply(List(1)) <=== Works!!!
  //Expr.apply((aa, expr.valueOrAbort))

  //Expr.apply(List(expr))
  //Expr.ofTuple( ("asdfghjkl", "fgfgf") )
  //Expr.ofList(List("asdfghjkl", "fgfgf"))

  //Expr.ofList(List(Expr(expr.show), expr, expr, Expr(42), Expr("42"))) // <=== Works!!!

  /*
  //expr.summon[com.mvv.scala.temp.tests.macros2.Bbbbbb]()
  //q.summon[com.mvv.scala.temp.tests.macros2.Bbbbbb]()
  val ddd1 = Expr.summon[com.mvv.scala.temp.tests.macros2.Bbbbbb]
  println(s"summon1: $ddd1")

  val ddd2 = Expr.summon[Bbbbbb]
  println(s"summon2: $ddd2")
  */

  val isBoolExpr = expr.isExprOf[Boolean]
  println(s"isBoolExpr: $isBoolExpr")

  val isStringExpr = expr.isExprOf[String]
  println(s"isStringExpr: $isStringExpr")

  val isIntExpr = expr.isExprOf[Int]
  println(s"isIntExpr: $isIntExpr")

  val isLongExpr = expr.isExprOf[Long]
  println(s"isLongExpr: $isLongExpr")

  //expr.matches()

  //var tt = expr match
  //  case '{ $expr: t } => t
  expr match
    case a @ '{ $expr: t } => println(s"expr match ${a.getClass.getName} ${allMethods(a)}")

  println(s"expr ${expr.getClass.getName} ${allMethods(expr)}")
  println(s"expr ${Expr.getClass.getName} ${allMethods(Expr)}")

  val value: Expr[Any] = Expr.betaReduce(expr)
  //val apply: Expr[Any] = Expr.apply(expr)
  //val quotesToOption: Option[Any] = Expr.unapply(expr)
  //val value1: Expr[Any] = Expr.block(List(expr), expr)

  val scope = getProp(expr, "scope")
  println(s"scope: ${scope.getClass.getName} ${allMethods(scope)}")
  allMethods(scope).foreach( f => printField("scope", scope, f) )

  val tree = getProp(expr, "tree")
  println(s"tree: ${tree.getClass.getName} ${allMethods(tree)}")
  allMethods(tree).foreach( f => printField("tree", tree, f) )

  //Symbol.matches()

  import quotes.reflect.*
  //TypeRepr.of[T].typeSymbol
  val ddInt = TypeRepr.of[Int].typeSymbol
  println(s"ddInt: $ddInt")

  val bbbbbTypeRepr = TypeRepr.of[Bbbbbb]
  printFields("\n\n---------------------------\nBbbbb type", bbbbbTypeRepr)
  printFields("\n\n---------------------------\nBbbbb type", bbbbbTypeRepr.typeSymbol)
  //printFields("\n\n---------------------------\nBbbbb type", bbbbbTypeRepr.tpe)
  //printFields("\n\n---------------------------\nBbbbb type", bbbbbTypeRepr.underlying)

  println(s"ddBbb: $bbbbbTypeRepr ${ allMethods(TypeRepr.of[Bbbbbb]) }")
  printFields("\n\n---------------------------\nBbbbb type", bbbbbTypeRepr)

  //val package1 = PackageClause(bbbbbTypeRepr, tree)
  //printFields("\n\n---------------------------\npackage1", bpackage1)


  //AppliedType.
  println(s"TypeRepr: ${TypeRepr.getClass.getName} ${ allMethods(TypeRepr) }")
  println(s"ThisType: ${ThisType.getClass.getName} ${ allMethods(ThisType) }")
  println(s"RecursiveThis: ${RecursiveThis.getClass.getName} ${ allMethods(RecursiveThis) }")
  println(s"RecursiveType: ${RecursiveType.getClass.getName} ${ allMethods(RecursiveType) }")
  //println(s"LambdaType: ${LambdaType.getClass.getName} ${ allMethods(LambdaType) }")
  println(s"TypeLambda: ${TypeLambda.getClass.getName} ${ allMethods(TypeLambda) }")
  println(s"MatchCase: ${MatchCase.getClass.getName} ${ allMethods(MatchCase) }")
  // +++ how to use it???
  println(s"Symbol: ${Symbol.getClass.getName} ${ allMethods(Symbol) }")

  // /home/vmelnykov/.m2/repository/org/scala-lang/scala3-library_3/3.3.0-RC1-bin-20230116-d99d9bf-NIGHTLY/scala3-library_3-3.3.0-RC1-bin-20230116-d99d9bf-NIGHTLY.jar!/scala/quoted/Quotes.tasty
  //
  // Tree
  //
  // PackageClause
  // Import
  // Export
  // Statement
  // Definition
  // ClassDef
  // DefDef
  // ValDef
  // TypeDef
  // Term
  // Ref
  // Ident
  // Wildcard
  // Select
  // Literal
  // This ?????????
  // New
  // NamedArg
  // Apply
  // TypeApply
  // Super
  // Typed
  // Assign
  // Block
  // Closure
  // If
  // Match
  // SummonFrom
  // Try
  // Return
  // Repeated
  // Inlined
  // SelectOuter
  // While
  // TypedOrTest
  // TypeTree
  // Inferred
  // TypeIdent
  // TypeSelect
  // TypeProjection
  // Singleton
  // Refined
  // Applied
  // Annotated
  // MatchTypeTree
  // ByName
  // LambdaTypeTree
  // TypeBind
  // TypeBlock
  // TypeBoundsTree
  // WildcardTypeTree
  // CaseDef
  // TypeCaseDef
  // Bind
  // Unapply
  // Alternatives
  // ParamClause
  // TermParamClause
  // TypeParamClause
  // Selector
  // SimpleSelector
  // +++ RenameSelector
  // OmitSelector
  // GivenSelector
  // +++ TypeRepr
  // ConstantType
  // NamedType
  // +++ TermRef
  // TypeRef
  // SuperType
  // Refinement
  // AppliedType
  // AnnotatedType
  // AndOrType
  //   AndType
  //   OrType
  // MatchType
  // ByNameType
  // ParamRef
  // ThisType
  // RecursiveThis
  // RecursiveType
  // LambdaType
  // MethodOrPoly
  //   MethodType
  //   PolyType
  // TypeLambda
  // MatchCase
  // TypeBounds
  // NoPrefix
  // Constant
  //   BooleanConstant, ByteConstant, ShortConstant, IntConstant, LongConstant, FloatConstant, DoubleConstant,
  //   CharConstant, StringConstant, UnitConstant, NullConstant ?, ClassOfConstant,
  //
  // ImplicitSearchResult, ImplicitSearchSuccess, ImplicitSearchFailure,
  // DivergingImplicit, NoMatchingImplicits, AmbiguousImplicits
  // +++ Symbol
  // Signature
  //
  // Flags
  // Position, SourceFile,
  //
  //
  //
  // ...

  //
  // Signature
  // Flags
  // Position
  // SourceFile
  // ? report / Report
  // Printer
  // Nested
  //

  // root, stack, pos, outer, contextWithNewSpliceScope, getCurrent, setSpliceScope, isOuterScopeOf, atSameLocation

  /*
  val root = invoke(scope, "root")
  println(s"scope.root: $root")

  val stack = invoke(scope, "stack")
  println(s"scope.stack: $stack")

  val pos = invoke(scope, "pos")
  println(s"scope.root: $pos")

  val outer = invoke(scope, "outer")
  println(s"scope.outer: $outer")

  val getCurrent = invoke(scope, "getCurrent")
  println(s"scope.getCurrent: $getCurrent")

  val atSameLocation = invoke(scope, "atSameLocation")
  println(s"scope.atSameLocation: $atSameLocation")
  */

  // +++
  // tree.tpe: TermRef(ThisType(TypeRef(ThisType(TypeRef(NoPrefix,module class macros2)),class Bbbbbb)),val aaa)
  // tree.typeOpt: TermRef(ThisType(TypeRef(ThisType(TypeRef(NoPrefix,module class macros2)),class Bbbbbb)),val aaa)
  // tree.myTpe: TermRef(ThisType(TypeRef(ThisType(TypeRef(NoPrefix,module class macros2)),class Bbbbbb)),val aaa)
  // tree._3: Select(This(Ident(Bbbbbb)),aaa)
  // tree.expansion: Select(This(Ident(Bbbbbb)),aaa)
  //

  val term: Term = expr.asTerm
  printFields("\n\n---------------------------\nTERM", term)

  //val import1: Import = Import(term)
  //printFields("\n\n---------------------------\nimport", import1)

  //val export1: Export = Export(term)
  //printFields("\n\n---------------------------\nexport", export1)

  //println(s"scope => root: $root, stack: $stack, pos: $pos, outer: $outer, getCurrent: $getCurrent, atSameLocation: $atSameLocation")

  //Expr.ofTuple( (Expr(expr.show), expr) ) // <=== Works!!!

  //'{ List("vcv", ${ expr }, ${ aa22 } ) } // <=== working !!!!!
  //'{ com.mvv.scala.temp.tests.macros2.BeanPropertyValue.beanPropertyValue[Any, Any]( ${ aa22 }, ${ expr } ) // <=== working !!!!!
  '{ com.mvv.scala.temp.tests.macros2.BeanPropertyValue.beanPropertyValue[Any, Any]( $aa22, $expr )
  } // <=== working !!!!!
}


inline def asBeanValue3[T](@unused inline expr: T): BeanPropertyValue[T, Any] =
  ${ asBeanValue3Impl[T]('expr) }


def asBeanValue3Impl[T](expr: Expr[T])(using t: Type[T])(using q: Quotes): Expr[BeanPropertyValue[T, Any]] = {
  val aa: String = expr.show
  println(s"\n\n=============================\nasBeanValue2Impl: $aa")
  val aa22: Expr[String] = Expr(expr.show)

  val eee = '{ com.mvv.scala.temp.tests.macros2.BeanPropertyValue.beanPropertyValue[T, Any]($aa22, $expr) }

  println(s"eee ${eee.show}")
  eee
}


private def printFields(label: String, obj: Any): Unit =
  println(label)
  import scala.language.unsafeNulls
  allMethods(obj).foreach( printField(obj.getClass.getSimpleName, obj, _) )

private def printField(label: String, obj: Any, prop: String): Unit =
  try { println(s"$label.$prop: ${ getProp(obj, prop) }") } catch { case ignore: Exception => }


private def allMethods(obj: Any): List[String] =
  import scala.language.unsafeNulls
  obj.getClass.getMethods .map(_.getName) .toList
  //obj.getClass.getMethods.map( _.getName ).toList
  //obj.getClass.getMethods.nn.map( _.nn.getName.nn ).nn.toList

def asText(x: Expr[Any])(using Quotes): String = x.show.toString

def getProp(obj: Any, method: String): Any = {
  import scala.language.unsafeNulls
  val methodMethod = try { obj.getClass.getDeclaredMethod(method) } catch { case ignore: Exception => obj.getClass.getMethod(method) }
  val v = methodMethod.invoke(obj)
  if (v.isInstanceOf[Iterator[Any]]) {
    v.asInstanceOf[Iterator[Any]].toList
  } else v
}

inline def inspect(inline x: Any): Any = ${ inspectCode('x) }

def inspectCode(x: Expr[Any])(using Quotes): Expr[Any] =
  println(x.show)
  x



/*

Obtaining a Symbol for a type
There is a handy shortcut to get the symbol for the definition of T. Instead of
TypeTree.of[T].tpe.typeSymbol
you can use
TypeRepr.of[T].typeSymbol



ClassSymbol.defTree: TypeDef(Bbbbbb,
Template(DefDef(<init>,List(List()),
TypeTree[TypeRef(ThisType(TypeRef(NoPrefix,module class scala)),class Unit)],EmptyTree),
List(Apply(Select(New(TypeTree[TypeRef(ThisType(TypeRef(NoPrefix,module class lang)),class Object)]),<init>),List())),
ValDef(_,EmptyTree,EmptyTree),
List(ValDef(aaa,Ident(String),L

ClassSymbol.defTree: TypeDef(Bbbbbb,Template(DefDef(<init>,List(List()),TypeTr...
List(ValDef(aaa,Ident(Rfvtgb),Apply(Select(Ident(Rfvtgb),apply),List(Literal(Constant(54646)))))
*/



private def getClassTag[T](using Type[T], Quotes): Expr[ClassTag[T]] = {
  import quotes.reflect.*

  Expr.summon[ClassTag[T]] match {
    case Some(ct) =>
      ct
    case None =>
      report.error(
        s"Unable to find a ClassTag for type ${Type.show[T]}",
        Position.ofMacroExpansion
      )
      throw new Exception("Error when applying macro")
  }
}
