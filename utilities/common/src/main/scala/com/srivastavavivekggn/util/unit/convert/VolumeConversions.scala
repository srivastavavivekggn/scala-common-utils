package com.srivastavavivekggn.scala.util.unit.convert

import com.srivastavavivekggn.scala.util.unit.MeasurementUnit.{GALLON, LITER, MILLILITER, PINT}

object VolumeConversions extends UnitConversions {

  final val PINTS_PER_GALLON = 8
  final val PINTS_PER_LITER = 2.1134
  final val MILLILITERS_PER_PINT = 473.176
  final val MILLILITERS_PER_GALLON = 3785.41
  final val MILLILITERS_PER_LITER = 1000
  final val GALLONS_PER_LITER = 3.785

  override val conversions: CNV_MAP = Map(
    (PINT, PINT) -> identity,
    (PINT, GALLON) -> pintsToGallons,
    (PINT, MILLILITER) -> pintsToMilliliters,
    (PINT, LITER) -> pintsToLiters,

    (GALLON, PINT) -> gallonsToPints,
    (GALLON, GALLON) -> identity,
    (GALLON, MILLILITER) -> gallonsToMilliliters,
    (GALLON, LITER) -> gallonsToLiters,

    (MILLILITER, PINT) -> millilitersToPints,
    (MILLILITER, GALLON) -> millilitersToGallons,
    (MILLILITER, MILLILITER) -> identity,
    (MILLILITER, LITER) -> millilitersToLiters,

    (LITER, PINT) -> litersToPints,
    (LITER, GALLON) -> litersToGallons,
    (LITER, MILLILITER) -> litersToMilliliter,
    (LITER, LITER) -> identity
  )


  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  //
  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  def pintsToGallons(in: Double): Double = in / PINTS_PER_GALLON

  def pintsToMilliliters(in: Double): Double = in * MILLILITERS_PER_PINT

  def pintsToLiters(in: Double): Double = in / PINTS_PER_LITER

  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  def gallonsToPints(in: Double): Double = in * PINTS_PER_GALLON

  def gallonsToMilliliters(in: Double): Double = in * MILLILITERS_PER_GALLON

  def gallonsToLiters(in: Double): Double = in * GALLONS_PER_LITER

  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  def millilitersToPints(in: Double): Double = in / MILLILITERS_PER_PINT

  def millilitersToGallons(in: Double): Double = in / MILLILITERS_PER_GALLON

  def millilitersToLiters(in: Double): Double = in / MILLILITERS_PER_LITER

  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  def litersToPints(in: Double): Double = in * PINTS_PER_LITER

  def litersToGallons(in: Double): Double = in / GALLONS_PER_LITER

  def litersToMilliliter(in: Double): Double = in * MILLILITERS_PER_LITER
}
