package com.srivastavavivekggn.scala.util.lang.i18n

import java.time.temporal.TemporalAccessor
import java.util.Locale

import com.srivastavavivekggn.scala.util.placeholder.PlaceholderUtils
import com.srivastavavivekggn.scala.util.placeholder.PlaceholderUtils.ReplacementMode
import org.springframework.context.MessageSource

trait LocaleDetail {

  /**
    * The locale for this detail
    *
    * @return the locale
    */
  def locale: Locale

  /**
    * Format a temporal as a string using the given format.
    *
    * This does NOT rely on Java's DateTimeFormat/SimpleDateFormat.  It utilizes
    * PlaceholderUtils to format and translate date/time
    *
    * @param dt  the temporal
    * @param fmt the named standard format or a custom format string
    * @return the formatted date
    */
  def formatDate(dt: TemporalAccessor, fmt: String): String = {

    // get the named standard format or use the input string directly
    val formatStr = standardFormats.getOrElse(fmt, fmt)

    PlaceholderUtils.replaceAllPlaceholders(
      in = formatStr,
      replacementCtx = Map(LocaleDetail.INPUT -> dt),
      replacementMode = ReplacementMode.STRIP_PLACEHOLDERS,
      locale = locale
    )
  }

  /**
    * See if the provider has a specific format available
    *
    * @param fmt the format
    * @return true if the given fmt is one of the standard formats for this provider
    */
  def hasDateFormat(fmt: String): Boolean = standardFormats.contains(fmt)

  /**
    * Expose the message source for this detail provider
    *
    * @return the message source
    */
  def messageSource: MessageSource

  /**
    * The pre-defined date formats
    *
    * These are modeled from MomentJS library's formats:
    * https://momentjs.com/docs/#/displaying/format/
    *
    * @return the common date formats
    */
  protected def standardFormats: Map[String, String]
}


object LocaleDetail {

  /**
    * Shared (static) name for placeholder context input
    */
  final val INPUT = "input"

  /**
    * Standard set of date/time formats
    *
    * These are modeled from MomentJS library's formats:
    *  - https://momentjs.com/docs/#/displaying/format/
    *
    * There are upper and lower case variations on the same formats.
    * The lowercase version is intended to be the shortened version of its uppercase counterpart.
    */
  object DateTimeFormats {

    // time without seconds
    final val LT = "LT"

    // time with seconds
    final val LTS = "LTS"

    // month numeral, day of month, year
    final val L = "L"
    final val l = "l"

    // month name, day of month, year
    final val LL = "LL"
    final val ll = "ll"

    // month name, day of month, year, time
    final val LLL = "LLL"
    final val lll = "lll"

    // day of week, month name, day of month, year, time
    final val LLLL = "LLLL"
    final val llll = "llll"

    final val ALL = List(LT, LTS, L, LL, LLL, LLLL, l, ll, lll, llll)
  }

  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  // =- Common translated date/time placeholder components
  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  object CommonFormats {
    final val MONTH_LONG = s"{{$INPUT|localizedDate:MMMM}}"
    final val MONTH_SHORT = s"{{$INPUT|localizedDate:MMM}}"

    final val WEEKDAY_LONG = s"{{$INPUT|localizedDate:EEEE}}"
    final val WEEKDAY_SHORT = s"{{$INPUT|localizedDate:EEE}}"

    final val YEAR = s"{{$INPUT|localizedDate:yyyy}}"

    final val DAY = s"{{$INPUT|localizedDate:d}}"
    final val DAY2 = s"{{$INPUT|localizedDate:dd}}"

    final val MONTH = s"{{$INPUT|localizedDate:M}}"
    final val MONTH2 = s"{{$INPUT|localizedDate:MM}}"

    final val AM_PM = s"{{$INPUT|localizedDate:a}}"

    final val H24_MM = s"{{$INPUT|localizedDate:H}}:{{$INPUT|localizedDate:mm}}"
    final val HH24_MM = s"{{$INPUT|localizedDate:HH}}:{{$INPUT|localizedDate:mm}}"
    final val H24_MM_SS = s"{{$INPUT|localizedDate:H}}:{{$INPUT|localizedDate:mm}}:{{$INPUT|localizedDate:ss}}"
    final val HH24_MM_SS = s"{{$INPUT|localizedDate:HH}}:{{$INPUT|localizedDate:mm}}:{{$INPUT|localizedDate:ss}}"

    final val H_MM = s"{{$INPUT|localizedDate:h}}:{{$INPUT|localizedDate:mm}}"
    final val HH_MM = s"{{$INPUT|localizedDate:hh}}:{{$INPUT|localizedDate:mm}}"
    final val H_MM_SS = s"{{$INPUT|localizedDate:h}}:{{$INPUT|localizedDate:mm}}:{{$INPUT|localizedDate:ss}}"
    final val HH_MM_SS = s"{{$INPUT|localizedDate:hh}}:{{$INPUT|localizedDate:mm}}:{{$INPUT|localizedDate:ss}}"

    final val DD_MM_YYYY = s"$DAY2/$MONTH2/$YEAR"
    final val D_M_YYYY = s"$DAY/$MONTH/$YEAR"
  }
}
