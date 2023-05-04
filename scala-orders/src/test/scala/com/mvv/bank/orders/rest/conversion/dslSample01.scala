package com.mvv.bank.orders.rest.conversion

import org.junit.jupiter.api.Test

import scala.collection.mutable.ArrayBuffer


class Table:
    val rows = new ArrayBuffer[Row]
    def add(r: Row): Unit = rows += r
    override def toString: String = rows.mkString("Table(", ", ", ")")

class Row:
  val cells = new ArrayBuffer[Cell]
  def add(c: Cell): Unit = cells += c
  override def toString: String = cells.mkString("Row(", ", ", ")")

case class Cell(elem: String)



def table(init: Table ?=> Unit) =
  given t: Table = Table()
  init
  t

def row(init: Row ?=> Unit)(using t: Table): Unit =
  given r: Row = Row()
  init
  t.add(r)

def cell(str: String)(using r: Row): Unit =
  r.add(Cell(str))


@main def gfkjgfkjg(): Unit = {

  val aa =
    table {
      row {
        cell("top left")
        cell("top right")
      }
      row {
        cell("bottom left")
        cell("bottom right")
      }
    }

  println(s"aa: $aa")
}

// 1st approach: path method as lambda
// Disadvantage: impossible to use such methods without parentheses ()
enum Enum1 (val method: ()=>String) derives CanEqual :
  case ONE   extends Enum1(() => "1")
  case TWO   extends Enum1(() => "2")
  case THREE extends Enum1(() => "3")

// 2nd approach: extension method (you can put it into any 'object', just remember to import it)
// Advantage: really scala approach
// Disadvantage: you can miss new enum value in match if you use default compiler settings (there will be only warning)
//  Use compiler param '-Wconf:msg=match may not be exhaustive:error' to get compilation error
enum Enum2 derives CanEqual:
  case ONE, TWO, THREE
object Enum2 :
  extension (en: Enum2)
    // without parentheses () (side-effects are not expected)
    def method1: String = en match
      case Enum2.ONE   => "1"
      case Enum2.TWO   => "2"
      case Enum2.THREE => "3"
    // with parentheses () (side-effects are expected)
    def method2(): String = en match
      case Enum2.ONE   => "11"
      case Enum2.TWO   => "22"
      case Enum2.THREE => "33"


class EnumsTest {
  import scala.language.unsafeNulls
  import org.assertj.core.api.Assertions.assertThat

  @Test
  def enumsTest(): Unit = {
    assertThat(Enum1.ONE.method()).isEqualTo("1")
    assertThat(Enum1.TWO.method()).isEqualTo("2")
    assertThat(Enum1.THREE.method()).isEqualTo("3")

    // of course does not work without using ()
    //assertThat(Enum1.ONE.method).isEqualTo("1")

    assertThat(Enum2.ONE.method1).isEqualTo("1")
    assertThat(Enum2.TWO.method1).isEqualTo("2")
    assertThat(Enum2.THREE.method1).isEqualTo("3")

    assertThat(Enum2.ONE.method2()).isEqualTo("11")
    assertThat(Enum2.TWO.method2()).isEqualTo("22")
    assertThat(Enum2.THREE.method2()).isEqualTo("33")
  }
}



@main def aa(): Unit = {

  val l = 1 :: 2 :: 3 :: Nil

  l.ne(List(2))
  //l.→()

  //println(l !! 2)

}
