package org.mvv.scala.tools.beans

import scala.language.unsafeNulls
import scala.jdk.CollectionConverters.*
//
import org.junit.jupiter.api.Test
import org.assertj.core.api.SoftAssertions
//
import com.mvv.scala3.samples.{ Scala3ClassWithMethods, InheritedFromJavaClass2 }
import org.mvv.scala.tools.beans.testclasses.BaseJavaClass2
//
import org.mvv.scala.tools.Logger
import org.mvv.scala.tools.quotes.{ classNameOf, topClassOrModuleFullName }
import org.mvv.scala.tools.inspection._Type
import org.mvv.scala.tools.inspection.light.ScalaBeanInspector


private val log: Logger = Logger(topClassOrModuleFullName)

trait Trait120 :
    def trait120ValMethod1: String = "567"

trait Trait121 :
    def trait121ValMethod1: String = "567"

class Class122 extends Trait121 :
    val val0: String = "0"

class Class123 extends Class122 with Trait120:
    val val1: String = "1"
    var var1: String = "987654321"

    def valMethod986: String = "987654322"
    def method987(): String = "987654323"


class ScalaBeanInspectorTest {

    @Test
    def inspectClass(): Unit = {

        val inspector = ScalaBeanInspector()

        log.info("--------------------------------------------------------------------")
        inspector.inspectClass(classNameOf[Class123])
        log.info("--------------------------------------------------------------------")
        inspector.inspectClass(classNameOf[BaseJavaClass2])
        log.info("--------------------------------------------------------------------")
        inspector.inspectClass(classNameOf[InheritedFromJavaClass2])
        log.info("--------------------------------------------------------------------")
        inspector.inspectClass(classNameOf[Scala3ClassWithMethods])
    }

    @Test
    def inspectClass2(): Unit = {
        val inspector = ScalaBeanInspector()

        log.info("--------------------------------------------------------------------")
        val _class = inspector.inspectClass(classNameOf[Class123])
        log.info("--------------------------------------------------------------------")

        val a = SoftAssertions()

        a.assertThat(_class.parentTypes.asJava).containsExactlyInAnyOrder(
            _Type("org.mvv.scala.tools.beans.Class122"),
            _Type("org.mvv.scala.tools.beans.Trait120"),
        )

        a.assertAll()
    }
}
