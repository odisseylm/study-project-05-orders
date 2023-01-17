package com.mvv.collections

import org.junit.jupiter.api.Test
import org.scalatest.Assertions
import org.scalatest.Assertions.assert
import org.scalatest.Assertions.assert


class RangeExtTest {

  @Test
  def temp(): Unit = {
    //assert( ( ('A' - 1).asInstanceOf[Char] in ('A' to 'Z') ) == false)
    val ddd = 'A' in ('A' to 'Z')
    println(ddd)

    val ddd2 = ('A' to 'Z') contains 'A'
    println(ddd2)
  }

  @Test
  def forChar(): Unit = {
    assert( ( ('A' - 1).asInstanceOf[Char] in ('A' to 'Z') ) == false)
    assert('A' in ('A' to 'Z'))
    assert('Z' in ('A' to 'Z'))
    assert( ( ('Z' + 1).asInstanceOf[Char] in ('A' to 'Z') ) == false)

    assert( ( ('A'-1).asInstanceOf[Char] in ('A' until 'Z') ) == false)
    assert('A' in ('A' until 'Z'))
    assert('X' in ('A' until 'Z'))
    assert(('Z' in ('A' until 'Z')) == false)
    assert( ( ('Z' + 1).asInstanceOf[Char] in ('A' until 'Z') ) == false)
  }

  @Test
  //no insp ection SimplifyBoolean
  def forInt(): Unit = {
    assert( ( 2 in (3 to 7) ) == false)
    assert(3 in (3 to 7))
    assert(7 in (3 to 7))
    assert((8 in (3 to 7) ) == false)

    assert( (2 in (3 until 7) ) == false)
    assert(3 in (3 until 7))
    assert(6 in (3 until 7))
    assert((7 in (3 until 7)) == false)
    assert( ( 8 in (3 until 7) ) == false)
  }
}
