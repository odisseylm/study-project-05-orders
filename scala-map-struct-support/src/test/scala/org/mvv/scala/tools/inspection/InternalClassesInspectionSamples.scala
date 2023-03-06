package org.mvv.scala.tools.inspection

import scala.annotation.nowarn



class TopClass :
  class TopClassInternalClassLevel1 :
    class TopClassInternalClassLevel2


object TopClass :
  class TopObjectInternalClassLevel1:
    class TopObjectInternalClassLevel2

  object TopObjectObjectLevel2 :
    class TopObjectObjectLevel2InternalClassLevel1:
      class TopObjectObjectLevel2InternalClassLevel2



//noinspection ScalaUnusedExpression
@nowarn("msg=pure expression does nothing")
class ClassWithBodyStatements :
  print("LazyClass creation\n")

  if 1 == System.currentTimeMillis then { }

  System.currentTimeMillis match
    case 11 =>
    case 22 =>
    case _  =>

  { print("") }

  // it is represented as Apply... (see full details below)
  for var345 <- List(1, 2) do { var345 }

  try {} catch case _: Exception => {}

  import java.time.LocalTime

  object Temp ;

  //noinspection ScalaUnusedSymbol
  def this(s: String) = { this() }

  new java.util.Date()
  null
  "Some string"
  123
  123.0
  true
  false

  type Type1

  while false do {}

  // these keywords cannot be used
  //this()
  //yield
  //with
  // forSome

  lazy val lazyProp: String = { s"${this.hashCode}" }
end ClassWithBodyStatements


class ClassWithThrowInBody :
  // in AST throw is Apply(
  //   Apply( Ident(throw),
  //     List( Apply(
  //      Select( New(Ident(IllegalStateException)), <init> ),
  //      List(Literal(Constant(Some error.)))
  //   )))
  throw IllegalStateException("Some error.")
  print("ClassWithThrowInBody\n")
