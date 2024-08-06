package com.srivastavavivekggn.scala.util.placeholder.format

import java.util.Locale

trait PlaceholderValueFormatter {

  def canFormat(formatType: String): Boolean

  def canFormatValue(value: Any): Boolean = false

  def format(value: Any, args: List[String] = List.empty, locale: Locale): String
}
