package org.mvv.mapstruct.scala

import scala.annotation.nowarn
import scala.collection.mutable
import scala.quoted.*
import scala.tasty.inspector.{Inspector, Tasty, TastyInspector}

import java.nio.file.Path



private val _templateArgs = List("constr", "preParentsOrDerived", "self", "preBody")


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

  def toSymbol: Option[quotes.reflect.Symbol] =
    // TODO: try to remove risky asInstanceOf[Symbol]
    //if el.symbol.isInstanceOf[Symbol] then Option(el.symbol.asInstanceOf[Symbol]) else None
    Option(el.symbol.asInstanceOf[quotes.reflect.Symbol])

  def isTypeDef: Boolean =
    el.toSymbol.map(s => s.isTypeDef || s.isClassDef).getOrElse(false)

  def isPackageDef: Boolean =
    el.toSymbol .map(_.isPackageDef) .getOrElse(false)

  def isClassDef: Boolean =
    el.toSymbol .map(_.isClassDef) .getOrElse(false)

  def isValDef: Boolean =
    el.toSymbol .map(_.isValDef) .getOrElse(false)

  def isImport: Boolean =
    //println(s"isImport: $el")
    if !el.isValDef then return false
    el.toSymbol.map(_.name == "<import>").getOrElse(false)

  def isDefDef: Boolean =
    el.toSymbol .map(_.isDefDef) .getOrElse(false)

  //noinspection IsInstanceOf
  // Hacking approach because class Template is really used instead of ClassDef
  // but Template is not present in official APY scala3-library_X.jar!/scala/quoted/Quotes.tasty
  def isTemplate: Boolean =
    el.isInstanceOf[Product] && el.asInstanceOf[Product].productPrefix == "Template"

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
