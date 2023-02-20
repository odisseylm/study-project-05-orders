package org.mvv.scala.quotes

import scala.quoted.{Expr, Quotes, Type}
import scala.collection.mutable
//
import org.mvv.scala.quotes.toQuotesTypeOf
import org.mvv.scala.mapstruct.{ Logger, lastAfter, isOneOf, getByReflection, unwrapOption }



def extractTuple2EntriesFromSeqExpr[T1, T2, TC1, TC2]
  (using q: Quotes)(using Type[T1], Type[T2])
  (inlinedExpr: Expr[Seq[(T1, T2)]],
   tupleExtractor: ValuesTuple2Extractor[q.reflect.Tree, TC1, TC2]
  ): List[(TC1, TC2)] =

  import q.reflect.{ asTerm, Inlined }
  extractValuesTuple2Entries[T1, T2, TC1, TC2](
    inlinedExpr.asTerm, tupleExtractor
  )



/** Since macros is used during compile time you cannot create real instance of T1/T2 (java class is not exist yet)
 * For that reason you can only return some representation of this constants
 * (in case of enum it will be String - name of enum filed/val/constant) */
def extractTuple2EntryFromExpr[T1, T2, TC1, TC2]
  (using quotes: Quotes)(using Type[T1], Type[T2])
  (inlinedExpr: Expr[(T1, T2)],
   tupleExtractor: ValuesTuple2Extractor[quotes.reflect.Tree, TC1, TC2]): (TC1, TC2) =

  import quotes.reflect.{ asTerm, Inlined }
  extractValuesTuple2Entries[T1, T2, TC1, TC2](
    inlinedExpr.asTerm.asInstanceOf[Inlined], tupleExtractor
  ).head



private def extractValuesTuple2Entries[T1, T2, TC1, TC2]
  (using q: Quotes)(using Type[T1], Type[T2])
  ( tree: q.reflect.Tree, tupleExtractor: ValuesTuple2Extractor[q.reflect.Tree, TC1, TC2])
  : List[(TC1, TC2)] =
  import q.reflect.*
  val treesTuples: List[(Tree, Tree)] = extractTreesTuple2Entries[T1, T2](tree)
  treesTuples.map(t => tupleExtractor(t))



private def extractTreesTuple2Entries[T1, T2]
  (using q: Quotes)(using Type[T1], Type[T2])
  (tree: q.reflect.Tree)
  : List[(q.reflect.Tree, q.reflect.Tree)] =

  import q.reflect.*
  val logPrefix = "parseTuple2Entries"

  val tuples: mutable.ArrayBuffer[(Tree, Tree)] = mutable.ArrayBuffer()

  val traverser = new TreeTraverser {
    override def traverseTree(tree: Tree)(owner: Symbol): Unit =
      val traverseLogPrefix = s"$logPrefix/traverseTree of ${tree.getClass.nn.getSimpleName}"
      try
        log.trace(s"$traverseLogPrefix")

        val tuple: Option[(Tree, Tree)] = qToTreesTuple2[T1, T2](tree)
        tuple.foreach(t => tuples.addOne(t))

        log.trace(s"$traverseLogPrefix => tuple: $tuple")

        // do not traverse to children if needed AST node is found
        if tuple.isEmpty then super.traverseTree(tree)(owner)
      catch
        // There is AssertionError because it can be thrown by scala compiler?!
        //noinspection ScalaUnnecessaryParentheses => braces are really needed there!!!
        case ex: (Exception | AssertionError) => log.warn(s"$traverseLogPrefix => Unexpected error $ex", ex)
  }

  traverser.traverseTree(tree)(tree.symbol)
  List.from(tuples)



/*
private def parseTuple2Entries[T1, T2, TC1, TC2]
  (using quotes: Quotes)(using Type[T1], Type[T2], Type[TC1], Type[TC2])
  (inlined: quotes.reflect.Inlined,
   tupleExtractor: ValuesTuple2Extractor[quotes.reflect.Tree, TC1, TC2]): List[(TC1, TC2)] =
  import quotes.reflect.*

  parseTuple2Entries_new[T1, T2, TC1, TC2](inlined, tupleExtractor)

  // T O D O: impl
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

  val tuples: List[(TC1, TC2)] = elements.map(el =>
    require(el.isApply)
    parseApplyWithTypeApplyTuple[T1, T2, TC1, TC2](el.asInstanceOf[Apply], tupleExtractor))
  tuples


private def getElements(using quotes: Quotes)(tree: quotes.reflect.Tree): List[quotes.reflect.Tree] =
  import quotes.reflect.Tree
  tree match
    case el if el.isSeqLiteral => getByReflection(el, "elems", "elements", "items").unwrapOption.asInstanceOf[List[Tree]]
    case other => throw IllegalStateException(s"Getting elements from ${other.getClass.nn.getName} is not supported yet.")
*/
