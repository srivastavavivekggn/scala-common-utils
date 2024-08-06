package com.srivastavavivekggn.scala.util.unit

import com.srivastavavivekggn.scala.util.BaseUtilSpec
import com.srivastavavivekggn.scala.util.lang.{LocaleUtils, StringUtils}
import com.srivastavavivekggn.scala.util.unit.exception.{NoUnitConversionFound, UnitNotFoundException, UnitTypeMismatchException}

class UnitConversionUtilsSpec extends BaseUtilSpec {

  val imperialLocale = LocaleUtils.parseLocale("en-US")
  val metricLocale = LocaleUtils.parseLocale("de-DE")

  behavior of "UnitConversionUtils"

  it should "fail to convert on unknown unit (source)" in {
    intercept[UnitNotFoundException] {
      UnitConversionUtils.convert(2.3, "bad", "in")
    }
  }

  it should "fail to convert on unknown unit (target)" in {
    intercept[UnitNotFoundException] {
      UnitConversionUtils.convert(2.3, "in", "bad")
    }
  }

  it should "fail to convert when unit types differ" in {
    intercept[UnitTypeMismatchException] {
      UnitConversionUtils.convert(2.3, "in", "gal")
    }
  }

  it should "convert measurement (direct unit)" in {
    val result = UnitConversionUtils.convert(24, "in", "ft")
    assertResult(2.0)(result.amount)
    assertResult(MeasurementUnit.FOOT)(result.unit)
  }

  it should "convert measurement (mesaurement and unit)" in {
    val result = UnitConversionUtils.convert(Measurement(24, MeasurementUnit.INCH), MeasurementUnit.FOOT)
    assertResult(2.0)(result.amount)
    assertResult(MeasurementUnit.FOOT)(result.unit)
  }

  it should "pass through original measurement" in {
    val result = UnitConversionUtils.convert(Measurement(24, MeasurementUnit.INCH), MeasurementUnit.INCH)
    assertResult(24.0)(result.amount)
    assertResult(MeasurementUnit.INCH)(result.unit)
  }

  it should "convert from imperial to metric (in to cm)" in {
    val result = UnitConversionUtils.convert(Measurement(24, MeasurementUnit.INCH), metricLocale)
    assertResult(MeasurementUnit.CENTIMETER)(result.unit)
  }

  it should "convert from metric to imperial (cm to in)" in {
    val result = UnitConversionUtils.convert(Measurement(24, MeasurementUnit.CENTIMETER), imperialLocale)
    assertResult(MeasurementUnit.INCH)(result.unit)
  }

  it should "confirm conversion is possible (in to km)" in {
    assertResult(true)(UnitConversionUtils.canConvert("in", "km"))
  }

  it should "confirm conversion is not possible (in to lb)" in {
    assertResult(false)(UnitConversionUtils.canConvert("in", "lb"))
  }

  it should "confirm conversion is possible (in to en-US and de-DE)" in {
    assertResult(true)(UnitConversionUtils.canConvert("in", imperialLocale))
    assertResult(true)(UnitConversionUtils.canConvert("in", metricLocale))
  }

  it should "confirm conversion is not possible (kg/m2)" in {
    assertResult(false)(UnitConversionUtils.canConvert("kg\\m2", imperialLocale))
    assertResult(false)(UnitConversionUtils.canConvert("kg\\m2", metricLocale))
  }

  it should "confirm conversion is possible for string value" in {
    assertResult(true)(UnitConversionUtils.canConvertValue("32 in", metricLocale))
    assertResult(true)(UnitConversionUtils.canConvertValue("32in", imperialLocale))
    assertResult(true)(UnitConversionUtils.canConvertValue("32.54 cm", metricLocale))
    assertResult(true)(UnitConversionUtils.canConvertValue("32.54cm", imperialLocale))
  }

  it should "confirm conversion is NOT possible for string value" in {
    assertResult(false)(UnitConversionUtils.canConvertValue("32 mg/dl2", metricLocale))
    assertResult(false)(UnitConversionUtils.canConvertValue("32         ", imperialLocale))
    assertResult(false)(UnitConversionUtils.canConvertValue("32.54cats", metricLocale))
    assertResult(false)(UnitConversionUtils.canConvertValue("32.54", imperialLocale))
  }

  it should "convert a string value properly (unknown unit)" in {
    val v1 = UnitConversionUtils.convertValue("32 mg/dl2", metricLocale)
    assertResult(true)(v1.isDefined)
    assertResult(32.0)(v1.get.amount)
    assertResult(MeasurementUnit.UNKNOWN)(v1.get.unit)
    assertResult("mg/dl2")(v1.get.unitName.orNull)
  }

  it should "convert a string value properly (known unit)" in {
    val measurement = Measurement(32.0, MeasurementUnit.INCH)
    val v1 = UnitConversionUtils.convertValue("32 in", metricLocale)
    assertResult(true)(v1.isDefined)
    assertResult(UnitConversionUtils.convert(measurement, metricLocale).amount)(v1.get.amount)
    assertResult(MeasurementUnit.CENTIMETER)(v1.get.unit)
    assertResult(true)(v1.get.unitName.isEmpty)

    val v2 = UnitConversionUtils.convertValue("32 in", imperialLocale)
    assertResult(true)(v2.isDefined)
    assertResult(32.0)(v2.get.amount)
    assertResult(MeasurementUnit.INCH)(v2.get.unit)
    assertResult(true)(v2.get.unitName.isEmpty)
  }

  it should "keep original value when rounded with no decimal" in {
    val v1 = UnitConversionUtils.convertValue("32 mg/dl2", metricLocale)

    val roundedValue = v1.map(_.roundedTo(2))
    assertResult(true)(roundedValue.isDefined)
    assertResult(true)(StringUtils.isEqualIgnoreCase(roundedValue, "32"))
  }

  it should "remove trailing zeros from decimal" in {
    val v1 = UnitConversionUtils.convertValue("32.00 mg/dl2", metricLocale)

    val roundedValue = v1.map(_.roundedTo(2))
    assertResult(true)(roundedValue.isDefined)
    assertResult(true)(StringUtils.isEqualIgnoreCase(roundedValue, "32"))
  }

  it should "keep trailing non-zeros in decimal" in {
    val v1 = UnitConversionUtils.convertValue("32.3423 mg/dl2", metricLocale)

    val roundedValue = v1.map(_.roundedTo(2))
    assertResult(true)(roundedValue.isDefined)
    assertResult(true)(StringUtils.isEqualIgnoreCase(roundedValue, "32.34"))
  }

  it should "round to the specified # of precision" in {
    val v1 = UnitConversionUtils.convertValue("32.3423458 mg/dl2", metricLocale)

    for(a <- 0 to 7) {
      val roundedValue = v1.map(_.roundedTo(a))
      assertResult(true)(roundedValue.isDefined)
      val roundedValueString = roundedValue.getOrElse(StringUtils.EMPTY)
      val digitsAfterDecimal = roundedValueString.split("\\.").tail.headOption.getOrElse(StringUtils.EMPTY).length
      // the rounded value should have the correct # of digits after the decimal
      assertResult(true)(a == digitsAfterDecimal)
    }
  }

}
