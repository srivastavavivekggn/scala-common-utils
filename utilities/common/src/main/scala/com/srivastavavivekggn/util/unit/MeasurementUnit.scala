package com.srivastavavivekggn.scala.util.unit

import com.srivastavavivekggn.scala.util.lang.StringUtils
import com.srivastavavivekggn.scala.util.unit.MeasurementSystem.{IMPERIAL, METRIC, UNIVERSAL}
import com.srivastavavivekggn.scala.util.unit.UnitType.{LENGTH, TEMPERATURE, VOLUME, MASS, NONE}
import com.srivastavavivekggn.scala.util.unit.exception.UnitNotFoundException

sealed abstract class MeasurementUnit(val singular: String,
                                      val plural: String,
                                      val abbreviation: String,
                                      val unitType: UnitType,
                                      val measurementSystem: MeasurementSystem)

object MeasurementUnit {

  case object UNKNOWN extends MeasurementUnit("", "", "", NONE, UNIVERSAL)

  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  // distance
  case object INCH extends MeasurementUnit("inch", "inches", "in", LENGTH, IMPERIAL)

  case object FOOT extends MeasurementUnit("foot", "feet", "ft", LENGTH, IMPERIAL)

  case object MILE extends MeasurementUnit("mile", "miles", "mi", LENGTH, IMPERIAL)

  case object CENTIMETER extends MeasurementUnit("centimeter", "centimeters", "cm", LENGTH, METRIC)

  case object METER extends MeasurementUnit("meter", "meters", "m", LENGTH, METRIC)

  case object KILOMETER extends MeasurementUnit("kilometer", "kilometers", "km", LENGTH, METRIC)

  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  // weight
  case object KILOGRAM extends MeasurementUnit("kilogram", "kilograms", "kg", MASS, METRIC)

  case object POUND extends MeasurementUnit("pound", "pounds", "lb", MASS, IMPERIAL)

  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  // volume
  case object PINT extends MeasurementUnit("pint", "pints", "pt", VOLUME, IMPERIAL)
  case object GALLON extends MeasurementUnit("gallon", "gallons", "gal", VOLUME, IMPERIAL)
  case object MILLILITER extends MeasurementUnit("milliliter", "milliliters", "ml", VOLUME, METRIC)
  case object LITER extends MeasurementUnit("liter", "liters", "l", VOLUME, METRIC)

  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  // area


  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  // temperature
  case object DEGREE_FAHRENHEIT extends MeasurementUnit("degree Fahrenheit", "degrees Fahrenheit", "F", TEMPERATURE, IMPERIAL)

  case object DEGREE_CELSIUS extends MeasurementUnit("degree Celsius", "degrees Celsius", "C", TEMPERATURE, METRIC)



  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  // convenience values / lookups

  final val IMPERIAL_TO_METRIC: Map[MeasurementUnit, MeasurementUnit] = Map(
    INCH -> CENTIMETER,
    FOOT -> METER,
    MILE -> KILOMETER,
    PINT -> MILLILITER,
    GALLON -> LITER,
    POUND -> KILOGRAM,
    DEGREE_FAHRENHEIT -> DEGREE_CELSIUS
  )

  final val METRIC_TO_IMPERIAL = IMPERIAL_TO_METRIC.map(e => e._2 -> e._1)

  final val LENGTH_UNITS = Seq(INCH, FOOT, MILE, CENTIMETER, METER, KILOMETER)
  final val MASS_UNITS = Seq(KILOGRAM, POUND)
  final val VOLUME_UNITS = Seq(PINT, GALLON, MILLILITER, LITER)
  final val AREA_UNITS = Seq()
  final val TEMPERATURE_UNITS = Seq(DEGREE_FAHRENHEIT, DEGREE_CELSIUS)

  final val ALL_UNITS: Seq[MeasurementUnit] = LENGTH_UNITS ++ MASS_UNITS ++ VOLUME_UNITS ++ AREA_UNITS ++ TEMPERATURE_UNITS

  def of(name: String): MeasurementUnit = find(name).getOrElse(throw UnitNotFoundException(name))

  def exists(name: String): Boolean = find(name).isDefined

  def find(name: String): Option[MeasurementUnit] = StringUtils.nonEmpty(name, trim = true).flatMap(nm => ALL_UNITS.find(
    u => u.abbreviation.equalsIgnoreCase(nm) || u.singular.equalsIgnoreCase(nm) || u.plural.equalsIgnoreCase(nm)
  ))
}
