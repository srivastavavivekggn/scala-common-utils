package com.srivastavavivekggn.scala.util.parse.rule.impl

import fastparse.{P, StringInIgnoreCase}

trait ArrayOfRules {

  final val HAS_ONE_OF = "has one of"

  final val HAS_ALL_OF = "has all of"

  def operator[_: P]: P[String] = P(StringInIgnoreCase(HAS_ONE_OF, HAS_ALL_OF)).!
}
