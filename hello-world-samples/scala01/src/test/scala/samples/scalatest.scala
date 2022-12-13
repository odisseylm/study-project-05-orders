package samples


/*
ScalaTest facilitates different styles of testing by providing traits you can mix
together to get the behavior and syntax you prefer.  A few examples are
included here.  For more information, visit:

https://www.scalatest.org/
https://www.scalatest.org/user_guide/selecting_a_style

One way to use ScalaTest is to help make JUnit or TestNG tests more
clear and concise. Here's an example:
*/
import scala.collection.*
import org.scalatest.{Assertions, flatspec, matchers}
import org.junit.Test
import org.specs2.matcher.Matchers.beEmpty

class StackSuite extends Assertions {

  @Test def stackShouldPopValuesIinLastInFirstOutOrder() = {
    val stack = new mutable.Stack[Int]
    stack.push(1)
    stack.push(2)
    assert(stack.pop() === 2)
    assert(stack.pop() === 1)
  }

  @Test def stackShouldThrowRuntimeExceptionIfAnEmptyArrayStackIsPopped() = {
    val emptyStack = new mutable.Stack[String]
    intercept[RuntimeException] {
      emptyStack.pop()
    }
  }
}

/*
Here's an example of a FunSuite with Matchers mixed in:
*/

import org.junit.runner.RunWith
import org.scalatest.funsuite.AnyFunSuite
import flatspec._
import matchers._
import scala.Symbol
import should.Matchers._

//@RunWith(classOf[JUnitRunner])
class ListSuite extends AnyFunSuite /*with should.Matchers*/ {

  test("An empty list should be empty") {
    //List() should be ('empty)
    //Nil should be ('empty)
    List() should be (empty)
    List() should have size 0
    Nil should be (empty)
  }

  test("A non-empty list should not be empty") {
    List(1, 2, 3) should not be empty
    List("fee", "fie", "foe", "fum") should not be empty
  }

  test("A list's length should equal the number of elements it contains") {
    List() should have length 0
    List(1, 2) should have length 2
    List("fee", "fie", "foe", "fum") should have length 4
  }
}

/*
ScalaTest also supports the behavior-driven development style, in which you
combine tests with text that specifies the behavior being tested. Here's
an example whose text output when run looks like:

A Map
- should only contain keys and values that were added to it
- should report its size as the number of key/value pairs it contains
*/
//import org.scalatest.FunSpec
import org.scalatest.funspec.AnyFunSpec

class ExampleSpec22 extends AnyFunSpec {

  describe("An ArrayStack") {

    it("should pop values in last-in-first-out order") {
      val stack = new mutable.Stack[Int]
      stack.push(1)
      stack.push(2)
      assert(stack.pop() === 2)
      assert(stack.pop() === 1)
    }

    it("should throw RuntimeException if an empty array stack is popped") {
      val emptyStack = new mutable.Stack[Int]
      intercept[RuntimeException] {
        emptyStack.pop()
      }
    }
  }
}


import collection.mutable.Stack
import org.scalatest.*
import flatspec.*
import matchers.*

import scala.collection.mutable

class ExampleSpec23 extends AnyFlatSpec with org.scalatest.matchers.should.Matchers {

  "A Stack" should "pop values in last-in-first-out order" in {
    val stack = new mutable.Stack[Int]
    stack.push(1)
    stack.push(2)
    stack.pop() should be (2)
    stack.pop() should be (1)
  }

  it should "throw NoSuchElementException if an empty stack is popped" in {
    val emptyStack = new mutable.Stack[Int]
    a [NoSuchElementException] should be thrownBy {
      emptyStack.pop()
    }
  }
}
