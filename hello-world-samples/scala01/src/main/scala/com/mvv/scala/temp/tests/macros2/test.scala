package com.mvv.scala.temp.tests.macros2

def aa(): Unit = {}


class Bbbbbb :
  val aaa: String = "54646"

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
  }

  val ddd3: BeanPropertyValue[Any, Any] = asBeanValue(aaa)

