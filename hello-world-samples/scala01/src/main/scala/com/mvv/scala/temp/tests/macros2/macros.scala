package com.mvv.scala.temp.tests.macros2

import scala.annotation.unused
import scala.quoted.{Expr, Quotes}


inline def asBeanValue(@unused inline expr: Any): BeanPropertyValue[Any, Any] =
  ${ asBeanValueImpl('expr) }

def asBeanValueImpl(expr: Expr[Any])(using Quotes): Expr[BeanPropertyValue[Any, Any]] = '{
  //if !$expr then
  com.mvv.scala.temp.tests.macros2.BeanPropertyValue.beanPropertyValue[Any, Any](
    "", //asText(expr), //expr.show,
    //$expr
    $expr
  )
}






inline def asBeanValue2(@unused inline expr: Any): Any =
  ${ asBeanValue2Impl('expr) }


def asBeanValue2Impl(expr: Expr[Any])(using q: Quotes): Expr[Any] = {
  val aa: String = expr.show
  val aa22 = Expr(expr.show)
  //val value = expr.value
  //Expr(12390) <=== Works!!!
  //Expr(com.mvv.scala.temp.tests.macros2.BeanPropertyValue.beanPropertyValue[Any, Any]("prrrrrrr", "aasssssfjdfgdfjdlj"))
  //Expr(com.mvv.scala.temp.tests.macros2.BeanPropValue("prrrrrrr", "aasssssfjdfgdfjdlj"))
  //Expr.apply("com.mvv.scala.temp.tests.macros2.BeanPropValue(\"prrrrrrr\", \"aasssssfjdfgdfjdlj\")")
  //Expr.apply(List(1)) <=== Works!!!
  //Expr.apply((aa, expr.valueOrAbort))

  //Expr.apply(List(expr))
  //Expr.ofTuple( ("asdfghjkl", "fgfgf") )
  //Expr.ofList(List("asdfghjkl", "fgfgf"))

  //Expr.ofList(List(Expr(expr.show), expr, expr, Expr(42), Expr("42"))) // <=== Works!!!

  //expr.summon[com.mvv.scala.temp.tests.macros2.Bbbbbb]()
  //q.summon[com.mvv.scala.temp.tests.macros2.Bbbbbb]()
  val ddd = Expr.summon[com.mvv.scala.temp.tests.macros2.Bbbbbb]
  println(s"summon22: $ddd")

  //var tt = expr match
  //  case '{ $expr: t } => t

  Expr.ofTuple( (Expr(expr.show), expr) ) // <=== Works!!!

  //'{ List("vcv", ${ expr }, ${ aa22 } ) } // <=== working !!!!!
  //'{ com.mvv.scala.temp.tests.macros2.BeanPropertyValue.beanPropertyValue[Any, Any]( ${ aa22 }, ${ expr } ) // <=== working !!!!!
  '{ com.mvv.scala.temp.tests.macros2.BeanPropertyValue.beanPropertyValue[Any, Any]( $aa22, $expr )
  } // <=== working !!!!!
}


def asText(x: Expr[Any])(using Quotes): String = x.show.toString


inline def inspect(inline x: Any): Any = ${ inspectCode('x) }

def inspectCode(x: Expr[Any])(using Quotes): Expr[Any] =
  println(x.show)
  x
