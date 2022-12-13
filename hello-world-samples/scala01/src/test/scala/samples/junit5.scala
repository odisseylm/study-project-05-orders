package samples

//import org.junit.jupiter.api._
//import Assertions._
import org.junit.jupiter.api.*
import Assertions.*

//noinspection ScalaFileName
// To have it picked up by maven surefire you need to add pattern to configuration,
// by default only *Test/Test* are picked up.
class AppScalaTest_JUnit5 {

  @Test
  def testOK(): Unit = assertTrue(true)

  //    @Test
  //    def testKO() = assertTrue(false)
}

//noinspection ScalaFileName
class AppScalaJUnit5Test {
  @Test
  def testOK(): Unit = assertTrue(true)
}
