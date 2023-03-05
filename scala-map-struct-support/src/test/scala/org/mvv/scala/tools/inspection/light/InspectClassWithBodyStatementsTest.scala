package org.mvv.scala.tools.inspection.light

import scala.annotation.nowarn
import scala.jdk.CollectionConverters.*
//
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.{ assertThat, assertThatCode }
//
import org.mvv.scala.tools.beans.toBeanProperties
import org.mvv.scala.tools.inspection.{ InspectMode, _Class }
import org.mvv.scala.tools.inspection.{ ClassWithBodyStatements, ClassWithThrowInBody }



class InspectClassWithBodyStatementsTest {
  import scala.language.unsafeNulls

  @Test
  def testClassWithExpressionsInClassBody(): Unit = {
    val cls: _Class = ScalaBeanInspector().inspectClass(classOf[ClassWithBodyStatements])
    val beanProps = cls.toBeanProperties(InspectMode.AllSources)

    /*
    // with runtime types
    assertThat(cls.fields.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
      "lazyProp: java.lang.String",
      "Temp: ClassWithBodyStatements.this.Temp/org.mvv.scala.tools.inspection.tasty.ClassWithBodyStatements$Temp$"
    )
    */
    assertThat(cls.fields.keys.map(_.toString).asJava).containsExactlyInAnyOrder(
      "lazyProp: java.lang.String",
      "Temp: ClassWithBodyStatements.this.Temp"
    )
    /*
    assertThat(cls.methods.keys.map(_.toString).asJava).contains( //.containsExactlyInAnyOrder(
      "<init>(java.lang.String)",
    )
    */

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
        ScalaBeanInspector().inspectClass(classOf[ClassWithThrowInBody])
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