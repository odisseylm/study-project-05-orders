package com.mvv.scala.temp.tests

import com.mvv.scala.temp.tests.IntExtensions.{***, +++, toStr2}

import scala.annotation.targetName
import scala.util.Random


//class Class1 //:
//  def aa() = 42

class Class2 :
  def aa() = 42

class Class3 :
  def aa() = 42

class Class4 :
  def aa() = 42
end Class4

class ClassJavaStyle {
  def aa() = 42
}


class Class5 :
  def aa(): Int =
    while true do
      val v1 = 123
        val v2 = 124
          125
    126
  def bb(): Int =
    while true do
      val v1 = 123
        val v2 = 124
          125
    return 126

class Ccc :
  def mWithSideEffect(): Unit = {
    println("something")
  }
  private var _value = 40
  //def getValue(): Int = _value
  def value: Int = _value
  def getValue: Int = _value
  def getValue2(): Int = _value // ??!! What the difference ??
  def aaa: Int = _value
  def setValue(v: Int): Unit = { this._value = v }
  def setValueWithLazy(v: => Int): Unit = { this._value = v }
  def setValueWithDefault(v: Int = 43): Unit = { this._value = v }
  def value(v: Int): Unit = { this._value = v }


class Employee(private var _name: String) {
  def name: String = _name
  def nameToUpperCase: String = _name.toUpperCase
  def name_=(aName: String): Unit = { this._name = aName }
  //def name4_= (name: String) = { this._name = name }
}

class Employee22(private var name_ : String) {
}

object IntExtensions {
  extension (v: Int) {
    @targetName("append22")
    def +++(v2: Int): Int = v + v2
    @targetName("multiply22")
    def ***(v2: Int): Int = v * v2

    def toStr2: String = s"toStr2 $v"
    //def toStr2(): String = s"toStr2 $v"
  }
}

class Rational (n: Int) :
  //val n: Int = n
  var nn: Int = n

  def this(v: String) =
    //println("gfgf")
    //val ss = 3
    this(v.toInt)

  override def toString: String = s"n: $n"
  def add(v: Rational): Rational = Rational(v.nn + this.n)
  def ::(v: Rational): Rational = Rational(v.nn + this.n)
  def :->(v: Rational): Rational = Rational(v.nn + this.n)
  def `<?>`(v: Rational): Rational = Rational(v.nn + this.n)
  def ?(v: Rational): Rational = Rational(v.nn + this.n)

// companion object
object Rational :
  extension (v: Rational) // what is benefit ???
    def add4(v2: Rational): Rational = Rational(v.nn + v2.nn)


extension (v: Rational)
  @targetName("append22")
  def add2(v2: Rational): Rational = v.add(v2)
  def add3(v2: Rational): Rational = v add v2



@main
def m2(): Unit = {
  val r1 = Rational(22)
  println(s"r: $r1")

  val r2 = Rational(23)

  val r3 = r1.add(r2)
  val r4 = r1 add r2

  val r5 = r1 add2 r2
  val r6 = r1 add3 r2

  val r7 = r1 add4 r2

  // ??!! both are good ???
  // r.toString
  // r.toString()

  if (true)
    return


  val obj = new Ccc()
  obj.mWithSideEffect()

  val a = 1; val b = 2; val c=3

  println(a + b * c)
  println(a +++ b *** c)
  //println(IntExtensions.append22(a, b))
  println(a.toStr2)

  println("--------------------------------------------------------")
  println(obj.value)
  println(obj.getValue)
  println(obj.getValue2())
  //new Ccc().value       ' compilation error

  obj.setValue(2)
  obj.value(2)
  //obj.value = 2         ' compilation error
  //new Ccc().setValue 2  ' compilation error
  //new Ccc().value 2     ' compilation error

  obj.setValue(v = 2)

  println("--------------------------------------------------------")

  {
    val employee = new Employee("John")
    employee.name = "Ivan"
  }
  {
    val employee = new Employee(_name = "John")
    employee.name = "Ivan"
    employee.nameToUpperCase
  }

//  var aa = 22
//  do {
//    aa = Random.nextInt()
//  } while (aa == 22)

  while {
    val aa = Random.nextInt()
    aa == 22
  } do()

}
