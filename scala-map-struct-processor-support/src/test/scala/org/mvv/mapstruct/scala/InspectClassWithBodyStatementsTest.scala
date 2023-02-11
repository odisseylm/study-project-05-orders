package org.mvv.mapstruct.scala

import scala.annotation.nowarn
import scala.jdk.CollectionConverters.*
//
import org.assertj.core.api.Assertions.{ assertThat, assertThatCode }
import org.junit.jupiter.api.Test


//noinspection ScalaUnusedExpression
@nowarn("msg=pure expression does nothing")
class ClassWithBodyStatements :
  println("LazyClass creation")

  if 1 == System.currentTimeMillis then { }

  System.currentTimeMillis match
    case 11 =>
    case 22 =>
    case _  =>

  { println("") }

  // it is represented as Apply... (see full details below)
  for var345 <- List(1, 2) do { var345 }

  try {} catch case _: Exception => {}

  import java.time.LocalTime

  object Temp ;

  //noinspection ScalaUnusedSymbol
  def this(s: String) = { this() }

  //throw TODO

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
  println("ClassWithThrowInBody")




class InspectClassWithBodyStatementsTest {
  import scala.language.unsafeNulls

  @Test
  def testClassWithExpressionsInClassBody(): Unit = {
    val cls: _Class = ScalaBeansInspector().inspectClass(classOf[ClassWithBodyStatements])
    val beanProps = cls.beanProperties

    assertThat(cls.fields.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
      "lazyProp: java.lang.String",
      "Temp: ClassWithBodyStatements.this.Temp/org.mvv.mapstruct.scala.ClassWithBodyStatements$Temp$"
    )
    assertThat(cls.methods.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
      "<init>(java.lang.String)",
    )

    assertThat(beanProps.beanProps.keys.asJava).contains(
      "lazyProp",
      // hm... Undefined behavior:
      // Why is it there? Is it ok or not?
      "Temp",
    )
  }

  @Test
  def testClassWithThrowInBody(): Unit = {
    assertThatCode {
        ScalaBeansInspector().inspectClass(classOf[ClassWithThrowInBody])
        return // needed since java lambda is required
      }
      .doesNotThrowAnyException()
  }
}



/*
Current 'for' AST

Apply(
  TypeApply(
    Select(Apply(TypeApply(
      Select(Ident(List),apply),List(TypeTree[TypeRef(TermRef(ThisType(TypeRef(NoPrefix,module class <root>)),object scala),Int)])),
      List(Typed(SeqLiteral(
            List(Literal(Constant(1)), Literal(Constant(2))),
            TypeTree[TypeRef(TermRef(ThisType(TypeRef(NoPrefix,module class <root>)),object scala),Int)]
          ),
          TypeTree[
            AppliedType(TypeRef(TermRef(ThisType(TypeRef(NoPrefix,module class <root>)),object scala),<repeated>),List(TypeRef(TermRef(ThisType(TypeRef(NoPrefix,module class <root>)),object scala),Int)))]
          ))
      ),
      foreach
      ),
      List(TypeTree[TypeRef(TermRef(ThisType(TypeRef(NoPrefix,module class <root>)),object scala),Int)])
  ),
  List(Block(List(DefDef(
    $anonfun,
    List(
      List(
        ValDef(
          var345,
          TypeTree[TypeRef(TermRef(ThisType(TypeRef(NoPrefix,module class <root>)),object scala),Int)],
          EmptyTree
        )
      )
    ),
  TypeTree[TypeRef(TermRef(ThisType(TypeRef(NoPrefix,module class <root>)),object scala),Int)],Block(List(),Ident(var345)))),
  Closure(List(),Ident($anonfun),EmptyTree))))
*/