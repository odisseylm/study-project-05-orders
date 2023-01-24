package com.mvv.scala.temp.tests.macros2

import com.mvv.scala.macros.asPropValue
import com.mvv.scala.macros.PropValue

def aa(): Unit = {}


case class Rfvtgb (v: String)

def localVal: String = "gjhgjhgjh3333"
def localFunction1: String = "gjhgjhgjh"
def localFunction2(): String = "gjhgjhgjh"


@main
def Bbbbbb_test2(): Unit = {
  Bbbbbb().test2()
}

class Bbbbbb :
  val aaaStr: String = "str54646"
  val aaa: Rfvtgb = Rfvtgb("54646")
  def bbb1: Rfvtgb = Rfvtgb("54646")
  def bbb2(): Rfvtgb = Rfvtgb("54646")
  def optionProp: Option[Rfvtgb] = Option(Rfvtgb("54646"))

  def test1(): Unit = {
    val ccc = asBeanValue(aaa)
    println(s"bean value: $ccc")

    val ccc2: BeanPropertyValue[Any, Any] = asBeanValue(aaa) //.asInstanceOf[String, Any]
    val str2: String = ccc2.value.get.asInstanceOf[String]

    val ccc3 = asBeanValue(aaa) //.asInstanceOf[Int, Any]
    val int3: Int = ccc3.value.get.asInstanceOf[Int]

    //val jhdgfhdg = Bbbbbb.this.aaa

    inspect(aaa)
    //inspect(s"asdf: $aaa")

    /*
    val er1 = asBeanValue2(67890)
    val er2 = asBeanValue2(aaa)
    val er3 = asBeanValue2(bbb1)
    // val er3_ = asBeanValue2(bbb1()) /// <=== Good! Compilation error
    // val er4 = asBeanValue2(bbb2)    /// <=== Good! Compilation error
    val er4_ = asBeanValue2(bbb2())
    */

    val sss1_0 = asBeanValue3(aaaStr)
    val sss1_1 = asBeanValue3(aaa)
    val sss1_2 = asBeanValue3(bbb1)
    val sss1_3 = asBeanValue3(bbb2())
  }

  def test2(): Unit = {
    val sss2_0 = asPropValue(aaaStr)
    val sss2_1 = asPropValue(aaa)
    val sss2_2 = asPropValue(bbb1)
    val sss2_3 = asPropValue(bbb2())
    val sss2_4 = asPropValue(optionProp)

    println(s" $sss2_0 $sss2_1 $sss2_2 $sss2_3 $sss2_4 ")

    val a1: Option[String] = sss2_0.value
    val a2_1: Option[Rfvtgb] = optionProp
    val a2_2: Option[Rfvtgb] = sss2_4.value

    println(s" $a1 $a2_1 $a2_2 ")

    require(sss2_4.value == optionProp)
    require(sss2_0.value.orNull == aaaStr)

    //val sss3_0: PropValue[String, Any] = asPropValue(() => aaaStr)
    //val sss3_0: PropValue[String, Bbbbbb] = asPropValue(this, o => o.aaaStr)
    val sss3_0: PropValue[String, Bbbbbb] = asPropValue(this, aaaStr)
    val sss3_1: PropValue[Rfvtgb, Bbbbbb] = asPropValue(this, optionProp)
    val sss4_0 = asPropValue(this, aaaStr)
    val sss4_1 = asPropValue(this, optionProp)
    println(s" $sss3_0 $sss3_1 ")

  }

  /*
  def shouldBeCompiledWithError(): Unit = {
    val sss3_1 = asPropValue(localVal)
    val sss3_2 = asPropValue(localFunction1)
    val sss3_3 = asPropValue(localFunction2())
    println(s" $sss3_1 $sss3_2 $sss3_3")
  }
  */

  //val ddd3: BeanPropertyValue[Any, Any] = asBeanValue(aaa)
  // !!! in this case generated code also will use [Any, Any]
  //noinspection TypeAnnotation // Explicit type is not use to verify generated types
  val ddd3: PropValue[Rfvtgb, Any] = asPropValue(aaa)
  //noinspection TypeAnnotation // Explicit type is not use to verify generated types
  val ddd4 = asPropValue(aaa)
  //noinspection TypeAnnotation // Explicit type is not use to verify generated types
  val ddd5 = asPropValue(optionProp)
  //noinspection TypeAnnotation // Explicit type is not use to verify generated types
  val ddd6 = asPropValue(this, aaa)
  //noinspection TypeAnnotation // Explicit type is not use to verify generated types
  val ddd7 = asPropValue(this, optionProp)

