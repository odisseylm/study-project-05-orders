package org.mvv.scala.quotes

import scala.quoted.{Expr, Quotes, Type}
import scala.collection.mutable
//
import org.mvv.scala.quotes.toQuotesTypeOf
import org.mvv.scala.mapstruct.{ Logger, lastAfter, isOneOf, getByReflection, unwrapOption }



// TC - Tuple Component Type
type ValuesTuple2Extractor[QTree, TC1, TC2] = ( (QTree, QTree) ) => (TC1, TC2)


private val log: Logger = Logger("org.mvv.scala.quotes.tuples")


//noinspection ScalaUnusedSymbol
/* Expected:
  Apply(
    TypeApply(
      Select(Ident(Tuple2),apply),
      List(
        TypeTree[TypeRef(ThisType(TypeRef(NoPrefix,module class mappers)),class TestEnum11)],
        TypeTree[TypeRef(ThisType(TypeRef(NoPrefix,module class mappers)),class TestEnum12)]
      )
    ),
    List(
      Select(Ident(TestEnum11),TestEnumValue3),
      Select(Ident(TestEnum12),TestEnumValue4)
    )
  )
*/
def toValuesTuple2[T1, T2, TC1, TC2]
  (using q: Quotes)(using Type[T1], Type[T2])
  (el: q.reflect.Tree, tupleExtractor: ValuesTuple2Extractor[q.reflect.Tree, TC1, TC2])
  : Option[(TC1, TC2)] =
  import q.reflect.Tree
  val treesTuple: Option[(Tree, Tree)] = toTreesTuple2[T1, T2](el)
  val valuesTuple = treesTuple.map(t => tupleExtractor(t))
  valuesTuple



/**
 * Types T1/T2 are used to filter tuples out with improper types (T1,T2).
 */
def toTreesTuple2[T1, T2]
  (using q: Quotes)(using Type[T1], Type[T2])
  (el: q.reflect.Tree): Option[(q.reflect.Tree, q.reflect.Tree)] =

  import q.reflect.*
  val logPrefix = "toTreesTuple2"

  el.toQuotesTypeOf[Apply]
    .filter ( _.args.sizeIs == 2 )
    .filter ( _.fun.toQuotesTypeOf[TypeApply]
      .exists(typeApply => isTypeApplyOfTuple2[T1, T2](typeApply)) )
    .map { apply =>
      val treesTuple = (apply.args.head, apply.args.tail.head)
      log.trace(s"$logPrefix treesTuple: $treesTuple")
      treesTuple
    }



private def isTypeApplyOfTuple2[T1, T2]
  (using q: Quotes)(using Type[T1], Type[T2])
  (tree: q.reflect.Tree): Boolean =

  import q.reflect.*
  val logPrefix = s"isTypeApplyOfTuple2[${TypeRepr.of[T1]}, ${TypeRepr.of[T2]}] "

  tree.toQuotesTypeOf[TypeApply]
    .filter { typeApply => getTypeApplyClassName(typeApply).isTuple2 }
    .filter { typeApply =>
      val arg1TypeRepr = typeApply.args.head.tpe
      val arg2TypeRepr = typeApply.args.tail.head.tpe
      log.trace(s"$logPrefix TypeApply(${arg1TypeRepr.show}, ${arg2TypeRepr.show})")

      (arg1TypeRepr <:< TypeRepr.of[T1]) && (arg2TypeRepr <:< TypeRepr.of[T2])
    }
    .isDefined
