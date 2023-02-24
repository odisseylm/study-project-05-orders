package org.mvv.scala.tools.props

import scala.quoted.{ Expr, Type, Quotes }
import org.mvv.scala.tools.quotes.qClassNameOf



inline def namedValue[T](inline valueWithName: T): NamedValue[T] =
  ${ namedValueImpl[T]('valueWithName) }



def namedValueImpl[T]
  (using q: Quotes)(using Type[T], Type[NamedValue[T]])
  (valueWithName: Expr[T]): Expr[NamedValue[T]] =
  import q.reflect.*

  val namedValueClassTerm = qClassNameOf[NamedValue[T]] // to avoid hard-coding NamedValue package
  val namedValueApplyFunSelect = Select.unique(namedValueClassTerm, "apply")
  val typeApply = TypeApply(namedValueApplyFunSelect, List(TypeTree.of[T]))
  val nameOfValueWithName = valueWithName.show
  val namedValueApply = Apply(typeApply, List(Literal(StringConstant(nameOfValueWithName)), valueWithName.asTerm))
  val resInlined = Inlined(None, Nil, namedValueApply)
  resInlined.asExprOf[NamedValue[T]]
