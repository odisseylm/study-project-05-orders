package samples

import com.mvv.scala.temp.tests.{Num, UnOp, second1, second2, second3, simplifyTop}
import org.junit.jupiter.api.Test


class MatchTest {

  @Test
  def test1(): Unit = {
    val expr0 = Num(123)
    val expr = simplifyTop(expr0)

    println(s"Result: $expr")
  }

  @Test
  def test2(): Unit = {
    val expr0 = UnOp("-", Num(123))
    val expr = simplifyTop(expr0)

    println(s"Result: $expr")
  }

  @Test
  def test3(): Unit = {
    val expr0 = UnOp("-", UnOp("-", Num(123)))
    val expr = simplifyTop(expr0)

    println(s"Result: $expr")
  }

  @Test
  def testMatches(): Unit = {
    val l = List(5, 6, 7)
    val v1 = second1(l)
    val v2 = second2(l)
    val v3 = second3(l)

    println(s"v1: $v1, v2: $v2, v3: $v3")
  }

}
