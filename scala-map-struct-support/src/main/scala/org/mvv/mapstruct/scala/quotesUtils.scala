package org.mvv.mapstruct.scala

import scala.annotation.nowarn
import scala.collection.mutable
import scala.quoted.*
import scala.tasty.inspector.{Inspector, Tasty, TastyInspector}

import java.nio.file.Path



private val _templateArgs = List("constr", "preParentsOrDerived", "self", "preBody")
private val log: Logger = Logger("org.mvv.mapstruct.scala.quoteUtils")

def fullName(parentName: String, name: String) =
  if parentName == "" || parentName == "<empty>" then name else s"$parentName.$name"

def extractName(using quotes: Quotes)(el: quotes.reflect.Tree): String =
  el.toSymbol.get.name
  //import quotes.reflect.*
  //el match
  //  case id: Ident => id.name // T O D O: is it safe to use normal pattern matching
  //  case _ => throw IllegalArgumentException(s"Unexpected $el tree element in identifier. ")


def extractClassName(using Quotes)(t: quotes.reflect.Tree): String =
  val symbol = t.toSymbol.get
  val clsStr = if symbol.isType
    then symbol.typeRef.dealias.widen.dealias.show // we also can use symbol.fullName
    else symbol.fullName.stripSuffix(".<init>")
  clsStr


extension (using quotes: Quotes)(el: quotes.reflect.Tree)

  private def isImplClass(className: String): Boolean =
    el.isInstanceOf[Product] && el.asInstanceOf[Product].productPrefix == className

  private def isOneOfImplClasses(className: String, otherClassNames: String*): Boolean =
    el.isImplClass(className) || otherClassNames.exists(clName => el.isImplClass(clName))

  def toSymbol: Option[quotes.reflect.Symbol] =
    // TODO: try to remove risky asInstanceOf[Symbol]
    //if el.symbol.isInstanceOf[Symbol] then Option(el.symbol.asInstanceOf[Symbol]) else None
    Option(el.symbol.asInstanceOf[quotes.reflect.Symbol])

  def isTerm: Boolean =
    // It is unclear how to implement this properly...
    val isTermSymbol = el.toSymbol .map(_.isTerm) .getOrElse(false)
    val isTerm = if isTermSymbol then isTermSymbol
      else try unwrapOption(getByReflection(el, "isTerm")).asInstanceOf[Boolean]
           catch case _: Exception => false
    isTerm

  def isNoSymbol: Boolean =
    el.toSymbol .map(_.isNoSymbol) .getOrElse(false)

  def isTypeDef: Boolean =
    el.toSymbol .map(s => s.isTypeDef || s.isClassDef) .getOrElse(false)

  def isSingletonDef: Boolean =
    (el.isValDef || el.isClassDef)
      && el.toSymbol.map(_.name.endsWithOneOf("$", "$>")).getOrElse(false)

  def isPackageDef: Boolean =
    el.toSymbol .map(_.isPackageDef) .getOrElse(false)

  def isClassDef: Boolean =
    el.toSymbol .map(_.isClassDef) .getOrElse(false)

  def isValDef: Boolean =
    el.toSymbol .map(_.isValDef) .getOrElse(false)

  def isImport: Boolean =
    if !el.isValDef then return false
    el.toSymbol .map(_.name == "<import>") .getOrElse(false) || el.isImplClass("Import")

  def isApply: Boolean = el.isDefDef && el.isImplClass("Apply")

  def isIf: Boolean = el.isTerm && el.isImplClass("If")

  def isMatch: Boolean = el.isTerm && el.isImplClass("Match")

  def isWhile: Boolean = el.isTerm &&
    el.isOneOfImplClasses("While", "WhileDo", "DoWhile")

  def isBlock: Boolean = el.isTerm && el.isImplClass("Block")

  def isTry: Boolean = el.isTerm && el.isImplClass("Try")

  def isLiteral: Boolean = el.isTerm && el.isImplClass("Literal")

  def isConstant: Boolean = el.isTerm && el.isImplClass("Constant")

  def isExprStatement: Boolean =
    el.isApply || el.isIf || el.isWhile || el.isMatch || el.isBlock || el.isTry
      || el.isLiteral || el.isConstant

  def isDefDef: Boolean =
    el.toSymbol .map(_.isDefDef) .getOrElse(false)

  //noinspection IsInstanceOf
  // Hacking approach because class Template is really used instead of ClassDef
  // but Template is not present in official APY scala3-library_X.jar!/scala/quoted/Quotes.tasty
  def isTemplate: Boolean =
    el.isInstanceOf[Product] && el.asInstanceOf[Product].productPrefix == "Template"

  def isDefinition: Boolean =
    // NOot tested yet
    el.isInstanceOf[Product] && el.asInstanceOf[Product].productPrefix == "Definition"

  /*
  private def treeElementName: Option[String] =
    import quotes.reflect.*
    try
      el match
        case e if e.isNamedType => Option(e.asInstanceOf[NamedType].name)
        case e if e.isTypeRef => Option(e.asInstanceOf[TermRef].name)
        case e if e.isTermRef => Option(e.asInstanceOf[TermRef].name)
        case e if e.isRefinement => Option(e.asInstanceOf[Refinement].name)
        case e if e.isRefinement => Option(e.asInstanceOf[Refinement].name)
        case e if e.isRefinement => Option(e.asInstanceOf[Refinement].name)
        case e if e.isRefinement => Option(e.asInstanceOf[Refinement].name)
        case e if e.isDefinition => Option(e.asInstanceOf[Definition].name)
        case e if e.isIdent => Option(e.asInstanceOf[Ident].name)
        case e if e.isSelect => Option(e.asInstanceOf[Select].name)
        case e if e.isNamedArg => Option(e.asInstanceOf[NamedArg].name)
        case e if e.isSelectOuter => Option(e.asInstanceOf[SelectOuter].name)
        case e if e.isTypeIdent => Option(e.asInstanceOf[TypeIdent].name)
        case e if e.isTypeSelect => Option(e.asInstanceOf[TypeSelect].name)
        case e if e.isTypeProjection => Option(e.asInstanceOf[TypeProjection].name)
        case e if e.isTypeBind => Option(e.asInstanceOf[TypeBind].name)
        case e if e.isBind => Option(e.asInstanceOf[Bind].name)
        case e if e.isSimpleSelector => Option(e.asInstanceOf[SimpleSelector].name)
        case e if e.isOmitSelector => Option(e.asInstanceOf[OmitSelector].name)
        case e if e.isTypeProjection => Option(e.asInstanceOf[TypeProjection].name)
        case _ => None
    catch case ex: Exception =>
      log.info(s"Unexpected error of getting name: [$ex]", ex)
      None

  def elementName: Boolean =
    el.treeElementName .orElse(el.toSymbol.map(s => s.name))
      .getOrElse(throw IllegalStateException(s"Error of getting name of [$el]."))
  */

  //def isEnumValueDef: Boolean =
  //  val symbolOpt = el.toSymbol
  //  if symbolOpt.isEmpty then return false
  //
  //  import quotes.reflect.*
  //  val symbol: Symbol = symbolOpt.get
  //  val name = symbol.fullName
  //  val fullName = symbol.fullName
  //  val typeRef: TypeRef = symbol.typeRef
  //
  //  false

  def getClassMembers: List[quotes.reflect.Tree] =
    el match
      case cd if cd.isClassDef => cd.asInstanceOf[quotes.reflect.ClassDef].body
      case t if t.isTemplate  => getByReflection(t, "body", "preBody", "unforcedBody")
        .unwrapOption.asInstanceOf[List[quotes.reflect.Tree]]
      case _ => throw IllegalArgumentException(s"Unexpected tree $el.")

  def getClassDefParents: List[quotes.reflect.Tree] =
    el match
      case cd if cd.isClassDef => cd.asInstanceOf[quotes.reflect.ClassDef].parents
      case t if t.isTemplate  => getByReflection(t, "parents", "preParentsOrDerived", "unforcedParents")
        .unwrapOption.asInstanceOf[List[quotes.reflect.Tree]]
      case _ => throw IllegalArgumentException(s"Unexpected tree $el.")

  //// ???? Dow we need it?
  //def getConstructor: List[Tree] =
  //  el match
  //    case cd if cd.isClassDef => cd.asInstanceOf[ClassDef].constructor
  //    //case t if t.isTemplate  => getByReflection(t, "constructor") // template does not have constructor
  //    case _ => throw IllegalArgumentException(s"Unexpected tree $tree.")

end extension


def getByReflection(obj: AnyRef, propName: String*): Any =
  val klass = obj.getClass
  propName
    .map( propName =>
      try Option(klass.getMethod(propName).nn.invoke(obj))
      catch case _: Exception =>
          try
            val field: java.lang.reflect.Field = klass.getDeclaredField(propName).nn
            field.setAccessible(true)
            Option(field.get(obj)) // we use/return of 1st successful case
          catch case _: Exception => None
    )
    .find( _.isDefined )
    .getOrElse(throw IllegalArgumentException(s"Property [${klass.getName}.${propName.mkString(", ")}] is not found."))
end getByReflection


// Use it very-very careful!!! It is designed only for hacking code
// for example when reflection is used
extension [T](v: T|Null|Option[T])
  @nowarn @unchecked //noinspection IsInstanceOf
  def unwrapOption: T|Null =
    if v.isInstanceOf[Option[T]] then v.asInstanceOf[Option[T]].orNull else v.asInstanceOf[T|Null]
