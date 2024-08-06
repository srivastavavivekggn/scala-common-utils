package com.srivastavavivekggn.scala.util.unit.convert

import com.srivastavavivekggn.scala.util.unit.MeasurementUnit

trait UnitConversions {

  type CNV_MAP = Map[(MeasurementUnit, MeasurementUnit), Double => Double]

  implicit def conversionsToFnMap(uc: UnitConversions): CNV_MAP = uc.conversions

  def conversions: CNV_MAP

}
