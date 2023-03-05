package org.mvv.scala.tools.inspection.tasty

import scala.language.unsafeNulls
//
import org.junit.jupiter.api.{ Test, Disabled }
import org.assertj.core.api.Assertions.assertThat
//
import org.mvv.scala.tools.beans.{ BeanProperties, toBeanProperties }
import org.mvv.scala.tools.inspection.InspectMode
import org.mvv.scala.tools.beans.testclasses.InheritedFromJavaClass2



class BeanPropertiesTest {

    @Test
    def beanProps(): Unit = {
        val inspector = TastyScalaBeansInspector()
        val cls = inspector.inspectClass(classOf[InheritedFromJavaClass2])

        val aa: BeanProperties = cls.toBeanProperties(InspectMode.AllSources)
        assertThat(aa).isNotNull
    }
}
