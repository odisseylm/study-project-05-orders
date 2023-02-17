package org.mvv.scala.mapstruct.mappers

import scala.quoted.{Expr, Quotes, Type}
//
import org.mvv.scala.mapstruct.{ Logger, lastAfter, isOneOf, getByReflection, unwrapOption }


type Tuple2Extractor[QTree, T1, T2] = ( (QTree, QTree) ) => (T1, T2)


private def parseTuple2EntriesFromSeqExpr[T1, T2]
  (using quotes: Quotes)(using Type[T1], Type[T2])
  (inlinedExpr: Expr[Seq[(T1, T2)]],
   tupleExtractor: Tuple2Extractor[quotes.reflect.Tree, T1, T2]): List[(T1, T2)] =
  import quotes.reflect.asTerm
  parseTuple2Entries[T1, T2](
    inlinedExpr.asTerm.asInstanceOf[quotes.reflect.Inlined],
    tupleExtractor,
  )

def parseTuple2EntryFromSeqExpr[T1, T2]
  (using quotes: Quotes)(using Type[T1], Type[T2])
  (inlinedExpr: Expr[(T1, T2)],
   tupleExtractor: Tuple2Extractor[quotes.reflect.Tree, T1, T2]): (T1, T2) =
  import quotes.reflect.asTerm
  parseTuple2Entries[T1, T2](
    inlinedExpr.asTerm.asInstanceOf[quotes.reflect.Inlined],
    tupleExtractor,
  ).head



private def parseTuple2Entries[T1, T2]
  (using quotes: Quotes)(using Type[T1], Type[T2])
  (inlined: quotes.reflect.Inlined,
   tupleExtractor: Tuple2Extractor[quotes.reflect.Tree, T1, T2]): List[(T1, T2)] =
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

  val tuples: List[(T1, T2)] = elements.map(el =>
    require(el.isApply)
    parseApplyWithTypeApplyTuple[T1, T2](el.asInstanceOf[Apply], tupleExtractor))
  tuples



private def parseApplyWithTypeApplyTuple[T1, T2]
  (using quotes: Quotes)(using Type[T1], Type[T2])
  (applyWithTypeApply: quotes.reflect.Apply,
   tupleExtractor: Tuple2Extractor[quotes.reflect.Tree, T1, T2]): (T1, T2) =

  import quotes.reflect.*

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

  val tuple = tupleExtractor(treesTuple)
  log.trace(s"$logPrefix tuple: $tuple")
  tuple
