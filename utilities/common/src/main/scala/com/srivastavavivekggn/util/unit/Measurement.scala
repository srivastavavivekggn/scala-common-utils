package com.srivastavavivekggn.scala.util.unit

import com.srivastavavivekggn.scala.util.lang.StringUtils

import java.text.DecimalFormat
import java.util.regex.Pattern


case class Measurement(amount: Double,
                       unit: MeasurementUnit,
                       unitName: Option[String] = None) {

  def roundedTo(precision: Int): String = {
    try {
      val fmt = Measurement.Precision.getFormat(precision)
      fmt.format(amount)
    }
    catch {
      // just return the amount if an exception
      case _: Exception => amount.toString
    }
  }

  def toString(precision: Int): String = {

    val amt = roundedTo(precision)

    unit match {
      case MeasurementUnit.UNKNOWN if unitName.isDefined => s"$amt ${unitName.orNull}"
      case MeasurementUnit.UNKNOWN => amt
      case _ => s"$amt ${unit.abbreviation}"
    }
  }

  override def toString: String = toString(2)
}


object Measurement {

  object Precision {
    val ZERO = new DecimalFormat("0")
    val ONE = new DecimalFormat("0.#")
    val TWO = new DecimalFormat("0.##")
    val THREE = new DecimalFormat("0.###")

    def getFormat(precision: Int): DecimalFormat = precision match {
      case 1 => ONE
      case 2 => TWO
      case 3 => THREE
      // anything > 3
      case p: Int if p > 3 => new DecimalFormat(s"0.${"#" * precision}")
      // anything else (< 1 or > 3)
      case _ => ZERO
    }
  }

  /**
    * Regex to parse string amounts with units (e.g., "32 in", "45.3 cm")
    */
  private val VALUE_REGEX: Pattern = Pattern.compile("^(\\d+(\\.\\d+)?)(\\s*(.*))?$")

  def apply(amount: Double, unitName: String): Measurement = {
    Measurement(amount, MeasurementUnit.of(unitName))
  }

  /**
    * Parse a string into a Measurement
    *
    * @param str the string
    * @return the Measurement, if able to parse
    */
  def fromString(str: String): Option[Measurement] = {
    StringUtils.nonEmpty(str, trim = true)
      .filter(VALUE_REGEX.matcher(_).matches())
      .flatMap(v => {
        val matcher = VALUE_REGEX.matcher(v)

        if (matcher.find()) {
          val amount = matcher.group(1).toDouble
          val unit = matcher.group(4)

          // if we have a known unit
          if (MeasurementUnit.exists(unit)) {
            Some(Measurement(amount, MeasurementUnit.of(unit)))
          }
          else {
            Some(Measurement(amount, MeasurementUnit.UNKNOWN, StringUtils.nonEmpty(unit, trim = true)))
          }
        }
        else {
          None
        }
      })
  }
}
