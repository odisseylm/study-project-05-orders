//noinspection DuplicatedCode
package com.mvv.scala.macros

import scala.Option
import scala.annotation.unused
import scala.compiletime.error
import scala.quoted.*
import scala.quoted.{Expr, Quotes, Type}
//
import com.mvv.scala.macros.Logger as log


// TODO: try to fix warning
inline def asPropValue[T](@unused inline expr: T): PropValue[T, Any] =
  ${ asPropValueImpl[T]('expr) }

inline def asPropValue[T](@unused inline expr: Option[T]): PropValue[T, Any] =
  ${ asPropOptionValueImpl[T]('expr) }


private def asPropValueImpl[T](expr: Expr[T])(using t: Type[T])(using Quotes): Expr[PropValue[T, Any]] = {
  log.debug(s"asPropValue => expr: [${expr.show}]")

  @unused val propNameExpr: Expr[String] = Expr(extractPropName(expr))
  val propValueExpr = '{ com.mvv.scala.macros.PropValue[T, Any]($propNameExpr, $expr) }

  log.debug(s"asPropValue => resulting expr: [${propValueExpr.show}]")
  propValueExpr
}

private def asPropOptionValueImpl[T](expr: Expr[Option[T]])(using t: Type[T])(using Quotes): Expr[PropValue[T, Any]] = {
  log.debug(s"asPropOptionValue => expr: [${expr.show}]")

  @unused val propNameExpr: Expr[String] = Expr(extractPropName(expr))
  val propValueExpr = '{ com.mvv.scala.macros.PropValue[T, Any]($propNameExpr, $expr) }

  //extractOwnerType(expr)

  log.debug(s"asPropOptionValue => resulting expr: [${propValueExpr.show}]")
  propValueExpr
}


private def extractPropName(expr: Expr[?])(using Quotes): String =
  val exprText: String = expr.show
  val separator = ".this."
  // TODO: try to use Quotes to get rop name (as last AST node)
  val sepIndex = exprText.indexOf(separator)

  if (sepIndex == -1)
    import quotes.reflect.report
    //logCompilationError(s"Seems expression [$exprText] is not property.", expr)
    report.errorAndAbort(s"Seems expression [${reportedFailedExprAsText(expr)}] is not property.", expr)

  val propName = exprText.substring(sepIndex + separator.length).nn
  propName


private def reportedFailedExprAsText(expr: Expr[Any])(using Quotes): String =
  import quotes.reflect.Position
  Position.ofMacroExpansion.sourceCode
    .map(sourceCode => s"${expr.show} used in $sourceCode")
    .getOrElse(expr.show)


//noinspection ScalaUnusedSymbol
private def logCompilationError(errorMessage: String, expr: Expr[Any])(using Quotes) =
  import quotes.reflect.Position
  val pos = Position.ofMacroExpansion
  //log.error(s"Seems expression [$exprText] is not property..")
  log.error(errorMessage)
  log.error(s"Seems expression [${expr.show}] is not property..")
  log.error(s"  At ${pos.sourceFile}:${pos.startLine}:${pos.startColumn}")
  log.error(s"     ${pos.sourceFile}:${pos.endLine}:${pos.endColumn}")
  pos.sourceCode.foreach(v => log.error(s"     $v"))







// TODO: move below functions to debug file
//noinspection ScalaUnusedSymbol
// Debug functions
// temp
private def printFields(label: String, obj: Any): Unit =
  println(s"\n\n$label   $obj")
  import scala.language.unsafeNulls
  allMethods(obj).foreach( printField(obj.getClass.getSimpleName, obj, _) )

private def printField(label: String, obj: Any, prop: String): Unit =
  try { println(s"$label.$prop: ${ getProp(obj, prop) }") } catch { case _: Exception => }

private def allMethods(obj: Any): List[String] =
  import scala.language.unsafeNulls
  obj.getClass.getMethods .map(_.getName) .toList

private def allMethodsDetail(obj: Any): String =
  import scala.language.unsafeNulls
  obj.getClass.getMethods /*.map(_.getName)*/ .toList .mkString("\n")

private def getProp(obj: Any, method: String): Any = {
  import scala.language.unsafeNulls
  val methodMethod = try { obj.getClass.getDeclaredMethod(method) } catch { case _: Exception => obj.getClass.getMethod(method) }
  val v = methodMethod.invoke(obj)
  //noinspection TypeCheckCanBeMatch
  if (v.isInstanceOf[Iterator[Any]]) {
    v.asInstanceOf[Iterator[Any]].toList
  } else v
}


/*

Term <: Statement <: Tree


Tree
  TreeMethods
    pos: Position
    symbol: Symbol
    show: String
    isExpr: Boolean
    asExpr: Expr[Any]
    asExprOf[T]: Expr[T]
    def changeOwner(newOwner: Symbol): ThisTree

    TypeTreeModule {
      def of[T <: AnyKind]: TypeTree
      def ref(typeSymbol: Symbol): TypeTree

    def unapply(tree: LambdaTypeTree): (List[TypeDef], Tree)

    TypeBoundsTreeModule
      def apply(low: TypeTree, hi: TypeTree): TypeBoundsTree
      def copy(original: Tree)(low: TypeTree, hi: TypeTree): TypeBoundsTree
      def unapply(x: TypeBoundsTree): (TypeTree, TypeTree)

Statement
  trait ClassDefMethods {
      def constructor(self: ClassDef): DefDef
      def parents(self: ClassDef): List[Tree]
      def self(self: ClassDef): Option[ValDef]
      def body(self: ClassDef): List[Statement]



Term

trait DefDefMethods
  extension (self: DefDef)
    def paramss: List[ParamClause]
    def leadingTypeParams: List[TypeDef]
    def trailingParamss: List[ParamClause]
    def termParamss: List[TermParamClause]
    def returnTpt: TypeTree
    def rhs: Option[Term]


trait TermMethods {
  extension (self: Term)
    def tpe: TypeRepr
    def underlyingArgument: Term
    def underlying: Term
    def etaExpand(owner: Symbol): Term
    def appliedTo(arg: Term): Term
    def appliedTo(arg: Term, args: Term*): Term
    def appliedToArgs(args: List[Term]): Apply
    def appliedToArgss(argss: List[List[Term]]): Term
    def appliedToNone: Apply
    def appliedToType(targ: TypeRepr): Term
    def appliedToTypes(targs: List[TypeRepr]): Term
    def appliedToTypeTrees(targs: List[TypeTree]): Term
    def select(sym: Symbol): Select



bool ast.Trees$Inlined.isType()
Tree ast.Trees$Inlined.call()
immutable.List ast.Trees$Inlined.bindings()
ast.Trees$Inlined ast.Trees$Inlined.unapply(ast.Trees$Inlined)
bool ast.Trees$Inlined.isTerm()
Tree ast.Trees$Inlined.expansion()

bool ast.Trees$Tree.isEmpty()
Attachment$Link ast.Trees$Tree.next()
immutable.List ast.Trees$Tree.toList()
Object ast.Trees$Tree.attachment(dotc.util.Property$Key)
Symbols$Symbol ast.Trees$Tree.symbol(Context)  // Contexts$Context
String ast.Trees$Tree.show(Context)
void ast.Trees$Tree.next_$eq (Attachment$Link)
Types$Type ast.Trees$Tree.tpe()
bool ast.Trees$Tree.hasType()
bool ast.Trees$Tree.isDef()
String ast.Trees$Tree.showIndented(int, Context)
String ast.Trees$Tree.showSummary(int, Contexts$Context)
dotc.printing.Texts$Text ast.Trees$Tree.toText(Printer)   printing.Printer
dotc.printing.Texts$Text ast.Trees$Tree.fallbackToText(Printer)   printing.Printer
Denotation ast.Trees$Tree.denot(Context)
Type ast.Trees$Tree.typeOpt()
Type ast.Trees$Tree.myTpe()
Attachments API ...

ast.Trees$Tree.myTpe_$eq(Types$Type)
ast.Trees$Tree.overwriteType(Types$Type)
Trees$Tree ast.Trees$Tree.withType(Types$Type, Contexts$Context)
Trees$Tree ast.Trees$Tree.withTypeUnchecked(Types$Type)
boolean ast.Trees$Tree.isPattern()
ast.Trees$Tree.foreachInThicket(Function1)
int ast.Positioned.line(Contexts$Context)
SrcPos ast.Positioned.srcPos()
SourceFile ast.Positioned.source()
SourcePosition ast.Positioned.endPos(Contexts$Context)
SourcePosition ast.Positioned.startPos(Contexts$Context)
long ast.Positioned.span()
SourcePosition Positioned.sourcePos(Contexts$Context)
Positioned ast.Positioned.withSpan(long)
SourcePosition ast.Positioned.focus(Contexts$Context)
Positioned ast.Positioned.cloneIn(SourceFile)
long Positioned.envelope$default$2()
long Positioned.envelope(SourceFile, long)
void Positioned.span_$eq(long)
int  Positioned.uniqueId()
void Positioned.checkPos(boolean, Contexts$Context)



%%%% exprTerm.symbol:
SymDenotations$SymDenotation Symbols$NoSymbol$.recomputeDenot(SymDenotations$SymDenotation, Contexts$Context)
AbstractFile Symbols$NoSymbol$.associatedFile(Contexts$Context)
Signature Symbols$Symbol.signature(Contexts$Context)
Names$Name Symbols$Symbol.name(Contexts$Context)
int Symbols$Symbol.line(Contexts$Context)
SrcPos Symbols$Symbol.srcPos()
bool Symbols$Symbol.isStatic(Contexts$Context)
Symbols$Symbol Symbols$Symbol.filter(Function1)
int Symbols$Symbol.id()
SourceFile Symbols$Symbol.source(Contexts$Context)
Symbols$Symbol Symbols$Symbol.copy(Contexts$Context, Symbols$Symbol, Names$Name, long, Types$Type, Symbols$Symbol, int, AbstractFile)
Symbols$Symbol Symbols$Symbol.asType(Contexts$Context)
boolean Symbols$Symbol.isType(Contexts$Context)
boolean Symbols$Symbol.isPrivate(Contexts$Context)
Nothing$ Symbols$Symbol.symbol(Object)
SourcePosition Symbols$Symbol.endPos(Contexts$Context)
void Symbols$Symbol.drop(Contexts$Context)
SourcePosition Symbols$Symbol.startPos(Contexts$Context)
Names$Name Symbols$Symbol.paramName(Contexts$Context)
String Symbols$Symbol.show(Contexts$Context)
String Symbols$Symbol.showExtendedLocation(Contexts$Context)
long Symbols$Symbol.span()
AbstractFile Symbols$Symbol.binaryFile(Contexts$Context)
boolean Symbols$Symbol.isClass()
Symbols$ClassSymbol Symbols$Symbol.asClass()
int Symbols$Symbol.nestingLevel()
String Symbols$Symbol.showIndented(int,Contexts$Context)
String Symbols$Symbol.showSummary(int,Contexts$Context)
SourcePosition Symbols$Symbol.sourcePos(Contexts$Context)
printing.Texts$Text Symbols$Symbol.toText(printing.Printer)
SourcePosition Symbols$Symbol.focus(Contexts$Context)
printing.Texts$Text Symbols$Symbol.fallbackToText(printing.Printer)
int Symbols$Symbol.paramVarianceSign(Contexts$Context)
int Symbols$Symbol.coord()
void Symbols$Symbol.coord_$eq(int)
ast.Trees$Tree Symbols$Symbol.defTree()
void Symbols$Symbol.defTree_$eq(ast.Trees$Tree,Contexts$Context)
boolean Symbols$Symbol.retainsDefTree(Contexts$Context)
SymDenotations$SymDenotation Symbols$Symbol.denot(Contexts$Context)
boolean Symbols$Symbol.isTerm(Contexts$Context)
void Symbols$Symbol.invalidateDenotCache()
void Symbols$Symbol.denot_$eq(SymDenotations$SymDenotation)
SymDenotations$SymDenotation Symbols$Symbol.originDenotation()
SymDenotations$SymDenotation Symbols$Symbol.lastKnownDenotation()
int Symbols$Symbol.defRunId()
boolean Symbols$Symbol.isDefinedInCurrentRun(Contexts$Context)
boolean Symbols$Symbol.isDefinedInSource(Contexts$Context)
boolean Symbols$Symbol.isValidInCurrentRun(Contexts$Context)
Symbols$Symbol Symbols$Symbol.asTerm(Contexts$Context)
boolean Symbols$Symbol.isPatternBound(Contexts$Context)
Symbols$Symbol Symbols$Symbol.entered(Contexts$Context)
Symbols$Symbol Symbols$Symbol.enteredAfter(DenotTransformers$DenotTransformer,Contexts$Context)
void Symbols$Symbol.dropAfter(DenotTransformers$DenotTransformer,Contexts$Context)
Symbols$Symbol Symbols$Symbol.sourceSymbol(Contexts$Context)
boolean Symbols$Symbol.isTypeParam(Contexts$Context)
Types$Type Symbols$Symbol.paramInfo(Contexts$Context)
Types$Type Symbols$Symbol.paramInfoAsSeenFrom(Types$Type,Contexts$Context)
Types$Type Symbols$Symbol.paramInfoOrCompleter(Contexts$Context)
long Symbols$Symbol.paramVariance(Contexts$Context)
Types$Type Symbols$Symbol.paramRef(Contexts$Context)
Types$TypeRef Symbols$Symbol.paramRef(Contexts$Context)
String Symbols$Symbol.prefixString()
String Symbols$Symbol.showLocated(Contexts$Context)
String Symbols$Symbol.showDcl(Contexts$Context)
String Symbols$Symbol.showKind(Contexts$Context)
String Symbols$Symbol.showName(Contexts$Context)
String Symbols$Symbol.showFullName(Contexts$Context)
*/


private def extractOwnerType_NonWorking[T](expr: Expr[T])(using t: Type[T])(using Quotes): Option[Class[T]] =
  val exprText: String = expr.show
  val separator = ".this."
  val sepIndex = exprText.indexOf(separator)

  if (sepIndex == -1)
    import quotes.reflect.report
    //logCompilationError(s"Seems expression [$exprText] is not property.", expr)
    report.errorAndAbort(s"Seems expression [${reportedFailedExprAsText(expr)}] is not property.", expr)

  val ownerClassName = exprText.substring(sepIndex).nn
  import quotes.reflect.*
  //import quotes.reflect.TreeMethods.given
  //import quotes.reflect.tree.*
  //import quotes.reflect.tree.given
  import quotes.reflect.Tree
  import quotes.reflect.Tree.*

  val exprTerm = expr.asTerm
  //log.debug(s"\n\n\n%%%% exprTerm: $exprTerm")
  //log.debug(s"\n\n\n%%%% exprTerm: ${allMethods(exprTerm)}")
  //log.debug(s"\n\n\n%%%% exprTerm: ${allMethodsDetail(exprTerm)}")
  printFields("exprTerm", exprTerm)

  //log.debug(s"\n\n\n%%%% exprTerm.symbol: ${exprTerm.symbol}")
  //log.debug(s"\n\n\n%%%% exprTerm.symbol: ${allMethods(exprTerm.symbol)}")
  //log.debug(s"\n\n\n%%%% exprTerm.symbol: ${allMethodsDetail(exprTerm.symbol)}")
  printFields("exprTerm.symbol", exprTerm.symbol)

  //exprTerm.toList() // no ??
  //exprTerm.isType // no
  //exprTerm.call
  //exprTerm.bindings
  //exprTerm.bindings

  exprTerm.pos

  import quotes.reflect.Inlined
  import quotes.reflect.Inlined.*
  import quotes.reflect.Select
  import quotes.reflect.Select.*

  /*
  exprTerm.pos

  val expansion = getProp(exprTerm, "expansion")
  //exprTerm.expansion
  println(s"\n\nexpansion: $expansion")
  printFields("\n\nexpansion", expansion)

  //exprTerm.select()

  //Select.qualifier
  val ss = Select(exprTerm, exprTerm.symbol)
  println(s"\n\nss: $ss")
  printFields("\n\nss", ss)

  val qualifier: Term = ss.qualifier
  printFields("qualifier", ss.qualifier)

  val tpe: TypeRepr = ss.tpe
  printFields("tpe", ss.tpe)
  */

  printFields("expr", expr)
  if (true) { return None }
  printFields("exprTerm", exprTerm)
  //printFields("exprTerm.underlyingArgument", exprTerm.qualifier)
  printFields("exprTerm.underlyingArgument", exprTerm.underlyingArgument)
  printFields("exprTerm.underlying", exprTerm.underlying)
  //printFields("exprTerm.signature", ss.signature)
  //printFields("exprTerm.appliedToNone", exprTerm.appliedToNone)
  //ss.typeOpt
  //ss.myTpe

  //printFields("exprTerm.underlying.underlying", exprTerm.underlying.underlying)
  //printFields("exprTerm.underlying.underlying.underlying", exprTerm.underlying.underlying.underlying)

  val under: Term = exprTerm.underlying


  //val aa: Term = This(under)
  //val aa: Term = This

  //val aa = This // bad
  //printFields("This", This)

  //val thisType = ThisType //(under)
  //printFields("ThisType", ThisType)

  val underAsSelect = under.asInstanceOf[Select]
  println(s"under.name: ${underAsSelect.name}")
  println(s"under.name: ${underAsSelect.qualifier}")

  printFields("underAsSelect.qualifier", underAsSelect.qualifier)

  val thisObj: This = underAsSelect.qualifier.asInstanceOf[This]
  thisObj.underlying
  //thisObj.qual
  //thisObj.typeOpt
  //thisObj.myTpe
  thisObj.tpe

  printFields("thisObj.underlying", thisObj.underlying)
  //printFields("thisObj.qualifier", thisObj.qualifier)

  printFields("thisObj.tpe", thisObj.tpe)
  //val thisTref: TypeRepr&ThisType  = thisObj.tpe.asInstanceOf[TypeRepr&ThisType].tref
  //val thisTref: TypeRepr&ThisType  = thisObj.tpe.tref
  //printFields("thisObj.tpe.tref", thisTref)

  println(s"thisObj.tpe.classSymbol: ${thisObj.tpe.classSymbol}")
  println(s"thisObj.tpe.typeSymbol: ${thisObj.tpe.typeSymbol}")
  println(s"thisObj.tpe.termSymbol: ${thisObj.tpe.termSymbol}")
  println(s"thisObj.tpe.baseClasses: ${thisObj.tpe.baseClasses}")
  println(s"thisObj.tpe.typeArgs: ${thisObj.tpe.typeArgs}")

  println(s"thisObj.tpe.asType: ${thisObj.tpe.asType}")
  //println(s"thisObj.tpe.asType: ${thisObj.tpe.classSymbol.asType}")

  //val bbbbbTypeRepr = TypeRepr.of[Bbbbbb]



  //val thisTref22: ThisType = thisObj.tpe.tref

  //thisTref.asInstanceOf[TypeRef].isType

  println("\n\n")
  None
end extractOwnerType_NonWorking
