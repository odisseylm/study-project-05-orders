package org.mvv.scala.quotes

import org.junit.jupiter.api.Test

import scala.quoted.*


class AaaTest {

  @Test
  def sss(): Unit = {

  }


}


def aaa(using q: Quotes)(): Unit = {
  import q.reflect.*

  val el: Tree = ???
  val el2: Apply = ???

  //val ddd22_2: Any = org.mvv.scala.quotes.toQuotesType22[String]("852")
  //val ddd22: Any = toQuotesType22[Apply](el2)
  //val ddd33: Any = toQuotesType33[Any, Any](el, Apply)
  //val ddd34: Any = toQuotesType34[Any](el, Apply)

  //isQuotesType[Apply](el2)

  //val ddd1: Apply = toQuotesType[Apply](el2)
  //val ddd2: Apply = toQuotesType22[Apply](el2)



}



