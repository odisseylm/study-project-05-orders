package org.mvv.scala.fp


// EXERCISE 5.1

object Stream33 :
  def toList[A]: List[A] = ???


// EXERCISE 5.2
// EXERCISE 5.3

// EXERCISE 5.7

// EXERCISE 5.10
// Write a function fibs that generates the infinite stream of Fibonacci numbers: 0, 1, 1, 2, 3, 5, 8, and so on.


// EXERCISE 5.11
// Write a more general stream-building function called unfold . It takes an initial state,
// and a function for producing both the next state and the next value in the generated stream.
//
// def unfold[A, S](z: S)(f: S => Option[(A, S)]): Stream[A]



// EXERCISE 5.13
// Use unfold to implement map , take , takeWhile , zipWith (as in chapter 3), and zipAll.
// The zipAll function should continue the traversal as long as either stream
// has more elements—it uses Option to indicate whether each stream has been exhausted.
//
// def zipAll[B](s2: Stream[B]): Stream[(Option[A],Option[B])]


// EXERCISE 5.14
//Hard: Implement startsWith using functions you’ve written. It should check if one
//Stream is a prefix of another. For instance, Stream(1,2,3) startsWith Stream(1,2)
//would be true .
//def startsWith[A](s: Stream[A]): Boolean


// EXERCISE 5.15
//Implement tails using unfold . For a given Stream , tails returns the Stream of suf-
//fixes of the input sequence, starting with the original Stream . For example, given
//Stream(1,2,3) , it would return Stream(Stream(1,2,3), Stream(2,3), Stream(3),
//Stream()) .
//def tails: Stream[Stream[A]]




// EXERCISE 5.16
//Hard: Generalize tails to the function scanRight , which is like a foldRight that
//returns a stream of the intermediate results. For example:
//scala> Stream(1,2,3).scanRight(0)(_ + _).toList
//res0: List[Int] = List(6,5,3,0)
//This example should be equivalent to the expression List(1+2+3+0, 2+3+0, 3+0,
//0) . Your function should reuse intermediate results so that traversing a Stream with n
//elements always takes time linear in n . Can it be implemented using unfold ? How, or
//why not? Could it be implemented using another function we’ve written?




