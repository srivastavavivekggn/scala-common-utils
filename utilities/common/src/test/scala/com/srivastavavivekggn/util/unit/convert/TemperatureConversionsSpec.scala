package com.srivastavavivekggn.scala.util.unit.convert

import com.srivastavavivekggn.scala.util.unit.convert.TemperatureConversions.{celsiusToFahrenheit, fahrenheitToCelsius}

class TemperatureConversionsSpec extends UnitConversionSpec {
  override val runs: List[TestRun] = List(
    TestRun(98.6, "F", 2, 37.0, "C", fahrenheitToCelsius),
    TestRun(-11.0, "F", 2, -23.89, "C", fahrenheitToCelsius),
    TestRun(47.43, "C", 3, 117.374, "F", celsiusToFahrenheit)
  )

  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  behavior of "TemperatureConversions"
  runTests()
}
