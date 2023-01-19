package samples

import org.junit.*
import Assert.*
import org.scalatest.Assertions.intercept

import scala.collection.mutable
import scala.collection.mutable.ListBuffer


//noinspection ScalaFileName
// To have it picked up by maven surefire you need to add pattern to configuration,
// by default only *Test/Test* are picked up.
class AppScalaTest_JUnit4 {

  @Test
  def testOK(): Unit = { assertTrue(true) }

  //@Test
  //def testKO() = assertTrue(false)

  @Test
  def verifyEasy(): Unit = { // Uses ScalaTest assertions
    val sb = new mutable.StringBuilder("ScalaTest is ")
    val lb: ListBuffer[String] = new ListBuffer[String]

    sb.append("easy!")
    //assert(sb.toString === "ScalaTest is easy!") TODO: use ===
    assert(sb.toString == "ScalaTest is easy!")
    assert(lb.isEmpty)
    lb += "sweet"
    intercept[StringIndexOutOfBoundsException] {
      "concise".charAt(1111111)
    }
  }
}

class AppScalaJUnit4Test {
  @Test
  def testOK(): Unit = { assertTrue(true) }
}
