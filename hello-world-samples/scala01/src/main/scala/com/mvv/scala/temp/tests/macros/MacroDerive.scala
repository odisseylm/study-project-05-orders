//noinspection TypeAnnotation
package com.mvv.scala.temp.tests.macros

import scala.deriving.*
import scala.quoted.*


trait Eq[T]:
  def eqv(x: T, y: T): Boolean

object Eq:
  given Eq[String] with
    def eqv(x: String, y: String) = x == y

  given Eq[Int] with
    def eqv(x: Int, y: Int) = x == y

  def eqProduct[T](body: (T, T) => Boolean): Eq[T] =
    new Eq[T]:
      def eqv(x: T, y: T): Boolean = body(x, y)

  def eqSum[T](body: (T, T) => Boolean): Eq[T] =
    new Eq[T]:
      def eqv(x: T, y: T): Boolean = body(x, y)

  def summonAll[T: Type](using Quotes): List[Expr[Eq[?]]] =
    Type.of[T] match
      case '[String *: tpes] => '{ summon[Eq[String]] } :: summonAll[tpes]
      case '[Int *: tpes]    => '{ summon[Eq[Int]] }    :: summonAll[tpes]
      case '[tpe *: tpes]    => derived[tpe] :: summonAll[tpes]
      case '[EmptyTuple]     => Nil

  given derived[T: Type](using q: Quotes): Expr[Eq[T]] =
    import quotes.reflect.*

    val ev: Expr[Mirror.Of[T]] = Expr.summon[Mirror.Of[T]].get

    ev match
      case '{ $m: Mirror.ProductOf[T] { type MirroredElemTypes = elementTypes }} =>
        val elemInstances = summonAll[elementTypes]
        val eqProductBody: (Expr[T], Expr[T]) => Expr[Boolean] = (x, y) =>
          elemInstances.zipWithIndex.foldLeft(Expr(true: Boolean)) {
            case (acc, (elem, index)) =>
              val e1 = '{$x.asInstanceOf[Product].productElement(${Expr(index)})}
              val e2 = '{$y.asInstanceOf[Product].productElement(${Expr(index)})}

              '{ $acc && $elem.asInstanceOf[Eq[Any]].eqv($e1, $e2) }
          }
        '{ eqProduct((x: T, y: T) => ${eqProductBody('x, 'y)}) }

      case '{ $m: Mirror.SumOf[T] { type MirroredElemTypes = elementTypes }} =>
        val elemInstances = summonAll[elementTypes]
        val eqSumBody: (Expr[T], Expr[T]) => Expr[Boolean] = (x, y) =>
          val ordx = '{ $m.ordinal($x) }
          val ordy = '{ $m.ordinal($y) }

          val elements = Expr.ofList(elemInstances)
            '{ $ordx == $ordy && $elements($ordx).asInstanceOf[Eq[Any]].eqv($x, $y) }

        '{ eqSum((x: T, y: T) => ${eqSumBody('x, 'y)}) }
  end derived
end Eq

object Macro3:
  extension [T](inline x: T)
    inline def === (inline y: T)(using eq: Eq[T]): Boolean = eq.eqv(x, y)

  inline given eqGen[T]: Eq[T] = ${ Eq.derived[T] }
