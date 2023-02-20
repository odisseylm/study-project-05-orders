package org.mvv.scala.mapstruct

import scala.language.unsafeNulls
//
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
//
import org.mvv.scala.mapstruct.testclasses.InheritedFromJavaClass2


class BeanPropertiesTest {

    @Test
    def beanProps(): Unit = {

        val inspector = ScalaBeansInspector()

        val cls = inspector.inspectClass(classOf[InheritedFromJavaClass2])

        val aa: BeanProperties = cls.beanProperties
        assertThat(aa).isNotNull
    }
}
