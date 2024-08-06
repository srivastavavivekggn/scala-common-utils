package com.srivastavavivekggn.scala.util.placeholder.format

import java.util.Locale

import com.srivastavavivekggn.scala.util.crypto.EncodeUtils

class UrlEncodingValueFormatter extends PlaceholderValueFormatter {
  private final val formatTypes = Seq("e", "urlEncoded")

  override def canFormat(formatType: String): Boolean = formatTypes.contains(formatType)

  override def format(value: Any, args: List[String], locale: Locale): String = EncodeUtils.urlEncode(value.toString)
}
