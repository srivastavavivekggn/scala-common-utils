package com.srivastavavivekggn.scala.util.unit.convert

import com.srivastavavivekggn.scala.util.unit.convert.LengthConversions._

class LengthConversionsSpec extends UnitConversionSpec {

  override val runs = List(
    TestRun(37.0, "in", 2, 3.08, "ft", inchesToFeet),
    TestRun(23.22, "in", 3, 1.935, "ft", inchesToFeet),

    TestRun(23.22, "in", 2, 58.98, "cm", inchesToCentimeters),

    TestRun(23.22, "in", 2, 0.59, "m", inchesToMeters),

    TestRun(2723, "in", 6, 0.042977, "mi", inchesToMiles),

    // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

    TestRun(3.4, "ft", 2, 40.8, "in", feetToInches),

    TestRun(3.4, "ft", 2, 103.63, "cm", feetToCentimeters),

    TestRun(3.4, "ft", 2, 1.04, "m", feetToMeters),

    TestRun(8789.4, "ft", 3, 1.665, "mi", feetToMiles),

    // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

    TestRun(0.67, "mi", 2, 42451.2, "in", milesToInches),

    TestRun(0.67, "mi", 2, 3537.6, "ft", milesToFeet),

    TestRun(0.67, "mi", 0, 107826, "cm", milesToCentimeters),

    TestRun(0.67, "mi", 2, 1078.26, "m", milesToMeters),

    // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

    TestRun(454.5, "cm", 2, 178.94, "in", centimetersToInches),

    TestRun(454.5, "cm", 5, 14.91142, "ft", centimetersToFeet),

    TestRun(454.5, "cm", 4, 0.0028, "mi", centimetersToMiles),

    TestRun(454.5, "cm", 2, 4.55, "m", centimetersToMeters),

    // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

    TestRun(2.4, "m", 1, 94.5, "in", metersToInches),

    TestRun(2.4, "m", 2, 7.87, "ft", metersToFeet),

    TestRun(2376.23, "m", 2, 1.48, "mi", metersToMiles),

    TestRun(2.47, "m", 2, 247, "cm", metersToCentimeters)
  )


  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  behavior of "DistanceConversions"
  runTests()
}
