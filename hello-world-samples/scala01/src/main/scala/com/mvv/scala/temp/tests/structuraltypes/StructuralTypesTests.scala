package com.mvv.scala.temp.tests.structuraltypes

import scala.annotation.unused
//import scala.language.reflectiveCalls
//import scala.reflect.Selectable.reflectiveSelectable
//import languageFeature.reflectiveCalls
//import reflect.Selectable.reflectiveSelectable


class DatabaseRecord(elems: Map[String, Any]) extends Selectable :
  private val fields = elems
  def this(elems: (String, Any)*) = this(elems.toMap)
  @unused
  def selectDynamic(name: String): Any = fields(name)
  override def toString: String = s"${getClass.getSimpleName} ${fields.mkString("{", ", ", "}")}"

type Person = DatabaseRecord {
  val name: String
  val age: Int
}

type Book = DatabaseRecord {
  val title: String
  val author: String
  val year: Int
  val rating: Double
}


@main
def example1(): Unit = {
  val person = DatabaseRecord(
    "name" -> "Emma",
    "age" -> 42
  ).asInstanceOf[Person]

  println(s"${person.name} is ${person.age} years old.")
}


@main
def example2(): Unit = {
  val databaseValuesAsArray = Array[(String, Any)] (
    "title" -> "The Catcher in the Rye",
    "author" -> "J. D. Salinger",
    "year" -> 1951,
    "rating" -> 4.5
  )
  val book = DatabaseRecord(databaseValuesAsArray*).asInstanceOf[Book]
  println(book)

  println(book.title)
  println(book.author)
  println(book.year)
  println(book.rating)
}


@main
def example3(): Unit = {
  val databaseValuesAsMap: Map[String, Any] = Map(
    "title" -> "The Catcher in the Rye",
    "author" -> "J. D. Salinger",
    "year" -> 1951,
    "rating" -> 4.5
  )

  val book = DatabaseRecord(databaseValuesAsMap.toArray*).asInstanceOf[Book]
  println(book)
}



