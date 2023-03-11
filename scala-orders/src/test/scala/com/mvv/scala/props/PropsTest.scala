package com.mvv.scala.props

import scala.language.unsafeNulls
import scala.jdk.CollectionConverters.*
import java.time.ZonedDateTime
//
import org.junit.jupiter.api.{ Test, DisplayName }
import org.assertj.core.api.Assertions.assertThat
//
import com.mvv.log.Logger
import com.mvv.bank.orders.domain.{AbstractOrder, Order}


class PropsTest {

  trait Trait1 :
    val log: com.mvv.log.Logger // should be ignored
    var prop1: String
    val prop2: Int
    def getProp2: Int // duplicate should be ignored
    def prop3: ZonedDateTime
    def prop4_=(v: ZonedDateTime): Unit // should be ignored
    def setProp5(v: ZonedDateTime): Unit // should be ignored

  class Trait1Impl extends Trait1:
    override val log: Logger = Logger(classOf[Trait1Impl])
    var prop1: String = "567"
    override val prop2: Int = 852
    override def getProp2: Int = 951
    override def prop3: ZonedDateTime = ZonedDateTime.now
    override def prop4_=(v: ZonedDateTime): Unit = {}
    override def setProp5(v: ZonedDateTime): Unit = {}

    val newProp1: String = ""
    var newProp2: String = ""
    def newProp3: String = ""


  /*
  @Test
  @DisplayName("collectRequiredPropertiesOfTrait")
  def testCollectRequiredPropertiesOfTrait(): Unit = {
    val propNames = collectRequiredProperties(classOf[Trait1])
    assertThat(propNames.asJava).containsExactlyInAnyOrder("prop1", "prop2", "prop3")
  }

  @Test
  @DisplayName("collectRequiredPropertiesOfClass")
  def testCollectRequiredPropertiesOfClass(): Unit = {
    val propNames = collectRequiredProperties(classOf[Trait1Impl])
    assertThat(propNames.asJava)
      .containsExactlyInAnyOrder("prop1", "prop2", "prop3", "newProp1", "newProp2", "newProp3")
  }
  */
}
