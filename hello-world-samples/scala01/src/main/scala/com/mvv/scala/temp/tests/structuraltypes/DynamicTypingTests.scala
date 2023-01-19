package com.mvv.scala.temp.tests.structuraltypes

// study sample (for myself only!!!)
// based on https://github.com/Baeldung/scala-tutorials/blob/master/scala-core-4/src/main/scala/com/baeldung/scala/structuraltypes/Quack.scala


import scala.language.reflectiveCalls
import scala.languageFeature.reflectiveCalls
import scala.reflect.Selectable.reflectiveSelectable


class Duck :
  def fly(): Unit = println("Ducks fly together")

class Eagle :
  def fly(): Unit = println("Eagles fly better than MJ")

class Walrus :
  def swim(): Unit = println("I am faster on the water than on the land")


//def flyLikeAnEagle(animal: Any) = animal.fly()

type Flyer = { def fly(): Unit }

//noinspection LanguageFeature
def callFly(thing: Flyer): Unit = thing.fly()

//noinspection LanguageFeature
def callFly2(thing: { def fly(): Unit }): Unit = thing.fly()

//noinspection LanguageFeature
def callFly3[T <: { def fly(): Unit }](thing: T): Unit = thing.fly()


@main
def example11(): Unit = {
  callFly(new Duck())
  callFly2(new Duck())
  callFly3(new Duck())

  callFly(new Eagle())
  callFly2(new Eagle())
  callFly3(new Eagle())

  // The following code won't compile
  //callFly(new Walrus())
  //callFly2(new Walrus())
  //callFly3(new Walrus())
}



type Closable = { def close(): Unit }

def using(resource: Closable)(fn: () => Unit): Unit =
  try fn() finally {
    //noinspection LanguageFeature
    resource.close() }

/*
using(file) {
  () =>
  // Code using the file
}
*/


// others temp // TODO: move to other study topic
def isIntOrString[T <: String | Int](t: T): String = t match {
  case i: Int => "%d is an Integer".format(i.asInstanceOf[Int])
  case s: String => "%s is a String".format(s)
}

def isIntOrString(t: Either[Int, String]): String = {
  t match {
    case Left(i) => "%d is an Integer".format(i)
    case Right(s) => "%s is a String".format(s)
  }
}
