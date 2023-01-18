package com.mvv.scala.temp.tests.xmls


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
