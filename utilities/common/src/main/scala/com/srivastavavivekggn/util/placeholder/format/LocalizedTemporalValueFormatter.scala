package com.srivastavavivekggn.scala.util.placeholder.format

import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.Locale
import java.util.regex.Pattern

import com.srivastavavivekggn.scala.util.lang.LocaleUtils
import com.srivastavavivekggn.scala.util.lang.i18n.LocaleDetail.DateTimeFormats

class LocalizedTemporalValueFormatter extends PlaceholderValueFormatter {

  private val numbersOnly = Pattern.compile("\\d+")

  override def canFormat(formatType: String): Boolean = LocalizedTemporalValueFormatter.FORMAT_TYPE.equals(formatType)

  override def format(value: Any, args: List[String], localeIn: Locale): String = {

    val detailProvider = LocaleUtils.getLocaleDetail(localeIn)
    val locale = detailProvider.locale

    value match {
      // no format provided
      case t: TemporalAccessor if args.isEmpty => detailProvider.formatDate(t, DateTimeFormats.L)

      // recognized format provided
      case t: TemporalAccessor if detailProvider.hasDateFormat(args.head) => detailProvider.formatDate(t, args.head)

      // non-standard format
      case t: TemporalAccessor =>

        // perform basic date/time formatting
        val formatted = DateTimeFormatter.ofPattern(args.head).format(t)

        // if it's a string of all numbers, resolve each number to the appropriate character in that locale
        if (numbersOnly.matcher(formatted).matches()) {
          formatted.map(c => detailProvider.messageSource.getMessage(c.toString, args.toArray, c.toString, locale)).mkString("")
        }
        // otherwise, attempt to translate the given value (e.g., 'January' gets translated to 'Januar' in German)
        else {
          detailProvider.messageSource.getMessage(formatted, args.toArray, formatted, locale)
        }

      // catch-all
      case _ => value.toString
    }
  }
}

object LocalizedTemporalValueFormatter {
  final val FORMAT_TYPE = "localizedDate"
}
