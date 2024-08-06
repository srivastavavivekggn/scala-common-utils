package com.srivastavavivekggn.scala.util.unit.convert

import com.srivastavavivekggn.scala.util.unit.convert.VolumeConversions.{
  pintsToGallons, pintsToMilliliters, pintsToLiters,
  gallonsToPints, gallonsToLiters, gallonsToMilliliters,
  millilitersToPints, millilitersToLiters, millilitersToGallons,
  litersToPints, litersToGallons, litersToMilliliter
}

class VolumeConversionsSpec extends UnitConversionSpec {

  override val runs: List[TestRun] = List(

    TestRun(3.5, "pt", 4, 0.4375, "gal", pintsToGallons),
    TestRun(3.5, "pt", 2, 1656.12, "ml", pintsToMilliliters),
    TestRun(3.5, "pt", 2, 1.66, "l", pintsToLiters),

    TestRun(3.5, "gal", 2, 28, "pt", gallonsToPints),
    TestRun(3.5, "gal", 0, 13249, "ml", gallonsToMilliliters),
    TestRun(3.5, "gal", 2, 13.25, "l", gallonsToLiters),

    TestRun(567.3, "ml", 3, 1.199, "pt", millilitersToPints),
    TestRun(567.3, "ml", 4, 0.1499, "gal", millilitersToGallons),
    TestRun(567.3, "ml", 3, 0.567, "l", millilitersToLiters),

    TestRun(2.4, "l", 4, 5.0722, "pt", litersToPints),
    TestRun(2.4, "l", 3, 0.634, "gal", litersToGallons),
    TestRun(2.4, "l", 2, 2400, "ml", litersToMilliliter)
  )


  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  behavior of "VolumeConversions"
  runTests()

}
