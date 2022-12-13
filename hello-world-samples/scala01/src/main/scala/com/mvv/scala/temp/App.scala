package com.mvv.scala.temp

/**
 * @author ${user.name}
 */
object App {
  
  def foo(x : Array[String]): Unit = x.foldLeft("")((a,b) => a + b)
  
  def main(args : Array[String]): Unit = {
    println( "Hello World!" )
    println("concat arguments = " + foo(args))
  }
}
