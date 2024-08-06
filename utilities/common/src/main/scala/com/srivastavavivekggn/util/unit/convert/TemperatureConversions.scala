package com.srivastavavivekggn.scala.util.unit.convert

import com.srivastavavivekggn.scala.util.unit.MeasurementUnit.{DEGREE_CELSIUS => C, DEGREE_FAHRENHEIT => F}

object TemperatureConversions extends UnitConversions {

  override val conversions: CNV_MAP = Map(
    (F, F) -> identity,
    (F, C) -> fahrenheitToCelsius,
    (C, F) -> celsiusToFahrenheit,
    (C, C) -> identity
  )


  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  def fahrenheitToCelsius(in: Double): Double = (in - 32) / 1.8

  def celsiusToFahrenheit(in: Double): Double = (in * 1.8) + 32
}
