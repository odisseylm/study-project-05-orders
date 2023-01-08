package com.mvv.scala.temp.tests

import com.mvv.scala.temp.tests.IntExtensions.{***, +++, toStr2}

import java.util.concurrent.atomic.AtomicInteger
import scala.annotation.{tailrec, targetName}
import scala.util.Random

//noinspection ScalaUnusedSymbol

//class Class1 //:
//  def aa() = 42

//noinspection ScalaUnusedSymbol,VarCouldBeVal,ScalaWeakerAccess
class Class2 :
  def aa() = 42

//noinspection ScalaUnusedSymbol,VarCouldBeVal,ScalaWeakerAccess
class Class3 :
  def aa() = 42

//noinspection ScalaUnusedSymbol,VarCouldBeVal,ScalaWeakerAccess
class Class4 :
  def aa() = 42
end Class4

//noinspection ScalaUnusedSymbol,VarCouldBeVal,ScalaWeakerAccess
class ClassJavaStyle {
  def aa() = 42
}


//noinspection ScalaUnusedSymbol,VarCouldBeVal,ScalaWeakerAccess,ScalaUnusedExpression
class Class5 :
  def aa(): Int =
    while true do
      val v1 = 123
        val v2 = 124
          125
    126
  //noinspection RemoveRedundantReturn
  def bb(): Int =
    while true do
      val v1 = 123
        val v2 = 124
          125
    return 126

//noinspection ScalaUnusedSymbol,VarCouldBeVal,ScalaWeakerAccess,AccessorLikeMethodIsEmptyParen
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

//noinspection ScalaUnusedSymbol,VarCouldBeVal,ScalaWeakerAccess
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

//noinspection ScalaUnusedSymbol
class Rational(n: Int) :
  //val n: Int = n
  var nn: Int = n

  def this(v: String) =
    //println("gfgf")
    //val ss = 3
    this(v.toInt)

  override def toString: String = s"n: $n"
  def add(v: Rational): Rational = Rational(v.nn + this.n)
  @targetName("lemon")
  def ::(v: Rational): Rational = Rational(v.nn + this.n)
  @targetName("cherry")
  def :->(v: Rational): Rational = Rational(v.nn + this.n)
  @targetName("orange")
  def `<?>`(v: Rational): Rational = Rational(v.nn + this.n)
  @targetName("apple")
  def ?(v: Rational): Rational = Rational(v.nn + this.n)

//noinspection ScalaUnusedSymbol,VarCouldBeVal,ScalaWeakerAccess
// companion object
object Rational :
  extension (v: Rational) // what is benefit ???
    def add4(v2: Rational): Rational = Rational(v.nn + v2.nn)


extension (v: Rational)
  @targetName("append22")
  def add2(v2: Rational): Rational = v.add(v2)
  def add3(v2: Rational): Rational = v add v2



@main
//noinspection ScalaUnusedSymbol,VarCouldBeVal,ScalaWeakerAccess
def main2(): Unit = {

  usageOfLazy()

  if (true) return

  usageOfDogTraits()

  if (true) return

  usageOfBlackCatTraits()

  if (true) return

  usageChild1()

  if (true) return

  val get3 = ()=> { println("get3"); 3 }

  byNameAssert(get3() > 2)

  if (true) {
    return
  }


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

val f = (_: Int) + (_: Int)

//noinspection ScalaUnusedSymbol,VarCouldBeVal,ScalaWeakerAccess
def byNameAssert(predicate: => Boolean): Unit = {
  println(" 555 1")
  val res = predicate
  val res2 = predicate
  val res3 = predicate
  val res4 = predicate
  println(s" 555 2: $res")
}

@tailrec
def whileLoop(condition: => Boolean)(body: => Unit): Unit =
  if condition then
    body
    whileLoop(condition)(body)

//noinspection ScalaUnusedSymbol,VarCouldBeVal,ScalaWeakerAccess
def aaa(): Unit = {
  var i = 2

  whileLoop(i > 0) {
    println(i)
    i -= 1
  }
}



//noinspection ScalaUnusedSymbol,VarCouldBeVal,ScalaWeakerAccess
class Parent1 :
  var var0 = 100
  def func0: Int = var0

trait MyTrait1 :
  var var1 = 101
  def func1: Int = var1

//noinspection ScalaUnusedSymbol,VarCouldBeVal
trait MyTrait2 :
  private var var2 = 102
  def func2: Int = var2

//noinspection ScalaUnusedSymbol,VarCouldBeVal
trait MyTrait3 :
  private var var1 = 10100
  private var var2 = 10200
  def func1: Int = var1
  def func2: Int = var2


//noinspection ScalaUnusedSymbol,VarCouldBeVal,ScalaWeakerAccess
class Child1 extends Parent1, MyTrait1, MyTrait2, MyTrait3 :
  //override var var1 = 101
  //override var var2 = 102
  var var3 = 110
  //override def func1 = MyTrait3.this.var1
  override def func1: Int = super[MyTrait3].func1
  override def func2: Int = super[MyTrait2].func2
  def func3: Int = var3

/*
def printChild1(obj: Child1): Unit = {
  println(s"obj.var0: ${obj.var0}")
  println(s"obj.var1: ${obj.var1}")
  println(s"obj.var2: ${obj.var2}")
  println(s"obj.var3: ${obj.var3}")

  println(s"obj.func0: ${obj.func0}")
  println(s"obj.func1: ${obj.func1}")
  println(s"obj.func2: ${obj.func2}")
  println(s"obj.func3: ${obj.func3}")
}
*/
def usageChild1(): Unit = {
  val obj1: Child1 = Child1()
  println(s"obj1.var1: ${obj1.var1}")

  obj1.var1 = 10001
  println(s"obj1.var1: ${obj1.var1}")


  val obj2: Child1 = Child1()
  println(s"obj2.var1: ${obj2.var1}")

  obj2.var1 = 10002
  println(s"obj2.var1: ${obj2.var1}")

  println(s"obj1.var1: ${obj1.var1}")
}



val ii = AtomicInteger()
//var ii: Int = 0


abstract class Animal :
  def name(): String

class Cat extends Animal :
  override def name(): String = {
    val i = ii.incrementAndGet()
    println("Cat.name()")
    s"( Cat t=$i)"
  }

class BlackCat extends Cat :
  override def name(): String = {
    val i = ii.incrementAndGet()
    println("BlackCat.name()")
    s"( Black ${super.name()} t=$i)"
  }

trait WithName1 extends Animal :
  abstract override def name(): String = {
    val i = ii.incrementAndGet()
    println("WithName1.name()")
    s"( ${super.name()} +  <= 1 t=$i)"
  }

trait WithName2 extends Animal :
  abstract override def name(): String = {
    val i = ii.incrementAndGet()
    println("WithName2.name()")
    s"( ${super.name()} +  <= 2 t=$i)"
  }

trait SkipName extends Animal :
  abstract override def name(): String = {
    val i = ii.incrementAndGet()
    println("SkipName.name()")
    s"( SKIPPED t=$i)"
  }

class Animal22 extends BlackCat with WithName1 with WithName2

def usageOfBlackCatTraits(): Unit = {
  println("---------------------------------------------")

  val obj1 = new BlackCat with WithName1 with WithName2 with SkipName ()
  println(s"name1: ${obj1.name()}")

  val obj2 = new Animal22()
  println(s"name2: ${obj2.name()}")

  println("---------------------------------------------")
}


/*
abstract class IntQueue :
  def get(): Int
  def put(x: Int): Unit

trait Doubling extends IntQueue :
  abstract override def put(x: Int): Unit = super.put(2 * x)

*/


//abstract class Interface1 :
//  def name1: String
//
//abstract class Interface2 :
//  def name2: String
//
//interface Interface3 :
//  def name2: String
//
//class MyClass22 extends Interface1, Interface2


abstract class Animal23 :
  def name: String

trait Furry extends Animal23 :
  abstract override def name: String = {
    println("Furry")
    s"Furry ( ${super.name} )"
  }

trait HasLegs extends Animal23 :
  abstract override def name: String = {
    println("HasLegs")
    s"HasLegs ( ${super.name} )"
  }

trait FourLegged extends HasLegs :
  abstract override def name: String = {
    println("FourLegged")
    s"FourLegged ( ${super.name} )"
  }

class Dog1 extends Animal23 :
  override def name: String = {
    println("Dog1")
    "Dog1"
  }

class Dog3 extends Dog1 with Furry with FourLegged

//noinspection ScalaUnusedSymbol,VarCouldBeVal,ScalaWeakerAccess
class Dog4 extends Dog1 with Furry with FourLegged :
  // !!! in this case 'name' from traits is ignored/overridden !!!
  override def name: String = {
    println("Dog3")
    "Dog3"
    //s"Dog ( ${super.name} )"
  }



def usageOfDogTraits(): Unit = {
  println("---------------------------------------------")
  new Dog1().name
  println("---------------------------------------------")
  new Dog1 with Furry with FourLegged ().name
  //println("---------------------------------------------")
  //new Dog2().name
  println("---------------------------------------------")
  new Dog3().name
  //new Dog2 with Furry with FourLegged ().name
  println("---------------------------------------------")
}


class LazyClass :
  println("LazyClass creation")

  lazy val lazyProp: String = {
    println("calculating lazy value only ONCE")
    "lazy value"
  }

def usageOfLazy(): Unit = {
  println("Before creating instance with lazy field")
  val obj = LazyClass()
  println("Instance with lazy field is created")
  println(obj.lazyProp)
  println(obj.lazyProp)
  println(obj.lazyProp)
}


//noinspection ScalaUnusedSymbol,VarCouldBeVal,ScalaWeakerAccess
trait Drinker :
  def firstName: String
  def lastName: String


//noinspection ScalaUnusedSymbol,VarCouldBeVal
trait Drink :
  def baseSubstation: String
  def flavour: String
  def description: String


//noinspection ScalaUnusedSymbol,VarCouldBeVal
trait VanillaFlavour {
  //this: Drink =>
  //thisFlavour55: Drink =>
  //drinkRef: Drink =>
  //drinkerRef: Drinker =>
  //this: Drink =>
  //this: Drinker =>
  self: Drink with Drinker =>

  //def flavour = "vanilla"
  override def flavour = "vanilla"
  override def description: String = s"Vanilla $baseSubstation"

  def newF1: String = { s"newF1 ${self.flavour}" }
  def newF2: String = { s"newF2 ${this.flavour}" }
}

//noinspection ScalaUnusedSymbol,VarCouldBeVal
def f2(x:Int)(y:Int) = x + y
