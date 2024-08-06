package com.srivastavavivekggn.scala.util.parse.rule.impl

import fastparse.{P, StringInIgnoreCase}

/**
  * Shared operators and parsers for Array rules
  */
trait ArrayRules {

  final val FOUND_IN = "found in"
  final val NOT_FOUND_IN = "not found in"

  def operator[_: P]: P[String] = P(StringInIgnoreCase(FOUND_IN, NOT_FOUND_IN)).!
}
