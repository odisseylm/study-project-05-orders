package com.mvv.scala.temp.tests.macros2

def aa(): Unit = {}


case class Rfvtgb (v: String)

class Bbbbbb :
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
  }

  val ddd3: BeanPropertyValue[Any, Any] = asBeanValue(aaa)

