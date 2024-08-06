package com.srivastavavivekggn.scala.util.unit.convert

import com.srivastavavivekggn.scala.util.unit.MeasurementUnit.{KILOGRAM, POUND}

object MassConversions extends UnitConversions {

  final val KG_PER_LB = 0.45359237

  override val conversions: CNV_MAP = Map(
    (POUND, POUND) -> identity,
    (POUND, KILOGRAM) -> poundsToKilograms,
    (KILOGRAM, POUND) -> kilogramsToPounds,
    (KILOGRAM, KILOGRAM) -> identity
  )

  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  def kilogramsToPounds(in: Double): Double = in / KG_PER_LB

  def poundsToKilograms(in: Double): Double = in * KG_PER_LB
}
