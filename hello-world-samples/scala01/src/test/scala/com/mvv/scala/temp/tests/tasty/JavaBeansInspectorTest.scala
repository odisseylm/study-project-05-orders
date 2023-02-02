package com.mvv.scala.temp.tests.tasty;

import scala.jdk.CollectionConverters.*
//
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test


class JavaBeansInspectorTest {

  @Test
  def inspect(): Unit = {
    import scala.language.unsafeNulls
    val inspector = JavaBeansInspector()

    val _class = inspector.inspect(classOf[BaseJavaClass2])
    println(s"_class: $_class")

    val a = SoftAssertions()

    a.assertThat(inspector.classesDescr.asJava)
      .containsOnlyKeys(
        "com.mvv.scala.temp.tests.tasty.com.mvv.scala.temp.tests.tasty.BaseJavaClass1",
        "com.mvv.scala.temp.tests.tasty.com.mvv.scala.temp.tests.tasty.BaseJavaClass2",
        "com.mvv.scala.temp.tests.tasty.com.mvv.scala.temp.tests.tasty.Interface2",
        "com.mvv.scala.temp.tests.tasty.com.mvv.scala.temp.tests.tasty.Interface1",
      )

    a.assertAll()
  }

}
