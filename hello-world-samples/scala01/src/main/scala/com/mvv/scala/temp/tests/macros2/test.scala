package com.mvv.scala.temp.tests.macros2

import com.mvv.scala.macros.asPropValue

def aa(): Unit = {}


case class Rfvtgb (v: String)

def localVal: String = "gjhgjhgjh3333"
def localFunction1: String = "gjhgjhgjh"
def localFunction2(): String = "gjhgjhgjh"

class Bbbbbb :
  val aaaStr: String = "str54646"
  val aaa: Rfvtgb = Rfvtgb("54646")
  def bbb1: Rfvtgb = Rfvtgb("54646")
  def bbb2(): Rfvtgb = Rfvtgb("54646")

  def test1(): Unit = {
    val ccc = asBeanValue(aaa)
    println(s"bean value: $ccc")

    val ccc2: BeanPropertyValue[Any, Any] = asBeanValue(aaa) //.asInstanceOf[String, Any]
    val str2: String = ccc2.value.get.asInstanceOf[String]

    val ccc3 = asBeanValue(aaa) //.asInstanceOf[Int, Any]
    val int3: Int = ccc3.value.get.asInstanceOf[Int]

    //val jhdgfhdg = Bbbbbb.this.aaa

    inspect( aaa)
    //inspect(s"asdf: $aaa")

    val er1 = asBeanValue2(67890)
    val er2 = asBeanValue2(aaa)
    val er3 = asBeanValue2(bbb1)
    // val er3_ = asBeanValue2(bbb1()) /// <=== Good! Compilation error
    // val er4 = asBeanValue2(bbb2)    /// <=== Good! Compilation error
    val er4_ = asBeanValue2(bbb2())

    val sss1_0 = asBeanValue3(aaaStr)
    val sss1_1 = asBeanValue3(aaa)
    val sss1_2 = asBeanValue3(bbb1)
    val sss1_3 = asBeanValue3(bbb2())

    val sss2_0 = asPropValue(aaaStr)
    val sss2_1 = asPropValue(aaa)
    val sss2_2 = asPropValue(bbb1)
    val sss2_3 = asPropValue(bbb2())

    val sss3_1 = asPropValue(localVal)
    val sss3_2 = asPropValue(localFunction1)
    val sss3_3 = asPropValue(localFunction2())


    println(s" $sss2_0 $sss2_1 $sss2_2 $sss2_3 ")
  }

  val ddd3: BeanPropertyValue[Any, Any] = asBeanValue(aaa)

