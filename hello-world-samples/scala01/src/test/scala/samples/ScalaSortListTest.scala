package samples

import scala.language.unsafeNulls

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.{Disabled, Test}

import scala.util.Random

import scala.jdk.CollectionConverters.*


class ScalaSortListTest {

  @Test
  @Disabled // for manual running
  def testSorting(): Unit = {
    val rnd = new java.util.Random

    val start33 = System.nanoTime
    val intValues: List[Int] = Seq
      .range(0, 10_000_000)
      .map(_ => rnd.nextInt)
      .toList
    val end33 = System.nanoTime

    val start = System.nanoTime
    val sorted = intValues.sorted
    val end = System.nanoTime

    println(s"sort time: ${(end - start) / 1000_000}")
    println(s"scala preparing ${(end33 - start33)/1000_000}")
  }

  @Test
  def foldLeft(): Unit = {
    val rnd = new java.util.Random

    val strResult: String = Seq
      .range(0, 10)
      .map(_ => rnd.nextInt)
      .foldLeft(StringBuilder())((str, v) => str.append(v))
      .toString

    println(strResult)
  }

  @Test
  //noinspection SimplifiableFoldOrReduce
  def fold(): Unit = {
    val rnd = new java.util.Random

    val strResult: Int = Seq
      .range(0, 10)
      .map(_ => rnd.nextInt)
      .fold(0)((v1, v2) => v1 + v2)
      //.fold(0)(_ + _)

    println(strResult)
  }

  @Test
  def knownSizeOfFiltered(): Unit = {
    val res: Int = Seq
      .range(0, 10)
      .filter(_ == 4)
      .knownSize

    assertThat(res).isEqualTo(1)
  }

  @Test
  def knownSizeOfLinkedList(): Unit = {
    val res: Int = List // linked list size cannot be get without iteration/traversal (but strange that it can be calculated cheaply for sequence)
      .range(0, 500)
      .knownSize

    assertThat(res).isEqualTo(-1)
  }

  @Test
  def unKnownSizeOfSeq(): Unit = {
    val res: Int = Seq
      .range(0, 500)
      .knownSize

    assertThat(res).isEqualTo(500)
  }

}
