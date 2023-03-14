package com.mvv.scala.temp.tests.dsl

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


@main def dslTest(): Unit = {

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
