package com.srivastavavivekggn.scala.util.unit.convert

import com.srivastavavivekggn.scala.util.unit.convert.MassConversions.{kilogramsToPounds, poundsToKilograms}

class MassConversionsSpec extends UnitConversionSpec {

  override val runs: List[TestRun] = List(
    TestRun(198.7, "lb", 2, 90.13, "kg", poundsToKilograms),
    TestRun(67.88, "kg", 4, 149.6498, "lg", kilogramsToPounds)
  )

  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  behavior of "WeightConversions"
  runTests()
}
