//noinspection ScalaUnnecessaryParentheses
package org.mvv.scala.fp

import scala.annotation.tailrec
import scala.compiletime.uninitialized
import scala.main


trait RNG :
  def nextInt: (Int, RNG)

object RNG :
  def simple(seed: Long): RNG = SimpleRNG(seed)

case class SimpleRNG (seed: Long) extends RNG :
  def nextInt: (Int, RNG) = {
    val newSeed = (seed * 0x5DEECE66DL + 0xBL) & 0xFFFFFFFFFFFFL
    val nextRNG = SimpleRNG(newSeed)
    val n = (newSeed >>> 16).toInt
    (n, nextRNG)
  }

case class LinerInts (seed: Int) extends RNG :
  def nextInt: (Int, RNG) = {
    val nextValue = seed + 1
    println(s"### LinerInts.nextInt => $nextValue")
    (nextValue, LinerInts(nextValue))
  }

  override def toString: String = s"LinerInts { seed = $seed }"



@main
def test567(): Unit = {
  val rndVal1: (Int, RNG) = RNG.simple(4434).nextInt
  val rndVal2: (Int, RNG) = rndVal1._2.nextInt
  val rndVal3: (Int, RNG) = rndVal2._2.nextInt

  println(s"rnds: ${rndVal1._1} ${rndVal2._1} ${rndVal3._1} ")
}


type Rand[+A] = RNG => (A, RNG)

def unit[A](a: A): Rand[A] = rng => (a, rng)

def map[A,B](s: Rand[A])(f: A => B): Rand[B] =
  rng => {
    val (a, rng2) = s(rng)
    (f(a), rng2)
  }


def nonNegativeInt: Rand[Int] = (rng: RNG) => {
  val v = rng.nextInt
  (v._1.abs, v._2)
}

def nonNegativeEven: Rand[Int] =
  map(nonNegativeInt)(i => i - i % 2)

def intDouble_01(rng: RNG): ((Int,Double), RNG) = {
  val v1 = rng.nextInt
  val v2 = v1._2.nextInt //.asInstanceOf[Double]

  //((v1._1, v2._1.asInstanceOf[Double]), v2._2)
  ((v1._1, v2._1.toDouble), v2._2)
}

def intDouble_02: Rand[(Int,Double)] = (rng: RNG) => {
  val v1 = rng.nextInt
  val v2 = v1._2.nextInt //.asInstanceOf[Double]

  //((v1._1, v2._1.asInstanceOf[Double]), v2._2)
  ((v1._1, v2._1.toDouble), v2._2)
}

/*
def intDouble_03: Rand[(Int,Double)] = (rng: RNG) => {
  val v1: String = map(rng)(_ => _)
  //val v2 = map(v1)(_.toDouble)
  //((v1._1, v2._1.toDouble), v2._2)
}
*/

def double01(rng: RNG): (Double, RNG) = {
  val v = rng.nextInt
  (v._1.toDouble, v._2)
}

def double02(rng: Rand[Int]): Rand[Double] = {
  map[Int, Double](rng)(_.toDouble)
}

def map2[A,B,C](ra: Rand[A], rb: Rand[B])(f: (A, B) => C): Rand[C] = {

  val cFunc: Rand[C] = (rng: RNG) => {

    val aa: (A, RNG) = ra(rng)
    val bb: (B, RNG) = rb(aa._2)

    val a: A = aa._1
    val b: B = bb._1
    val resRng: RNG = bb._2

    val c: C = f(a, b)
    (c, resRng)
  }
  cFunc
}

val intRand: Rand[Int] = (rng: RNG) => {
  val v: (Int, RNG) = rng.nextInt
  v
}

val doubleRand_0001: Rand[Double] = (rng: RNG) => {
  val v: (Int, RNG) = rng.nextInt
  (v._1.toDouble, v._2)
}

val doubleRand_0002: Rand[Double] = map[Int, Double](intRand)(_.toDouble)


def intDouble_03: Rand[(Int,Double)] =
  map2[Int, Double, (Int, Double)](intRand, doubleRand_0001)((intV, doubleV) => (intV, doubleV))



@main
def testAAA(): Unit = {

  println("testAAA()")
  //val LinerInts

  val aa = intDouble_03(LinerInts(0))
  println(s"$aa")

  val aa02 = intDouble_03(aa._2)
  println(s"$aa02")
}



def jhdfjk(): Unit = {

  //val int44: Rand[Int] = (g: Rand[Int]) => g.nextInt
  val int45: Rand[Int] = (g: RNG) => g.nextInt
  val int: Rand[Int] = _.nextInt // very very short form
}



def sequence[A](fs: List[Rand[A]]): Rand[List[A]] = {
  if fs.isEmpty then return (rng: RNG) => (Nil, rng)

  val cFunc: Rand[List[A]] = (rng: RNG) => {

    // TODO: bad unoptimized solution
    var res: List[A] = Nil
    var res2: (A, RNG) = fs.head.apply(rng) // (uninitialized, rng)
    var rng2: RNG = rng

    for (fss <- fs) {
      res2 = fss.apply(rng2)

      rng2 = res2._2
      res = res.appended(res2._1)
    }

    (res, rng2)
  }
  cFunc
}


@main
def testSequence(): Unit = {
  val randsPlain: List[Rand[Int]] = List[Rand[Int]](intRand, intRand, intRand)
  val plainResult = sequence[Int](randsPlain).apply(LinerInts(0))
  println(s"plainResult: $plainResult")

  val randsPower: List[Rand[Int]] = List[Rand[Int]](intRand, map(intRand)(v => v*v), map(intRand)(v => v*v*v))
  val powerResult = sequence[Int](randsPower).apply(LinerInts(0))
  println(s"powerResult: $powerResult")

  //assume()
  assert(plainResult._1 == List(1, 2, 3))
  assert(powerResult._1 == List(1, 2*2, 3*3*3))
}

// TODO: try to implement
def sequence2[A](fs: List[Rand[A]]): Rand[List[A]] = {
  if fs.isEmpty then return (rng: RNG) => (Nil, rng)

  val cFunc: Rand[List[A]] = (rng: RNG) => {

    // TODO: bad unoptimized solution
    var res: List[A] = Nil
    var res2: (A, RNG) = fs.head.apply(rng) // (uninitialized, rng)
    var rng2: RNG = rng

    for (fss <- fs) {
      res2 = fss.apply(rng2)

      rng2 = res2._2
      res = res.appended(res2._1)
    }

    (res, rng2)
  }
  cFunc
}


@main
def testSequence2(): Unit = {
  val randsPlain: List[Rand[Int]] = List[Rand[Int]](intRand, intRand, intRand)
  val plainResult = sequence2[Int](randsPlain).apply(LinerInts(0))
  println(s"plainResult: $plainResult")

  val randsPower: List[Rand[Int]] = List[Rand[Int]](intRand, map(intRand)(v => v*v), map(intRand)(v => v*v*v))
  val powerResult = sequence2[Int](randsPower).apply(LinerInts(0))
  println(s"powerResult: $powerResult")

  //assume()
  assert(plainResult._1 == List(1, 2, 3))
  assert(powerResult._1 == List(1, 2*2, 3*3*3))
}


// TODO: fix using tailrec
//@tailrec
def sum(xs: List[Int]): Int = xs match
  case Nil => 0
  case x :: tail => x + sum(tail)


// TODO: fix using tailrec
//@tailrec
def nonNegativeLessThan(n: Int): Rand[Int] = { rng =>
  val (i, rng2) = nonNegativeInt(rng)
  val mod = i % n
  if i + (n-1) - mod >= 0 then (mod, rng2)
  else nonNegativeLessThan(n)(rng)
}


//// TODO: impl
//def flatMap[A,B](f: Rand[A])(g: A => Rand[B]): Rand[B] = { rng =>
//  val aa: (A, RNG) = f(rng)
//  val bb: Rand[B] = g(aa._1)
//  //bb
//  //val newRng: RNG = bb._2
//  //val bVal: B = bb.
//  //(bVal, newRng)
//}

//noinspection ScalaUnnecessaryParentheses
// TODO: impl
def flatMap[A,B](f: Rand[A])(g: A => Rand[B]): Rand[B] = {
  val randB: (RNG => Rand[B]) = (rng: RNG) => {
    val aa: (A, RNG) =  f(rng)
    val bb: Rand[B] = g(aa._1)
    bb
  }

  (rng: RNG) => {
    val r: Rand[B] = randB(rng)
    r(rng)
  }
}

// TODO: impl
def flatMap2[A,B](f: Rand[A])(g: A => Rand[B]): Rand[B] = {
  val randB: (RNG => (B, RNG)) = (rng: RNG) => {
    val aa: (A, RNG) =  f(rng)
    val bb: Rand[B] = g(aa._1)
    bb(aa._2) // ??? !!! TODO: is it right to call it??
  }
  randB
}


@main
def testFlatMap(): Unit = {
  val f: Rand[String] = (rng: RNG) => {
    val v: (Int, RNG) = rng.nextInt
    (v._1.toString, v._2)
  }

  val g: (String => Rand[Double]) = (s: String) => {
    val randDouble: Rand[Double] = (rng: RNG) => {
      val rr: (Double, RNG) = (s.toDouble, rng)
      rr
    }
    randDouble
  }
  val g2: (String => Rand[Double]) = (s: String) => unit(s.toDouble)
  val g3: (String => Rand[Double]) = s => unit(s.toDouble)
  //val g4: (String => Rand[Double]) = unit(_.toDouble)

  val sss: Rand[Double] = flatMap[String, Double](f)(g3)
  val aa: (Double, RNG) = sss(LinerInts(0))

  println(s"aa: $aa")
}

// TODO: impl
def flatMap3[A,B](f: Rand[A])(g: A => Rand[B]): Rand[B] = {
  val randB: (RNG => (B, RNG)) = (rng: RNG) => {
    val aa: (A, RNG) =  f(rng)
    val bb: Rand[B] = g(aa._1)
    //bb(aa._2) // ??? !!! TODO: is it right??
    val dd: (B, RNG) = bb(aa._2)
    dd
  }
  randB
}

// TODO: impl
def flatttMap3[A,B,C](f: Rand[A])(g: A => Rand[B])(g2: B => Rand[C]): Rand[C] = {
  val randC: (RNG => (C, RNG)) = (rng: RNG) => {
    val aa: (A, RNG) =  f(rng)
    val bb: Rand[B] = g(aa._1)
    //bb(aa._2) // ??? !!! TODO: is it right??
    val dd: (B, RNG) = bb(aa._2)

    val cc: Rand[C] = g2(dd._1)
    val cc2: (C, RNG) = cc(dd._2)

    cc2
  }
  randC
}


//def nonNegativeLessThan22(n: Int): Rand[Int] = flatMap[Int,Int](nonNegativeInt)(???)

//def nonNegativeLessThan33(n: Int): Rand[Int] =
//  flatMap(nonNegativeInt) { i =>
//    val mod = i % n
//    if i + (n-1) - mod >= 0 then mod else nonNegativeLessThan33(n)//(???)
//  }

//def nonNegativeLessThan(n: Int): Rand[Int] =
//  map(nonNegativeInt) { i =>
//    val mod = i % n
//    if (i + (n-1) - mod >= 0) mod else nonNegativeLessThan(n)(???)
//  }
//


//def aaa(): Unit = {
//
//  val int: Rand[Int] = ???
//  val ints: (v: Any) => List[]
//
//  val ns: Rand[List[Int]] =
//    flatMap(int)(x =>
//      flatMap(int)(y =>
//        ints(x).map(xs =>
//          xs.map(_ % y))))
////
////  val ns: Rand[List[Int]] =
////    int.flatMap(x =>
////      int.flatMap(y =>
////        ints(x).map(xs =>
////          xs.map(_ % y))))
//}


/*
case class State[S,+A](run: S => (A,S))
//type State[S,+A] = S => (A,S)

extension [S,A](s: State[S,A])
  def flatMap[B](f: Rand[A])(g: A => Rand[B]): Rand[B] = {
    val randB: (RNG => (B, RNG)) = (rng: RNG) => {
      val aa: (A, RNG) = f(rng)
      val bb: Rand[B] = g(aa._1)
      bb(aa._2) // ??? !!! TODO: is it right to call it??
    }
    randB
  }


def get[S]: State[S, S] = State(s => (s, s))
//def get[S]: State[S, S] = ((s: S) => (s, s))
def set[S](s: S): State[S, Unit] = State(_ => ((), s))
//def set[S](s: S): State[S, Unit] = ((ignore: S) => ((), s))

def modify[S](f: S => S): State[S, Unit] = for {
  s <- get
  _ <- set(f(s))
} yield ()
*/

// TODO: to do exercise
// EXERCISE 6.11