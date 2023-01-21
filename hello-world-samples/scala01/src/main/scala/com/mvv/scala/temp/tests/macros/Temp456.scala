package com.mvv.scala.temp.tests.macros

import scala.math.Ordering


trait Show[T]

//object Show :
//  //object derived extends Show[Any]
//  //object derived extends Show[Tree[Any]]
//  object derived extends com.mvv.scala.temp.tests.macros.Show[com.mvv.scala.temp.tests.macros.Tree[T]]

/*
enum Tree[T] derives Show : // /*Eq, Ordering,*/
  case Branch(left: Tree[T], right: Tree[T])
  case Leaf(elem: T)

object Tree :
  object derived extends Show[Tree[Any]]

*/

//case class Point(x: Int, y: Int) derives Ordering

/*
enum Tree[T] derives Eq, Ordering, Show:
  case Branch(left: Tree[T], right: Tree[T])
  case Leaf(elem: T)
*/

trait CanFly[+T]

object CanFly :
  object derived extends CanFly[Any]

case class Point(x: Int, y: Int) //derives CanFly

object Point:
  given aa: Ordering[Point] = new Ordering[Point]() :
    def compare(x: Point, y: Point): Int = { x.x - y.x }

