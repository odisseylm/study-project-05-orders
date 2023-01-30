//noinspection DuplicatedCode , ScalaUnusedSymbol
package com.mvv.scala.macros

import com.sun.tools.javac.code.TypeTag

import runtime.stdLibPatches.Predef.nn
import scala.Option
import scala.annotation.unused
import scala.compiletime.error
import scala.quoted.*
import scala.quoted.{Expr, Quotes, Type}
import scala.reflect.ClassTag
//
import com.mvv.scala.macros.Logger as log


inline def asPropValue[T](inline expr: T): PropValue[T, Any] =
  ${ asPropValueImpl[T, Any]('{ null }, 'expr) }
inline def asPropValue[T, O](inline ownerExpr: O, inline expr: T): PropValue[T, O] =
  ${ asPropValueImpl[T, O]('ownerExpr, 'expr) }


inline def asPropValue[T](inline expr: Option[T]): PropValue[T, Any] =
  ${ asPropOptionValueImpl[T, Any]('{ null }, 'expr) }
inline def asPropValue[T, O](inline ownerExpr: O, inline expr: Option[T]): PropValue[T, O] =
  ${ asPropOptionValueImpl[T, O]('ownerExpr, 'expr) }


inline def asReadonlyProp[T, O](inline thisExpr: O, inline getExpr: T): ReadonlyProp[T, O] =
  ${ asRPropImpl[T, O]('thisExpr, 'getExpr) }
inline def asReadonlyProp[T, O](inline thisExpr: O, inline getExpr: Option[T]): ReadonlyProp[T, O] =
  ${ asRPropOptionImpl[T, O]('thisExpr, 'getExpr) }
inline def asWritableProp[T, O](inline thisExpr: O, inline getExpr: T): WritableProp[T, O] =
  ${ asWPropImpl[T, O]('thisExpr, 'getExpr) }
inline def asWritableProp[T, O](inline thisExpr: O, inline getExpr: Option[T]): WritableProp[T, O] =
  ${ asWPropOptionImpl[T, O]('thisExpr, 'getExpr) }


private def asPropValueImpl[T, O](ownerExpr: Expr[O], expr: Expr[T])
                                 (using Quotes, Type[T], Type[O]): Expr[PropValue[T, O]] =
  PropsMacro[T, O]().asPropValue(ownerExpr, expr)

private def asPropOptionValueImpl[T, O](ownerExpr: Expr[O], expr: Expr[Option[T]])
                                 (using Quotes, Type[T], Type[O]): Expr[PropValue[T, O]] =
  PropsMacro[T, O]().asPropOptionValue(ownerExpr, expr)


private def asRPropImpl[T, O](ownerExpr: Expr[O], expr: Expr[T])
                             (using Quotes, Type[T], Type[O]): Expr[ReadonlyProp[T, O]] =
  PropsMacro[T, O]().asProp(ownerExpr, expr, false).asInstanceOf[Expr[ReadonlyProp[T, O]]]
private def asWPropImpl[T, O](ownerExpr: Expr[O], expr: Expr[T])
                             (using Quotes, Type[T], Type[O]): Expr[WritableProp[T, O]] =
  PropsMacro[T, O]().asProp(ownerExpr, expr, true).asInstanceOf[Expr[WritableProp[T, O]]]


def asRPropOptionImpl[T, O](ownerExpr: Expr[O], expr: Expr[Option[T]])
                           (using Quotes, Type[T], Type[O]): Expr[ReadonlyProp[T, O]] =
  PropsMacro[T, O]().asPropOption(ownerExpr, expr, false).asInstanceOf[Expr[ReadonlyProp[T, O]]]

def asWPropOptionImpl[T, O](ownerExpr: Expr[O], expr: Expr[Option[T]])
                           (using Quotes, Type[T], Type[O]): Expr[WritableProp[T, O]] =
  PropsMacro[T, O]().asPropOption(ownerExpr, expr, true).asInstanceOf[Expr[WritableProp[T, O]]]



sealed class PropsMacro[T, O](using quotes: Quotes)(using t: Type[T])(using o: Type[O]) extends QuotesUtils(using quotes) {

  def asPropValue(ownerExpr: Expr[O], expr: Expr[T]): Expr[PropValue[T, O]] =
    log.debug(s"asPropValue => expr: [${ownerExpr.show}], [${expr.show}]")
    log.debug(s"${ getCompilationSource(expr) }")

    val propNameExpr: Expr[String] = Expr(extractPropName(expr))

    val propValueExpr: Expr[PropValue[T, O]] = findClassExpr[O]
      .map(typeExpr =>
        '{ com.mvv.scala.macros.PropValue[T, O]($propNameExpr, $expr, $typeExpr) })
      .getOrElse(
        '{ com.mvv.scala.macros.PropValue[T, O]($propNameExpr, $expr, ${ getClassTagExpr[O] }) })

    log.debug(s"asPropValue => resulting expr: [${propValueExpr.show}]")
    propValueExpr
  end asPropValue


  def asPropOptionValue(ownerExpr: Expr[O], expr: Expr[Option[T]]): Expr[PropValue[T, O]] =
    log.debug(s"asPropOptionValue => expr: [${expr.show}]")

    val propNameExpr: Expr[String] = Expr(extractPropName(expr))

    val propValueExpr: Expr[PropValue[T, O]] = findClassExpr[O]
      .map( typeExpr =>
        '{ com.mvv.scala.macros.PropValue[T, O]($propNameExpr, $expr, $typeExpr) }  )
      .getOrElse (
        '{ com.mvv.scala.macros.PropValue[T, O]($propNameExpr, $expr, ${ getClassTagExpr[O] }) }  )

    log.debug(s"asPropOptionValue => resulting expr: [${propValueExpr.show}]")
    propValueExpr
  end asPropOptionValue


  def asProp(thisExpr: Expr[O], getterExpr: Expr[T], isWritable: Boolean)
                      : Expr[WritableProp[T, O]] | Expr[ReadonlyProp[T, O]] = {
    log.debug(s"asProp => expr: [${thisExpr.show}], [${getterExpr.show}]")
    log.debug(s"${getCompilationSource(getterExpr)}")

    val propNameExpr = Expr(extractPropName(getterExpr))
    val isNullableProp = false // TODO: try to add isNullable as default parameter
    val isNullablePropExpr = Expr(isNullableProp)
    val getAsLambdaExpr = getterAsLambdaExpr(getterExpr) // '{ () => $getterFullName }
    val thisTypeAsClass = findClassExpr[O]

    val toPassThis = false // T O D O: make it as argument with default value 'false'
    val thisExprToPass = if toPassThis then
      // Passing this causes warning [Cannot prove the method argument is hot. Only hot values are safe to leak]
      // if PropertyObject is assigned to field (of this object)
      thisExpr
    else
      '{ null.asInstanceOf[O] }

    val propValueExpr: Expr[WritableProp[T, O]] | Expr[ReadonlyProp[T, O]] =
      if isWritable then // TODO: use universal signature and remove this big 'if'
        val setAsLambdaExpr = setterAsLambdaExpr(getterExpr, thisExpr)
        thisTypeAsClass
          .map(typeExpr =>
            '{ com.mvv.scala.macros.Property.property[T, O]($isNullablePropExpr, $propNameExpr, $getAsLambdaExpr, $setAsLambdaExpr, $thisExprToPass, $typeExpr) })
          .getOrElse(
            '{ com.mvv.scala.macros.Property.property[T, O]($isNullablePropExpr, $propNameExpr, $getAsLambdaExpr, $setAsLambdaExpr, $thisExprToPass, ${ getClassTagExpr[O] }) })
      else
        thisTypeAsClass
          .map(typeExpr =>
            '{ com.mvv.scala.macros.Property.property[T, O]($isNullablePropExpr, $propNameExpr, $getAsLambdaExpr, $thisExprToPass, $typeExpr) })
          .getOrElse(
            '{ com.mvv.scala.macros.Property.property[T, O]($isNullablePropExpr, $propNameExpr, $getAsLambdaExpr, $thisExprToPass, ${ getClassTagExpr[O] }) })

    log.debug(s"asProp => resulting expr: [${propValueExpr.show}]")
    propValueExpr
  }


  def asPropOption(thisExpr: Expr[O], getterExpr: Expr[Option[T]], isWritable: Boolean)
                                    : Expr[WritableProp[T, O]] | Expr[ReadonlyProp[T, O]] = {

    log.debug(s"asPropOption => expr: [${thisExpr.show}], [${getterExpr.show}]")
    log.debug(s"${getCompilationSource(getterExpr)}")

    val propNameExpr = Expr(extractPropName(getterExpr))
    val isNullableProp = false // TODO: try to add isNullable as default parameter
    val isNullablePropExpr = Expr(isNullableProp)
    val getAsLambdaExpr = getterOptionAsLambdaExpr(getterExpr) // '{ () => $getterFullName }
    val thisTypeAsClass = findClassExpr[O]

    val toPassThis = false // T O D O: make it as argument with default value 'false'
    val thisExprToPass = if toPassThis then
      // Passing this causes warning [Cannot prove the method argument is hot. Only hot values are safe to leak]
      // if PropertyObject is assigned to field (of this object)
      thisExpr
    else
      '{ null.asInstanceOf[O] }

    val propValueExpr: Expr[WritableProp[T, O]] | Expr[ReadonlyProp[T, O]] =
      if isWritable then // TODO: use universal signature and remove this big 'if'
        val setAsLambdaExpr = setterOptionAsLambdaExpr(getterExpr)
        thisTypeAsClass
          .map(typeExpr =>
            '{ com.mvv.scala.macros.Property.property[T, O]($isNullablePropExpr, $propNameExpr, $getAsLambdaExpr, $setAsLambdaExpr, $thisExprToPass, $typeExpr) })
          .getOrElse(
            '{ com.mvv.scala.macros.Property.property[T, O]($isNullablePropExpr, $propNameExpr, $getAsLambdaExpr, $setAsLambdaExpr, $thisExprToPass, ${ getClassTagExpr[O] }) })
      else
        thisTypeAsClass
          .map(typeExpr =>
            '{ com.mvv.scala.macros.Property.property[T, O]($isNullablePropExpr, $propNameExpr, $getAsLambdaExpr, $thisExprToPass, $typeExpr) })
          .getOrElse(
            '{ com.mvv.scala.macros.Property.property[T, O]($isNullablePropExpr, $propNameExpr, $getAsLambdaExpr, $thisExprToPass, ${ getClassTagExpr[O] }) })

    log.debug(s"asPropOption => resulting expr: [${propValueExpr.show}]")
    propValueExpr
  }


  private def getterAsLambdaExpr(getterExpr: Expr[T]): Expr[() => T | Null] =
    val getterFullMethodName = getterExpr.show
    //??? //'{ () => getterFullMethodName }
    '{ () => $getterExpr }


  private def getterOptionAsLambdaExpr(getterExpr: Expr[Option[T]]): Expr[() => Option[T]] =
    val getterFullMethodName = getterExpr.show
    '{ () => $getterExpr }


  private def setterAsLambdaExpr(getterExpr: Expr[T], thisExpr: Expr[O]): Expr[(T | Null) => Unit] =
    import quotes.reflect.*

    val getterFullMethodName = extractPropName(getterExpr)
    val setterFullMethodName = s"${getterFullMethodName}_="
    println("testFunc 03")

    // TODO: use proper type!!!! Not String as this!!!
    val this_ = This(TypeRepr.of[O].classSymbol.get)

    val setterMethodSymbol = Symbol.newMethod(
      this_.symbol, // TODO: use util function
      setterFullMethodName,
      MethodType(
        List("v88"))( // parameter list - here a single parameter
        _ => List(Symbol.requiredClass(TypeRepr.of[T].show).typeRef),
        _ => TypeRepr.of[Unit]
      ))

    println(s"testFunc 04  T: ${TypeRepr.of[T].show}")
    printFields("setterMethodSymbol", setterMethodSymbol)

    val rhsFn: (Symbol, List[Tree]) => Tree = (s: Symbol, paramsAsTrees: List[Tree]) => {
      println("testFunc 06  rhsFn s: $s: $paramsAsTrees")

      val setterMethodSymbolAsSelect = Select(thisExpr.asTerm, setterMethodSymbol)

      val applyParams: List[Term] = paramsAsTrees
        .map((vvv: Tree) => {
          printFields("testFunc 07   vvv", vvv)

          val asInstanceOfMethod = Symbol.newMethod(Symbol.noSymbol, "asInstanceOf", TypeRepr.of[T])
          val fun = Select(vvv.asInstanceOf[Term], asInstanceOfMethod)
          println(s"fun666777: $fun")

          val typeApply = TypeApply(fun, List(TypeTree.of[T]))
          typeApply
        })

      val apply: Apply = Apply(setterMethodSymbolAsSelect, applyParams)
      println(s"apply: ${apply.show}.")
      apply
    }

    println(s"testFunc 08  Before Lambda anonfunLamnda")

    val anonFunLambda = Lambda(
      Symbol.spliceOwner,
      MethodType(
        List("v44"))( // parameter names
        _ => List(TypeRepr.of[T | Null]),
        _ => TypeRepr.of[Unit]
      ),
      rhsFn
    )

    println(s"testFunc 09  anonfunLamnda: $anonFunLambda")
    val inlined = Inlined(None, Nil, anonFunLambda)

    println(s"testFunc resulting: ${inlined.asExpr.show}")
    inlined.asExprOf[T | Null => Unit]
  end setterAsLambdaExpr


  private def setterOptionAsLambdaExpr(getterExpr: Expr[Option[T]]): Expr[Option[T] => Unit] =
    val getterFullMethodName = getterExpr.show
    val setterFullMethodName = s"${getterFullMethodName}_="
    //'{ v => setterFullMethodName(v)  }
    // TODO: implement !!!
    '{ v => {} }

  //def internalCastToNonNullable[T](v: T|Null): T = v.asInstanceOf[T]

}


extension (s: String)
  private def startsWitOneOf(prefixes: String*): Boolean = prefixes.exists(s.startsWith)


//noinspection ScalaUnusedSymbol
def reportedFailedExprAsText(expr: Expr[Any])(using Quotes): String =
  import quotes.reflect.Position
  Position.ofMacroExpansion.sourceCode
    .map(sourceCode => s"${expr.show} used in $sourceCode")
    .getOrElse(expr.show)


//noinspection ScalaUnusedSymbol
def logCompilationError(errorMessage: String, expr: Expr[Any])(using Quotes) =
  import quotes.reflect.Position
  val pos = Position.ofMacroExpansion
  //log.error(s"Seems expression [$exprText] is not property..")
  log.error(errorMessage)
  log.error(s"Seems expression [${expr.show}] is not property..")
  log.error(s"  At ${pos.sourceFile}:${pos.startLine}:${pos.startColumn}")
  log.error(s"     ${pos.sourceFile}:${pos.endLine}:${pos.endColumn}")
  pos.sourceCode.foreach(v => log.error(s"     $v"))


private def findClassExpr[T](using Type[T], Quotes): Option[Expr[Class[T]]] =
  import quotes.reflect.*
  val asString: String = Type.show[T]
  if asString.startsWitOneOf("java.", "javax.", "scala.")
  then try {
    Option(Expr(Class.forName(asString).asInstanceOf[Class[T]]))
  } catch {
    case _: Exception => None
  }
  else None


private def getClassTagExpr[T](using Type[T], Quotes): Expr[ClassTag[T]] =
  import quotes.reflect.*
  Expr.summon[ClassTag[T]] match
    case Some(ct) => ct
    case None =>
      report.error(s"Unable to find a ClassTag for type ${Type.show[T]}", Position.ofMacroExpansion)
      throw new Exception("Error when applying macro")

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


//noinspection ScalaUnusedSymbol
private def getCompilationSource(expr: Expr[Any])(using Quotes): String =
  import quotes.reflect.Position
  val pos = Position.ofMacroExpansion
  s"""  At ${pos.sourceFile}:${pos.startLine}:${pos.startColumn}
     |    ${pos.sourceFile}:${pos.endLine}:${pos.endColumn}"""
  //pos.sourceCode.foreach(v => log.error(s"     $v"))


abstract class QuotesUtils()(using Quotes) :
  import quotes.reflect.*

  // example
  private def getDbType(typeRepr: TypeRepr): Long = typeRepr.asType match
    case '[Int]    => 1
    case '[String] => 2
    case '[unknown] =>
      report.errorAndAbort(s"Unsupported type as DB column ${Type.show[unknown]}")

  private def treeToTerm(tree: Tree)(using Quotes)(using Term): Term = tree match
    case t: Term => t // T O D O: improve better/legal code
    case _ => throw IllegalArgumentException("Tree does not present term and cannot be converted to Term.")

  //if tree.isInstanceOf[Term] then tree.asInstanceOf[Term]
  //else throw IllegalArgumentException("Tree does not present term and cannot be converted to Term")

end QuotesUtils


inline def dumpTerm[T](inline expr: T): T =
  ${ dumpTermImpl('expr) }
//${ 'expr }

private def dumpTermImpl[T](expr: Expr[T])(using Quotes)(using Type[T]): Expr[T] =
  import quotes.reflect.*
  val asTerm = expr.asTerm
  log.debug(s"dumpTerm => expr [$expr], [${expr.show}], as term [$asTerm].")
  expr



// TODO: move below functions to debug file
//noinspection ScalaUnusedSymbol
// Debug functions
// temp
def printFields(label: String, obj: Any): Unit =
  println(s"\n\n$label   $obj")
  import scala.language.unsafeNulls
  //noinspection TypeCheckCanBeMatch // scala3 warns about non matchable type
  if obj .isInstanceOf [List[?]] then
    obj.asInstanceOf[List[Any]].zipWithIndex
      .foreach { case (el, i) => printFields(s"$label $i:", _) }
  else
    allMethods(obj).foreach( printField(obj.getClass.getSimpleName, obj, _) )

def printField(label: String, obj: Any, prop: String): Unit =
  try { println(s"$label.$prop: ${ getProp(obj, prop) }") } catch { case _: Exception => }

//noinspection ScalaUnusedSymbol
def allMethods(obj: Any): List[String] =
  import scala.language.unsafeNulls
  obj.getClass.getMethods .map(_.getName) .toList

//noinspection ScalaUnusedSymbol
def allMethodsDetail(obj: Any): String =
  import scala.language.unsafeNulls
  obj.getClass.getMethods /*.map(_.getName)*/ .toList .mkString("\n")

def getProp(obj: Any, method: String): Any = {
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


//noinspection ScalaUnusedSymbol
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
  import quotes.reflect.ClassDefMethods

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
  import quotes.reflect.ClassDef

  /*
  printFields("ClassDef", ClassDef)
  printFields("ClassDef", ClassDef)
  printFields("ClassDef.parents", ClassDefMethods.parents)
  printFields("ClassDef.body", ClassDefMethods.body)
  printFields("ClassDef.constructor", ClassDefMethods.constructor)
  printFields("ClassDef.self", ClassDefMethods.self)

  printFields("DefDef", DefDef)

  /*
  printFields("DefDefMethods", DefDefMethods)
  printFields("DefDefMethods.paramss", DefDefMethods.paramss)
  printFields("DefDefMethods.leadingTypeParams", DefDefMethods.leadingTypeParams)
  printFields("DefDefMethods.trailingParamss", DefDefMethods.trailingParamss)
  printFields("DefDefMethods.termParamss", DefDefMethods.termParamss)
  printFields("DefDefMethods.returnTpt", DefDefMethods.returnTpt)
  printFields("DefDefMethods.rhs", DefDefMethods.rhs)
  */

  printFields("ValDefMethods.tpt", ValDefMethods.tpt)
  printFields("ValDefMethods.rhs", ValDefMethods.rhs)

  printFields("Ref", Ref)
  printFields("Ref.term", Ref.term)

  printFields("Ident", Ident)
  printFields("Select", Select)

  printFields("New", New)
  //printFields("ApplyMethods", ApplyMethods)
  //printFields("ApplyMethods.fun", ApplyMethods.fun)
  printFields("Super", Super)
  printFields("Typed", Typed)
  printFields("Closure", Closure)
  printFields("SummonFrom", SummonFrom)
  printFields("Return", Return)
  printFields("Inlined", Inlined)
  //printFields("Inlined", Inlined.call)
  //printFields("Inlined", Inlined.bindings)
  //printFields("Inlined", Inlined.body)
  printFields("SelectOuter", SelectOuter)
  printFields("TypeTree", TypeTree)
  printFields("Refined", Refined)
  printFields("Applied", Applied)
  printFields("LambdaTypeTree", LambdaTypeTree)
  printFields("TypeBind", TypeBind)
  printFields("Bind", Bind)
  printFields("Selector", Selector)
  printFields("Selector", Selector)
  printFields("RenameSelector", RenameSelector)
  printFields("OmitSelector", OmitSelector)
  printFields("GivenSelector", GivenSelector)
  printFields("TypeRef", TypeRef)
  printFields("SuperType", SuperType)
  printFields("Refinement", Refinement)
  printFields("AppliedType", AppliedType)
  printFields("ThisType", ThisType)
  printFields("RecursiveThis", RecursiveThis)
  printFields("Symbol", Symbol)
  printFields("defn", defn)
  printFields("Flags", Flags)
  printFields("Position", Position)
  printFields("Position", Position)
  printFields("SourceFile", SourceFile)
  printFields("Printer", Printer)
  //printFields("Nested", Nested)
  */

  if true then return None

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

  // TODO: to test
  //x.asTerm.show(using Printer.TreeStructure)

  None
end extractOwnerType_NonWorking


/*
 TODO: what is it???
 Spatial Data and Grids

abstract class NeighborVisitor[A <% Int => Double](val dim:Int) {
  def visitAllNeighbors(tDist:Double, visit:(A,A) => Unit)
  def visitNeighbors(i:Int, tDist:Double, visit:(A,A) => Unit)
  var distCalcs = 0
  def dist[A <% Int => Double](p1:A, p2:A):Double = {
    distCalcs += 1
    math.sqrt((0 until dim).foldLeft(0.0)((d,i) => {
      val di = p1(i)-p2(i)
      d+di*di
    }))
  }
}
*/
