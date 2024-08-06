package com.srivastavavivekggn.scala.util.placeholder.format

import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.Locale

class TemporalValueFormatter extends PlaceholderValueFormatter {
  override def canFormat(formatType: String): Boolean = "date".equals(formatType)

  override def canFormatValue(value: Any): Boolean = value match {
    case _: TemporalAccessor => true
    case _ => false
  }

  override def format(value: Any, args: List[String], locale: Locale): String = value match {
    case t: TemporalAccessor if args.isEmpty => DateTimeFormatter.ISO_LOCAL_DATE.format(t)
    case t: TemporalAccessor => DateTimeFormatter.ofPattern(args.head).format(t)
    case _ => value.toString
  }
}
