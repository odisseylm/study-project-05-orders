package com.mvv.scala2.samples;

import org.assertj.core.api.Assertions

import scala.reflect.runtime.{universe => ru}
//
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat


class Scala2ReflectTest {

  @Test
  def inspect(): Unit = {
    val mirror: ru.Mirror = ru.runtimeMirror(getClass.getClassLoader)
    val classSymbol: ru.ClassSymbol = mirror.staticClass("com.mvv.scala2.samples.InheritedFromJavaClass2")

    assertThat(classSymbol).isNotNull

    assertThat(classSymbol.isClass).isTrue

    //val personInterfaceType: ru.MemberScope = ru.typeOf[InheritedFromJavaClass2].decls
    val personClassType = ru.typeOf[InheritedFromJavaClass2].decls

    personClassType.foreach { symbol =>
      println(s"declared member: ${symbol.name}, type signature: ${symbol.typeSignature.toString}")
    }
  }

}
