package com.mvv.scala.temp.tests.xmls

import scala.compiletime.uninitialized


def emptyElem: scala.xml.Elem =
  scala.xml.Elem("p", "l", scala.xml.Null, scala.xml.NamespaceBinding("p", "https://www.www.www", scala.xml.TopScope), false)


@main
def aa(): Unit = {

  import scala.language.unsafeNulls // << ============== VERY important

  val sunMass = 1.99e30
  val sunRadius = 6.96e8
  val star = <star>
    <title>Sun</title>
    <mass unit="kg">
      {sunMass}
    </mass>
    <radius unit="m">
      {sunRadius}
    </radius>
    <surface unit="m²">
      {4 * Math.PI * Math.pow(sunRadius, 2)}
    </surface>
    <volume unit="m³">
      {4 / 3 * Math.PI * Math.pow(sunRadius, 3)}
    </volume>
  </star>

  println(star.getClass)
  println(star)
}

@main
def aa2(): Unit = {

  //var star: scala.xml.Elem = uninitialized
  var star: scala.xml.Elem = emptyElem
  {
    import scala.language.unsafeNulls // << ============== VERY important
    star = <star><title>Sun</title></star>
  }

  println(star.getClass)
  println(star)
}

@main
def aa3(): Unit = {
  val star = {
    import scala.language.unsafeNulls // << ============== VERY important
    <star><title>Sun</title></star>
  }

  println(star.getClass)
  println(star)
}

/*
@main
def aa4(): Unit = {
  val star: scala.xml.Elem = <star><title>Sun</title></star>.nn       // Not compiled
  val star2: scala.xml.Elem = ( <star><title>Sun</title></star> ).nn  // Not compiled

  println(star.getClass)
  println(star)
}
*/
