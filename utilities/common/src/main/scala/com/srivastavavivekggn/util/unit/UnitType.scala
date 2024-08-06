package com.srivastavavivekggn.scala.util.unit

sealed trait UnitType

object UnitType {

  case object NONE extends UnitType

  case object LENGTH extends UnitType

  case object MASS extends UnitType

  case object AREA extends UnitType

  case object VOLUME extends UnitType

  case object TEMPERATURE extends UnitType
}