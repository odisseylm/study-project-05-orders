package com.mvv.log

import org.junit.jupiter.api.Test


class LoggerMixinTest {

  class Child extends LoggerMixin
  trait Child1Trait extends LoggerMixin
  trait Child2Trait extends LoggerMixin
  class SuperChild extends Child with Child1Trait with Child2Trait :
    def logSomething(): Unit = log.info("Info log message")

  @Test
  def testDiamondInheritance(): Unit =
    // expects that it is compiled
    SuperChild().logSomething()

  @Test
  def suppressUnused(): Unit =
    val log = com.mvv.log.Logger(getClass)
    log.trace("1")
    log.debug("2")
    log.info("3")
    log.warn("4")
    log.error("5")
}
