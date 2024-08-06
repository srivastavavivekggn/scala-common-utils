package com.srivastavavivekggn.scala.util.unit.exception

case class UnitNotFoundException(unit: String) extends RuntimeException(s"Unit '${unit}' not found")
