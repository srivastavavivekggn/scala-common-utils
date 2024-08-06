package com.srivastavavivekggn.scala.util.unit

sealed trait MeasurementSystem

object MeasurementSystem {

  case object METRIC extends MeasurementSystem
  case object IMPERIAL extends MeasurementSystem

  case object UNIVERSAL extends MeasurementSystem

}