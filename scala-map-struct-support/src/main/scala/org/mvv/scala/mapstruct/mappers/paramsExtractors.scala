package org.mvv.scala.mapstruct.mappers

import scala.quoted.{Expr, Quotes, Type}
//
import org.mvv.scala.mapstruct.{ Logger, lastAfter, isOneOf, getByReflection, unwrapOption }


// TC - Tuple Component Type
type Tuple2Extractor[QTree, TCT1, TCT2] = ( (QTree, QTree) ) => (TCT1, TCT2)


private def parseTuple2EntriesFromSeqExpr[T1, T2, TCT1, TCT2]
  (using quotes: Quotes)(using Type[T1], Type[T2], Type[TCT1], Type[TCT2])
  (inlinedExpr: Expr[Seq[(T1, T2)]],
   tupleExtractor: Tuple2Extractor[quotes.reflect.Tree, TCT1, TCT2]
  ): List[(TCT1, TCT2)] =

  import quotes.reflect.{ asTerm, Inlined }
  parseTuple2Entries[T1, T2, TCT1, TCT2](
    inlinedExpr.asTerm.asInstanceOf[Inlined], tupleExtractor
  )

/** Since macros is used during compile time you cannot create real instance of T1/T2 (java class is not exist yet)
 * For that reason you can only return some representation of this constants
 * (in case of enum it will be String - name of enum filed/val/constant) */
def parseTuple2EntryFromExpr[T1, T2, TCT1, TCT2]
  (using quotes: Quotes)(using Type[T1], Type[T2], Type[TCT1], Type[TCT2])
  (inlinedExpr: Expr[(T1, T2)],
   tupleExtractor: Tuple2Extractor[quotes.reflect.Tree, TCT1, TCT2]): (TCT1, TCT2) =

  import quotes.reflect.{ asTerm, Inlined }
  parseTuple2Entries[T1, T2, TCT1, TCT2](
    inlinedExpr.asTerm.asInstanceOf[Inlined], tupleExtractor
  ).head



private def parseTuple2Entries[T1, T2, TCT1, TCT2]
  (using quotes: Quotes)(using Type[T1], Type[T2], Type[TCT1], Type[TCT2])
  (inlined: quotes.reflect.Inlined,
   tupleExtractor: Tuple2Extractor[quotes.reflect.Tree, TCT1, TCT2]): List[(TCT1, TCT2)] =
  import quotes.reflect.*

  // TODO: impl
  // val inlinedCall: Option[Tree] = inlined.call
  //require(inlinedCall.isEmpty || inlinedCall.get == EmptyTree, "Expected only simple tuple expression.")

  val bindings: List[Definition] = inlined.bindings
  require(bindings.isEmpty, "Expected only simple tuple expression.")

  val body: Term = inlined.body
  val bodyTypeClassName = typeReprFullClassName(body.tpe)

  def getElementsFromTyped(el: Tree): List[Tree] =
    // Typed ( SeqLiteral ( List(
    require(el.isTyped, s"Typed is expected but was $el.")
    getElements(el.asInstanceOf[Typed].expr)

  val elements: List[Tree] = bodyTypeClassName match
    case "Nil" | "scala.Nil" | "scala.collection.immutable.Nil" => Nil
    case "Tuple2" | "scala.Tuple2" => List(body)
    case "_*" | "<repeated>" | "scala.<repeated>" => getElementsFromTyped(body)
    case "List" | "scala.collection.immutable.List" =>
      require(body.isApply, s"Unexpected List format (creating list using :: is not supported, only simple format List(...) is supported)")
      val listApplyArgs: List[Term] = body.asInstanceOf[Apply].args
      require(listApplyArgs.sizeIs == 1) // List.apply has ONE repeated param
      getElementsFromTyped(listApplyArgs.head)
    case other => throw IllegalStateException(s"Unexpected type of body $other ( $body ).")

  val tuples: List[(TCT1, TCT2)] = elements.map(el =>
    require(el.isApply)
    parseApplyWithTypeApplyTuple[T1, T2, TCT1, TCT2](el.asInstanceOf[Apply], tupleExtractor))
  tuples



private def parseApplyWithTypeApplyTuple[T1, T2, TCT1, TCT2]
  (using q: Quotes)(using Type[T1], Type[T2], Type[TCT1], Type[TCT2])
  (applyWithTypeApply: q.reflect.Apply,
   tupleExtractor: Tuple2Extractor[q.reflect.Tree, TCT1, TCT2]): (TCT1, TCT2) =

  import q.reflect.*

  val logPrefix = s"parseApplyWithTypeApplyTuple [ ${Type.show[T1]} => ${Type.show[T2]} ], "

  val bodyAsApply: Apply = applyWithTypeApply
  val bodyApplyFun: Term = bodyAsApply.fun
  val bodyApplyArgs: List[Term] = bodyAsApply.args

  require(bodyApplyFun.isTypeApply)
  val typeApply: TypeApply = bodyApplyFun.asInstanceOf[TypeApply]
  val typeApplySelect: Select = typeApply.fun.asInstanceOf[Select]
  val typeApplyClassName = getTypeApplyClassName(typeApplySelect)

  val errorMsgTuple2TypeIsExpected = "Tuple2 is expected."
  require(typeApplyClassName.isOneOf("Tuple2", "scala.Tuple2"), errorMsgTuple2TypeIsExpected)

  val typeApplyArgs: List[TypeTree] = typeApply.args
  require(typeApplyArgs.sizeIs == 2, errorMsgTuple2TypeIsExpected)
  val typeRepr1 = typeApplyArgs.head.tpe
  val typeRepr2 = typeApplyArgs.tail.head.tpe

  require(typeRepr1 == TypeRepr.of[T1])
  require(typeRepr2 == TypeRepr.of[T2])

  require(bodyApplyArgs.sizeIs == 2, errorMsgTuple2TypeIsExpected)

  val treesTuple = (bodyApplyArgs.head, bodyApplyArgs.tail.head)
  log.trace(s"$logPrefix treesTuple: $treesTuple")

  val tuple: (TCT1, TCT2) = tupleExtractor(treesTuple)
  log.trace(s"$logPrefix tuple: $tuple")
  tuple


// probably not ideal solution
def typeReprFullClassName(using quotes: Quotes)(typeRepr: quotes.reflect.TypeRepr): String =
  val rawClassFullName = typeRepr.classSymbol.map(_.fullName.stripSuffix("$"))
    .getOrElse(typeRepr.show)
  rawClassFullName



private def getElements(using quotes: Quotes)(tree: quotes.reflect.Tree): List[quotes.reflect.Tree] =
  import quotes.reflect.Tree
  tree match
    case el if el.isSeqLiteral => getByReflection(el, "elems", "elements", "items").unwrapOption.asInstanceOf[List[Tree]]
    case other => throw IllegalStateException(s"Getting elements from ${other.getClass.nn.getName} is not supported yet.")
