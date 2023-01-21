package com.mvv.implicits

import org.junit.jupiter.api.Test


class ImplicitConversionTest {

  //noinspection ScalaUnusedSymbol
  @Test
  def optionAutoConversion(): Unit = {

    val strOption1: Option[String] = Option("String")
    val strOption2: Option[String] = Some("String")

    import com.mvv.implicits.ImplicitConversion.autoOption
    // this should be compiled successfully
    val strOption3: Option[String] = "String"
  }
}
