package com.srivastavavivekggn.scala.util.lang

import scala.math.BigDecimal.RoundingMode

object NumberUtils {

  /**
   * Ensures the desired number falls between floor and ceiling.
   *
   * @param floor   the minimum allowable value
   * @param desired the desired value
   * @param ceiling the maximum allowable value
   * @return the desired value if within range, or the smallest value that is within range
   */
  def range(floor: Int, desired: Int, ceiling: Int): Int = scala.math.min(scala.math.max(floor, desired), ceiling)

  /**
    * Rounds the given double number with the given precision.
    *
    * @param dbl   the input value
    * @param precision the precision value
    * @return the double value rounded for the given precision
    */
  def roundTo(dbl: Double, precision: Int): Double = BigDecimal(dbl).setScale(precision, RoundingMode.HALF_UP).toDouble

}
