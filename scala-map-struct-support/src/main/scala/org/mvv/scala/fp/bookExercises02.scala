package org.mvv.scala.fp

import dotty.tools.dotc.reporting.ExpectedTokenButFound

import scala.annotation.{tailrec, unused}



// EXERCISE 2.1

def fib_byLoop(n: Int): Int =
  if n == 0 then return 0
  if n == 1 then return 1

  var fPrev = 0; var f = 1

  for i <- 2 to n do
    val fib = fPrev + f
    fPrev = f
    f = fib
  f


@main
def testFibByLoop(): Unit = {
  assert(fib_byLoop(0), 0)
  assert(fib_byLoop(1), 1)
  assert(fib_byLoop(2), 1)
  assert(fib_byLoop(3), 2)
  assert(fib_byLoop(4), 3)
  assert(fib_byLoop(5), 5)
  assert(fib_byLoop(6), 8)
}


/*
fun fibonacci(n: Int, a: BigInteger, b: BigInteger): BigInteger {
    return if (n == 0) b
    else fibonacci(n - 1, a + b, a)
}
*/

//@tailrec
def fib(n: Int): Int =
  if n == 0 then 0
  else if n == 1 then 1
  else
    // TODO: do it tailrec and avoid double calculation
    fib(n - 1) + fib(n - 2)

/*
def fib02(n: Int): Int =
  @tailrec
  def go(n: Int, fibN_2: Int, fibN_1: Int)
    if n == 0 then 0
    else if n == 1 then 1
    else
      // TODO: do it tailrec and avoid double calculation
      go(n - 1) + fib(n - 2)
*/

@main
def testFib(): Unit = {
  assert(fib(0), 0)
  assert(fib(1), 1)
  assert(fib(2), 1)
  assert(fib(3), 2)
  assert(fib(4), 3)
  assert(fib(5), 5)
  assert(fib(6), 8)
}


//@tailrec // strange scala behavior, tailrec does not work without separate 'accumulator' variable (see 'fact' function)
def factorial(n: Int): Int =
  if n == 0 || n == 1 then 1
  else n * factorial(n - 1)

//@tailrec
//def factorial2(n: Int): Int = n match
//  case 0 => 0
//  case 1 => 1
//  case _ => n * factorial2(n - 1)

//@tailrec
//def factorial22(n: Int, accumulator: Int): Int = n match
//  case 0 => 0
//  case 1 => 1
//  case _ => n * factorial22(n - 1)

def fact(n: Int): Int =
  @tailrec
  def go(n: Int, acc: Int): Int =
    if n < 0 then throw new Exception
    else if n == 0 || n == 1 then acc
    else go(n - 1, acc * n)
  go(n, 1)


@main
def testFactorial(): Unit = {
  assert(factorial(0), 1)
  assert(factorial(1), 1)
  assert(factorial(2), 2)
  assert(factorial(3), 6)
  assert(factorial(4), 24)
  assert(factorial(5), 120)
  assert(factorial(6), 720)
}


@main
def testFact(): Unit = {
  assert(fact(0), 1)
  assert(fact(1), 1)
  assert(fact(2), 2)
  assert(fact(3), 6)
  assert(fact(4), 24)
  assert(fact(5), 120)
  assert(fact(6), 720)
}


def assert[T](v: T, expected: T): Unit =
  //import scala.languageFeature.implicitConversions
  //import scala.language.strictEquality
  //given CanEqual[T, T] = CanEqual.derived
  @unused given CanEqual[T, T] = CanEqual.derived
  if v != expected then scala.runtime.Scala3RunTime.assertFailed(s"$v != $expected")



// EXERCISE 2.3

def curry001[A,B,C](f: (A, B) => C): A => (B => C) =
  val ff: A => (B => C) = (a: A) => {
    val fff: (B => C) = (b: B) => f(a, b)
    fff
  }
  ff

def curry[A,B,C](f: (A, B) => C): A => (B => C) =
  (a: A) => (b: B) => f(a, b)



// EXERCISE 2.4

def uncurry[A,B,C](f: A => B => C): (A, B) => C =
  val ff: ((A, B) => C) = (a: A, b: B) => f(a)(b)
  ff



// EXERCISE 2.5

def compose[A,B,C](f: B => C, g: A => B): A => C =
  val ff: (A => C) = (a: A) => f(g(a))
  ff



// EXERCISE 3.1

@main
def exercise31_00(): Unit =

  //@unused given CanEqual[Int, Any] = CanEqual.derived

  sealed trait List[+A]
  case object Nil extends List[Nothing]
  case class Cons[+A](head: A, tail: List[A]) extends List[A]

  @unused given CanEqual[Any, List[Int]] = CanEqual.derived

  def List_apply[A](as: A*): List[A] =
    if (as.isEmpty) Nil
    //else Cons(as.head, apply333(as.tail: _*))
    else Cons(as.head, List_apply(as.tail*))

  //def List_product(ds: List[Double]): Double = ds match {
  //  case Nil => 1.0
  //  case Cons(0.0, _) => 0.0
  //  case Cons(x, xs) => x * List_product(xs)
  //}

  def sum(ints: List[Int]): Int = ints match {
    case Nil => 0
    case Cons(x, xs) => x + sum(xs)
  }

  val x = List_apply(1, 2, 3, 4, 5) match {
    //case Cons[Int](x, Cons[Int](2, Cons[Int](4, _))) => x
    case Cons(x, Cons(2, Cons(4, _))) => x
    case Nil => 42
    //case Cons[Int](x, Cons[Int](y, Cons[Int](3, Cons[Int](4, _)))) => x + y
    case Cons(x, Cons(y, Cons(3, Cons(4, _)))) => x + y
    //case Cons[Int](h, t) => h + sum(t)
    case Cons(h, t) => h + sum(t)
    case _ => 101
  }
  println(s"EXERCISE 3.1 => x: $x")

@main
def exercise31(): Unit =
  val x = List(1, 2, 3, 4, 5) match {
    case x :: 2 :: 4 :: _ => x
    case Nil => 42
    case x :: y :: 3 :: 4 :: _ => x + y  // !!! ### result/answer is 3
    case h :: t => h + sum(t)
    case _ => 101
  }
  println(s"EXERCISE 3.1 => x: $x")



// EXERCISE 3.6

@main
def exercise36(): Unit = {

  sealed trait List[+A]
  case object Nil extends List[Nothing]
  case class Cons[+A](head: A, tail: List[A]) extends List[A]

  @unused given CanEqual[Any, List[Any]] = CanEqual.derived

  def List_apply[A](as: A*): List[A] =
    if (as.isEmpty) Nil
    //else Cons(as.head, apply333(as.tail: _*))
    else Cons(as.head, List_apply(as.tail *))

  //@tailrec // TODO: fix tailrec
  def init[A](l: List[A]): List[A] = l match
    case Nil => Nil
    case Cons(_, Nil) => Nil
    case Cons(x, Cons(_, Nil)) => Cons(x, Nil)
    case Cons(x, y) => Cons(x, init(y))

  /*
  //@tailrec // TODO: fix tailrec
  def init2[A](l: List[A]): List[A] =
    def go(l: List[A], returnTopValue: List[A]): List[A] =
      l match
        case Nil => returnTopValue
        case Cons(_, Nil) => Nil
        //case Cons(x, Cons(_, Nil)) => Cons(x, Nil)
        case Cons(x, y) => Cons(x, init(y))
  */

  assert(init(List_apply(1, 2, 3, 4)), List_apply(1, 2, 3))
}


// TODO: impl
// EXERCISE 3.16

// TODO: impl
// EXERCISE 3.18

// TODO: impl
// EXERCISE 3.19

// TODO: impl
// EXERCISE 3.20

// TODO: impl
// EXERCISE 3.21

// TODO: impl
// EXERCISE 3.22

// TODO: impl
// EXERCISE 3.23

// TODO: impl
// EXERCISE 3.24  HARD task




// Trees
// EXERCISE 3.25
// ...
// EXERCISE 3.29



// Option
// EXERCISE 4.1


// ??? EXERCISE 4.2 (p55)

// EXERCISE 4.3

// EXERCISE 4.4
// ...
// EXERCISE 4.8





