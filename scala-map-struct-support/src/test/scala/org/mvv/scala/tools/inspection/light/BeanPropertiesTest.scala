package org.mvv.scala.tools.inspection.light

import scala.language.unsafeNulls
//
import org.junit.jupiter.api.{ Test, Disabled }
import org.assertj.core.api.Assertions.assertThat
//
import org.mvv.scala.tools.quotes.classNameOf
import org.mvv.scala.tools.beans.{ BeanProperties, toBeanProperties }
import org.mvv.scala.tools.inspection.InspectMode
import org.mvv.scala.tools.beans.testclasses.InheritedFromJavaClass2
import org.mvv.scala.tools.inspection.light.LightScalaBeanInspector as ScalaBeanInspector


class BeanPropertiesTest {

    @Test
    def beanProps(): Unit = {
        val inspector = ScalaBeanInspector()
        val cls = inspector.inspectClass(classNameOf[InheritedFromJavaClass2])

        val aa: BeanProperties = cls.toBeanProperties(InspectMode.AllSources)
        assertThat(aa).isNotNull
    }
}
