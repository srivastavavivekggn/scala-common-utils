package com.srivastavavivekggn.scala.util.unit.convert

import com.srivastavavivekggn.scala.util.unit.MeasurementUnit.{CENTIMETER, FOOT, INCH, METER, MILE}

object LengthConversions extends UnitConversions {

  final val IN_PER_FT = 12

  final val CM_PER_INCH = 2.54

  final val IN_PER_METER = 0.0254

  final val FT_PER_MILE = 5280

  final val CM_PER_METER = 100

  override val conversions: CNV_MAP = Map(
    (INCH, INCH) -> identity,
    (INCH, FOOT) -> inchesToFeet,
    (INCH, MILE) -> inchesToMiles,
    (INCH, CENTIMETER) -> inchesToCentimeters,
    (INCH, METER) -> inchesToMeters,
    //
    (FOOT, INCH) -> feetToInches,
    (FOOT, FOOT) -> identity,
    (FOOT, MILE) -> feetToMiles,
    (FOOT, CENTIMETER) -> feetToCentimeters,
    (FOOT, METER) -> feetToMeters,
    //
    (MILE, INCH) -> milesToInches,
    (MILE, FOOT) -> milesToFeet,
    (MILE, MILE) -> identity,
    (MILE, CENTIMETER) -> milesToCentimeters,
    (MILE, METER) -> milesToMeters,
    //
    (CENTIMETER, INCH) -> centimetersToInches,
    (CENTIMETER, FOOT) -> centimetersToFeet,
    (CENTIMETER, MILE) -> centimetersToMiles,
    (CENTIMETER, CENTIMETER) -> identity,
    (CENTIMETER, METER) -> centimetersToMeters,
    //
    (METER, INCH) -> metersToInches,
    (METER, FOOT) -> metersToFeet,
    (METER, MILE) -> metersToMiles,
    (METER, CENTIMETER) -> metersToCentimeters,
    (METER, METER) -> identity
  )


  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  //
  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  def inchesToFeet(in: Double): Double = in / IN_PER_FT

  def inchesToCentimeters(in: Double): Double = in * CM_PER_INCH

  def inchesToMeters(in: Double): Double = inchesToCentimeters(in) / CM_PER_METER

  def inchesToMiles(in: Double): Double = in / IN_PER_FT / FT_PER_MILE

  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  def feetToInches(in: Double): Double = in * IN_PER_FT

  def feetToCentimeters(in: Double): Double = inchesToCentimeters(feetToInches(in))

  def feetToMeters(in: Double): Double = feetToCentimeters(in) / CM_PER_METER

  def feetToMiles(in: Double): Double = in / FT_PER_MILE

  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  def milesToInches(in: Double): Double = feetToInches(milesToFeet(in))

  def milesToFeet(in: Double): Double = in * FT_PER_MILE

  def milesToCentimeters(in: Double): Double = inchesToCentimeters(milesToInches(in))

  def milesToMeters(in: Double): Double = inchesToMeters(milesToInches(in))

  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  def centimetersToInches(in: Double): Double = in / CM_PER_INCH

  def centimetersToFeet(in: Double): Double = inchesToFeet(centimetersToInches(in))

  def centimetersToMiles(in: Double): Double = inchesToMiles(centimetersToInches(in))

  def centimetersToMeters(in: Double): Double = in / CM_PER_METER

  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  def metersToInches(in: Double): Double = in / IN_PER_METER

  def metersToFeet(in: Double): Double = inchesToFeet(metersToInches(in))

  def metersToMiles(in: Double): Double = inchesToMiles(metersToInches(in))

  def metersToCentimeters(in: Double): Double = in * CM_PER_METER
}
