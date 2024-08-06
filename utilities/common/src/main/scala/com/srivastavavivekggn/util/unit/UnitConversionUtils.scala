package com.srivastavavivekggn.scala.util.unit

import com.srivastavavivekggn.scala.util.lang.LocaleUtils
import com.srivastavavivekggn.scala.util.unit.MeasurementSystem.{IMPERIAL, METRIC}
import com.srivastavavivekggn.scala.util.unit.convert.{AreaConversions, LengthConversions, MassConversions, TemperatureConversions, UnitConversions, VolumeConversions}
import com.srivastavavivekggn.scala.util.unit.exception.{NoUnitConversionFound, UnitConversionException, UnitTypeMismatchException}

import java.util.Locale

object UnitConversionUtils extends UnitConversions {

  /**
    * Set of known conversions. Key is a tuple of (source, target) units, and value is a function
    * that knows how to do that conversion
    */
  override val conversions: CNV_MAP = LengthConversions ++
    MassConversions ++
    TemperatureConversions ++
    AreaConversions ++
    VolumeConversions

  /**
    * Determine if we can convert source unit to target unit
    *
    * @param sourceUnit the source unit
    * @param targetUnit the target unit
    * @return true if we can convert from source to target
    */
  def canConvert(sourceUnit: String, targetUnit: String): Boolean = {
    MeasurementUnit.exists(sourceUnit) && MeasurementUnit.exists(targetUnit) &&
      MeasurementUnit.of(sourceUnit).unitType.equals(MeasurementUnit.of(targetUnit).unitType)
  }

  /**
    * Determine if we can convert the source unit to the target locale
    *
    * @param sourceUnit the source unit
    * @param locale     the locale
    * @return true if we can convert from source to target
    */
  def canConvert(sourceUnit: String, locale: Locale): Boolean = {
    // source unit exists
    MeasurementUnit.exists(sourceUnit) && (
      // measurement system is not changing
      MeasurementUnit.of(sourceUnit).measurementSystem.equals(LocaleUtils.getMeasurementSystem(locale)) ||
        // we have a way to go from metric to imperial
        MeasurementUnit.METRIC_TO_IMPERIAL.contains(MeasurementUnit.of(sourceUnit)) ||
        // we have a way to go from imperial to metric
        MeasurementUnit.IMPERIAL_TO_METRIC.contains(MeasurementUnit.of(sourceUnit))
      )
  }

  /**
    * Determine if the given value (e.g., "32 in" or "45.6 cm") can be converted to the target locale
    *
    * @param value        the value
    * @param targetLocale the target locale
    * @return true if the source can be converted
    */
  def canConvertValue(value: String, targetLocale: Locale): Boolean = {
    // convert string to a measurement
    val m = Measurement.fromString(value)

    // if measurement is defined and we have a known unit
    m.isDefined && canConvert(m.map(_.unit.abbreviation).orNull, targetLocale)
  }

  /**
    * Convert a given value (e.g., "32 in" or "45.6 cm") to the target locale
    *
    * @param value        the value
    * @param targetLocale the target locale
    * @return the measurement
    */
  def convertValue(value: String, targetLocale: Locale): Option[Measurement] = {
    Measurement.fromString(value) match {
      case None => None
      case Some(m: Measurement) if MeasurementUnit.UNKNOWN.equals(m.unit) => Some(m)
      case Some(m: Measurement) => Option(convert(m, targetLocale))
    }
  }

  /**
    * Convert the given amount in the source unit to a value in the target unit
    *
    * @param amount     the amount
    * @param sourceUnit the source unit
    * @param targetUnit the target unit
    * @return the converted Measurement
    */
  def convert(amount: Double, sourceUnit: String, targetUnit: String): Measurement = {
    convert(
      Measurement(amount, sourceUnit),
      MeasurementUnit.of(targetUnit)
    )
  }

  /**
    * Convert the given amount in the source unit to a value in the target locale
    *
    * @param amount       the amount
    * @param sourceUnit   the source unit
    * @param targetLocale the target locale
    * @return the converted Measurement
    */
  def convert(amount: Double, sourceUnit: String, targetLocale: Locale): Measurement = {
    convert(
      Measurement(amount, sourceUnit),
      targetLocale
    )
  }

  /**
    * Convert the given amount in the source unit to a value in the target locale
    *
    * @param source       the source measurement
    * @param targetLocale the target locale
    * @return the converted Measurement
    */
  def convert(source: Measurement, targetLocale: Locale): Measurement = {

    LocaleUtils.getMeasurementSystem(targetLocale) match {

      // anything 'universal' needs no conversion
      case MeasurementSystem.UNIVERSAL => source

      // no change in measurement system, simply return source unchanged
      case s: MeasurementSystem if s.equals(source.unit.measurementSystem) => source

      // target is imperial, source is metric
      case IMPERIAL if METRIC.equals(source.unit.measurementSystem) =>
        MeasurementUnit.METRIC_TO_IMPERIAL.get(source.unit)
          .map(targetUnit => convert(source, targetUnit))
          .getOrElse(throw NoUnitConversionFound(
            s"No imperial target unit found for source '$source' to locale ${targetLocale.toLanguageTag}"
          ))

      // target is metric, source is imperial
      case METRIC if IMPERIAL.equals(source.unit.measurementSystem) =>
        MeasurementUnit.IMPERIAL_TO_METRIC.get(source.unit)
          .map(targetUnit => convert(source, targetUnit))
          .getOrElse(throw NoUnitConversionFound(
            s"No metric target unit found for source '$source' to locale ${targetLocale.toLanguageTag}"
          ))

      // should not get here
      case _ => throw UnitConversionException(
        s"Could not convert source '$source' to locale ${targetLocale.toLanguageTag}"
      )
    }
  }

  /**
    * Convert the given source measurement into a measurement in the target unit
    *
    * @param source     the source measurement
    * @param targetUnit the target unit
    * @return the converted Measurement
    */
  def convert(source: Measurement, targetUnit: MeasurementUnit): Measurement = {
    // ensure we're trying to convert Distance-to-Distance or Weight-to-Weight, etc.
    if (!source.unit.unitType.equals(targetUnit.unitType)) {
      throw UnitTypeMismatchException(s"UnitTypes must match, got ${source.unit.unitType} and ${targetUnit.unitType}")
    }

    // find applicable converter
    val converter = conversions.get((source.unit, targetUnit))

    // no converter found
    if (converter.isEmpty) {
      throw NoUnitConversionFound(s"No conversion found from ${source.unit.abbreviation} to ${targetUnit.abbreviation}")
    }

    Measurement(converter.orNull.apply(source.amount), targetUnit)
  }
}
