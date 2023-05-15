package org.mvv.scala.fp


sealed trait Stream[+A] :
  def isEmpty: Boolean
  def head: A
  def tail: Stream[A]

case object Empty extends Stream[Nothing] :
  def isEmpty: Boolean = true

  def head: Nothing = throw IllegalArgumentException("Stream is empty")
  def tail: Stream[Nothing] = this

case class Cons[+A](h: () => A, t: () => Stream[A]) extends Stream[A] :
  def isEmpty: Boolean = false
  def head: A = h()
  def tail: Stream[A] = t()


object Stream :
  def cons[A](hd: => A, tl: => Stream[A]): Stream[A] =
    lazy val head = hd
    lazy val tail = tl
    Cons(() => head, () => tail)

  def empty[A]: Stream[A] = Empty

  def apply[A](as: A*): Stream[A] =
    //if (as.isEmpty) empty else cons(as.head, apply(as.tail: _*))
    if as.isEmpty then empty else cons(as.head, apply(as.tail*))

  /*
  def headOption: Option[A] = this match
    case Empty => None
    case Cons(h, t) => Some(h()) // Explicit forcing of the h thunk using h()

  def exists(p: A => Boolean): Boolean = this match {
    case Cons(h, t) => p(h()) || t().exists(p)
    case _ => false
  }

  def foldRight[B](z: => B)(f: (A, => B) => B): B =
    this match
      case Cons(h,t) => f(h(), t().foldRight(z)(f))
      case _ => z
  */

end Stream


object StreamGenerator :
  def generator[A](initialValue: A, f: A=>A): Stream[A] =
    Stream.cons(initialValue, generator(f(initialValue), f))


@main
def testGenerator(): Unit = {

  val fIncr: (Int => Int) = (prev: Int) => {
    if (prev == 90) then
      Exception(s"fIncr($prev). Test exception to see stack trace.").printStackTrace()

    prev + 1
  }

  var g = StreamGenerator.generator[Int](0, fIncr) //(0, _ + 1)

  for _ <- 1 to 100 do
    println(s"### testGenerator ${g.head}")
    g = g.tail
}

