package org.mvv.scala.tools.beans

import scala.language.unsafeNulls
//
import org.junit.jupiter.api.Test
//
import com.mvv.scala3.samples.{ Scala3ClassWithMethods, InheritedFromJavaClass2 }
import org.mvv.scala.tools.beans.testclasses.BaseJavaClass2
//
import org.mvv.scala.tools.Logger
import org.mvv.scala.tools.quotes.{ classNameOf, topClassOrModuleFullName }
import org.mvv.scala.tools.inspection.light.LightScalaBeanInspector


private val log: Logger = Logger(topClassOrModuleFullName)

class Class123:
    val val1: String = "1"
    var var1: String = "987654321"

    def valMethod986: String = "987654322"
    def method987(): String = "987654323"


class LightScalaBeanInspectorTest {

    @Test
    def inspectClass(): Unit = {

        val inspector = LightScalaBeanInspector()

        log.info("--------------------------------------------------------------------")
        inspector.inspectClass(classNameOf[Class123])
        log.info("--------------------------------------------------------------------")
        inspector.inspectClass(classNameOf[BaseJavaClass2])
        log.info("--------------------------------------------------------------------")
        inspector.inspectClass(classNameOf[InheritedFromJavaClass2])
        log.info("--------------------------------------------------------------------")
        inspector.inspectClass(classNameOf[Scala3ClassWithMethods])
    }
}
