package samples

import org.junit.jupiter.api.Test

import scala.language.strictEquality	//Enables Multiversal-Equality


case class Circle(radius: Float) derives CanEqual
case class Square(side: Float) derives CanEqual

val circle1 = Circle(5)
val circle2 = Circle(5)
val square1 = Square(5)

class Test43434 {

  @Test
  def test22(): Unit = {

    println(circle1 == circle2) //No compilation errors & prints true.

    // compilation error
    //println(circle1 == square1) //No compilation errors & prints true.
  }
}
