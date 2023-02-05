package com.mvv.scala.temp.tests.tasty

import com.mvv.scala.temp.tests.tasty.testclasses.GenericClass2
import com.mvv.scala.temp.tests.tasty.testclasses.JGenericClass2
import org.junit.jupiter.api.Test

class GenericsTest {

  @Test
  def smokeTest(): Unit = {

    val inspector = ScalaBeansInspector()
    val _class =  inspector.inspectClass(classOf[GenericClass2])

    println(_class.parents.dump("Parents"))
    println(_class.parentTypeNames.dump("Parent type names"))
    println(_class.fields.keys.dump("Fields"))
    println(_class.methods.keys.dump("Methods"))
  }

  @Test
  def sss(): Unit = {
    val cls = classOf[com.mvv.scala.temp.tests.tasty.testclasses.JGenericClass2]
    val ms = cls.getMethods.nnArray
    println(ms)
  }

  extension (collection: IterableOnce[?])
    private def dump(label: String): String =
      collection.iterator.mkString(s"$label:\n    ", "\n    ", "\n")
}
