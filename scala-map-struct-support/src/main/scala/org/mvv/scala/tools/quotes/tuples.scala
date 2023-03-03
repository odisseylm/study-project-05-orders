package org.mvv.scala.tools.quotes

import scala.quoted.{Expr, Quotes, Type}
import scala.collection.mutable
//
import org.mvv.scala.tools.Logger



// TC - Tuple Component Type
type ValuesTuple2Extractor[QTree, TC1, TC2] = ( (QTree, QTree) ) => (TC1, TC2)


private val log: Logger = Logger(topClassOrModuleFullName)


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
def qToValuesTuple2[T1, T2, TC1, TC2]
  (using q: Quotes)(using Type[T1], Type[T2])
  (el: q.reflect.Tree, tupleExtractor: ValuesTuple2Extractor[q.reflect.Tree, TC1, TC2])
  : Option[(TC1, TC2)] =
  import q.reflect.Tree
  val treesTuple: Option[(Tree, Tree)] = qToTreesTuple2[T1, T2](el)
  val valuesTuple = treesTuple.map(t => tupleExtractor(t))
  valuesTuple



/**
 * Types T1/T2 are used to filter tuples out with improper types (T1,T2).
 */
def qToTreesTuple2[T1, T2]
  (using q: Quotes)(using Type[T1], Type[T2])
  (el: q.reflect.Tree): Option[(q.reflect.Tree, q.reflect.Tree)] =

  import q.reflect.{ Apply, TypeApply }
  val logPrefix = "toTreesTuple2"

  el match
    case apply: Apply =>
      Option(apply)
        .filter ( _.args.sizeIs == 2 )
        .filter { _.fun match
            case typeApply: TypeApply => isTypeApplyOfTuple2[T1, T2](typeApply)
            case _ => false
        }
        .map { apply =>
          val treesTuple = (apply.args.head, apply.args.tail.head)
          log.trace(s"$logPrefix treesTuple: $treesTuple")
          treesTuple
        }
    case _ => None



private def isTypeApplyOfTuple2[T1, T2]
  (using q: Quotes)(using Type[T1], Type[T2])
  (tree: q.reflect.Tree): Boolean =

  import q.reflect.{ TypeApply, TypeRepr }
  val logPrefix = s"isTypeApplyOfTuple2[${TypeRepr.of[T1]}, ${TypeRepr.of[T2]}] "

  tree match
    case typeApply: TypeApply =>
      Option(typeApply)
        .filter { typeApply => getTypeApplyClassName(typeApply).isTuple2Type }
        .filter { typeApply =>
          val arg1TypeRepr = typeApply.args.head.tpe
          val arg2TypeRepr = typeApply.args.tail.head.tpe
          log.trace(s"$logPrefix TypeApply(${arg1TypeRepr.show}, ${arg2TypeRepr.show})")

          (arg1TypeRepr <:< TypeRepr.of[T1]) && (arg2TypeRepr <:< TypeRepr.of[T2])
        }
        .isDefined
    case _ => false
