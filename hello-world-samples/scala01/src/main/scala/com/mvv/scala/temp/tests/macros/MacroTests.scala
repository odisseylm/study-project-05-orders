package com.mvv.scala.temp.tests.macros

import scala.annotation.{nowarn, unused}
import scala.quoted.Expr


trait NullableCanEqualGivens[Type] :
  given givenCanEqual_Type_Type: CanEqual[Type, Type] = CanEqual.derived
  given givenCanEqual_Type_Null: CanEqual[Type, Null] = CanEqual.derived
  given givenCanEqual_TypeNull_Null: CanEqual[Type|Null, Null] = CanEqual.derived
  given givenCanEqual_TypeNull_Type: CanEqual[Type|Null, Type] = CanEqual.derived
  given givenCanEqual_Null_Type: CanEqual[Null, Type] = CanEqual.derived
  given givenCanEqual_Null_TypeNull: CanEqual[Null, Type|Null] = CanEqual.derived
  given givenCanEqual_Type_TypeNull: CanEqual[Type, Type|Null] = CanEqual.derived


class Type1 derives CanEqual
object Type1 extends NullableCanEqualGivens[Type1]


class Type2
object Type2 extends NullableCanEqualGivens[Type2]


/*
object Type1 :
  // It should be generated by macro
  given givenCanEqual_Type1_Null: CanEqual[Type1, Null] = CanEqual.derived
  given givenCanEqual_Type1Null_Null: CanEqual[Type1|Null, Null] = CanEqual.derived
  given givenCanEqual_Type1Null_Type1: CanEqual[Type1|Null, Type1] = CanEqual.derived
  given givenCanEqual_Null_Type1: CanEqual[Null, Type1] = CanEqual.derived
  given givenCanEqual_Null_Type1Null: CanEqual[Null, Type1|Null] = CanEqual.derived
  given givenCanEqual_Type1_Type1Null: CanEqual[Type1, Type1|Null] = CanEqual.derived

  //inspect {
  //  val v22 = scala.util.Random().nextInt()
  //  val v23 = v22 == 567
  //  println(s"v22 [$v22] == 567 => $v23")
  //
  //  given givenCanEqual_788787: CanEqual[Type1, Type1|Null] = CanEqual.derived
  //}



object Type1_33 :
  // It should be generated by macro
  given givenCanEqual_Type1_Null: CanEqual[Type1, Null] = CanEqual.derived
  given givenCanEqual_Type1Null_Null: CanEqual[Type1|Null, Null] = CanEqual.derived
*/


//noinspection DfaConstantConditions, ScalaUnusedSymbol
def testNullsEqualityMacro_testCompilation(): Unit = {

  val t1: Type1 = Type1()
  val t2: Type1|Null = Type1()

  if (t1 == null) println("1")
  if (t2 == null) println("1")
  if (null == t1) println("1")
  if (null == t2) println("1")

  if (t1 == t1) println("1")
  if (t1 == t2) println("1")
  if (t2 == t1) println("1")
  if (t2 == t2) println("1")
}
//noinspection DfaConstantConditions, ScalaUnusedSymbol
def testNullsEqualityMacro2_testCompilation(): Unit = {

  val t1: Type2 = Type2()
  val t2: Type2|Null = Type2()

  if (t1 == null) println("1")
  if (t2 == null) println("1")
  if (null == t1) println("1")
  if (null == t2) println("1")

  if (t1 == t1) println("1")
  if (t1 == t2) println("1")
  if (t2 == t1) println("1")
  if (t2 == t2) println("1")
}


//noinspection ScalaUnusedSymbol
def aaa(): Unit = {

  //------------------------------------------------
  val v_test = 1; println(v_test)

  val v1 =  test(false, scala.util.Random().nextInt() == 345)
  //newCode234()

  //println(printHello.show) // print("Hello")

//  import scala.quoted.* // imports Quotes, Expr
//  val msg = Expr("Hello")
//  val printHello = '{ print($msg) }
//  println(printHello.show) // print("Hello")

  //------------------------------------------------
  val v_generateLocalCode1 = 1; println(v_generateLocalCode1)

  // injecting some generated code (see generateLocalCode1 impl)
  generateLocalCode1("Vovan")

  //------------------------------------------------
  val v_inspect = 1; println(v_inspect)

  // injecting just this code (local code)
  inspect {
    val v22 = scala.util.Random().nextInt()
    val v23 = v22 == 567
    println(s"v22 [$v22] == 567 => $v23")
  }

  //------------------------------------------------
  val v_myMacro = 1; println(v_myMacro)

  // result => ' val ddd = true ' ???
  val ddd = myMacro("myMacro")


  //------------------------------------------------
  val v_printTree = 1; println(v_printTree)

  // usage
  PrintTree.printTree { (s: String) => s.length }


  //------------------------------------------------
  val v_test555 = 1; println(v_test555)

  // example usage - should use the: given MyTypeclass[String], defined above
  //test555[String]

  //------------------------------------------------
  val v_assert22 = 1; println(v_assert22)


  assert22("1" == "2")
}

// compilation error
//generateLocalCode1("Global Vovan")

class Aaaaaaa
object Aaaaaaa :
  val str: String = "789"
  //val v1: Int = addOneXv2(345)
  addOneXv2(445)
  //addOneXv2 { eee: Int = 345; 345 }
  //generateLocalCode2 { val aa1 = "fdfdf"; val aa2 = "tttt" }


//object Aaaaaaa2 :
//  @unused @nowarn val sdf: Any = generateLocalCode3()


class A

@main
def test897438759(): Unit = {
  val a: A = logAST { new A }
  println(a)
}

//object Fuck234 :
//  //' { val rrr: String = "678"  }
//  generateLocalCode3()

