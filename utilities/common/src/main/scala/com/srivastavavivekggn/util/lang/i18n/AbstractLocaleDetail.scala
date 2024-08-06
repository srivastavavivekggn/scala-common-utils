package com.srivastavavivekggn.scala.util.lang.i18n

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

import com.srivastavavivekggn.scala.util.collection.CollectionUtils
import com.srivastavavivekggn.scala.util.placeholder.format.{LocalizedTemporalValueFormatter, TranslationValueFormatter}
import org.springframework.context.MessageSource
import org.springframework.context.support.StaticMessageSource

abstract case class AbstractLocaleDetail(locale: Locale) extends LocaleDetail {

  /**
    * The symbols used for numbers
    * @return the number symbols
    */
  def numbers: List[String]

  /**
    * The names of AM/PM
    *
    * @return the am/pm names
    */
  def amPm: List[String]

  /**
    * The names of the months
    *
    * @return the month names
    */
  def months: List[String]

  /**
    * The abbreviated names of the months
    *
    * @return the abbreviated month names
    */
  def monthsShort: List[String]

  /**
    * The days of the week
    *
    * @return the week days, starting with Sunday
    */
  def weekdays: List[String]

  /**
    * The abbreviated days of the week
    *
    * @return the weekday abbreviations, starting with Sunday
    */
  def weekdaysShort: List[String]

  /**
    * The pre-defined date formats
    *
    * These are modeled from MomentJS library's formats:
    * https://momentjs.com/docs/#/displaying/format/
    *
    * @return the common date formats
    */
  override def standardFormats: Map[String, String] = Map(
    LocaleDetail.DateTimeFormats.LT -> LocaleDetail.CommonFormats.HH24_MM,
    LocaleDetail.DateTimeFormats.LTS -> LocaleDetail.CommonFormats.HH24_MM_SS
  )

  /**
    * Construct a message source
    */
  override lazy val messageSource: MessageSource = {
    val messageSource = new StaticMessageSource

    // if not the default locale, set parent message source
    if (!Locale.getDefault.equals(locale)) {
      messageSource.setParentMessageSource(AbstractLocaleDetail.defaultMessageSource)
    }

    val nums = numbers.zipWithIndex.map {
      case (v, idx) => AbstractLocaleDetail.numbers(idx) -> v
    }.toMap

    val ampm = amPm.zipWithIndex.map {
      case (v, idx) => AbstractLocaleDetail.ampm(idx) -> v
    }.toMap

    val monthsKeys = months.zipWithIndex.map {
      case (v, idx) => AbstractLocaleDetail.months(idx) -> v
    }.toMap

    val monthsShortKeys = monthsShort.zipWithIndex.map {
      case (v, idx) => AbstractLocaleDetail.monthsShort(idx) -> v
    }.toMap

    val weekdaysKeys = weekdays.zipWithIndex.map {
      case (v, idx) => AbstractLocaleDetail.weekdays(idx) -> v
    }.toMap

    val weekdaysShortKeys = weekdaysShort.zipWithIndex.map {
      case (v, idx) => AbstractLocaleDetail.weekdaysShort(idx) -> v
    }.toMap

    messageSource.addMessages(
      CollectionUtils.asJavaMapOrEmpty(
        nums ++ ampm ++ monthsKeys ++ monthsShortKeys ++ weekdaysKeys ++ weekdaysShortKeys
      ),
      locale
    )

    messageSource
  }
}

// scalastyle:off magic.number
object AbstractLocaleDetail {

  // fixed starting point - a sunday in january
  private final val startDate = LocalDate.of(2021, 1, 3)

  // patterns to determine month/day names
  private val longMonth = DateTimeFormatter.ofPattern("MMMM")
  private val shortMonth = DateTimeFormatter.ofPattern("MMM")
  private val longWeekday = DateTimeFormatter.ofPattern("EEEE")
  private val shortWeekday = DateTimeFormatter.ofPattern("EEE")

  final val numbers = (0 to 10).map("" + _)

  /**
    * AM / PM values
    */
  final val ampm = List("AM", "PM")

  /**
    * The names of all the months in the default locale
    */
  final val months = (0 to 11).map(idx => longMonth.format(startDate.plusMonths(idx)))

  /**
    * The abbreviated names of all the months in the default locale
    */
  final val monthsShort = (0 to 11).map(idx => shortMonth.format(startDate.plusMonths(idx)))

  /**
    * The names of all the weekdays in the default locale
    */
  final val weekdays = (0 to 6).map(idx => longWeekday.format(startDate.plusDays(idx)))

  /**
    * The abbreviated names of the weekdays in the default locale
    */
  final val weekdaysShort = (0 to 6).map(idx => shortWeekday.format(startDate.plusDays(idx)))

  /**
    * A default message source containing all the above values as key-and-value
    */
  final val defaultMessageSource = {
    val ms = new StaticMessageSource
    (months ++ monthsShort ++ weekdays ++ weekdaysShort).foreach(v => ms.addMessage(v, Locale.getDefault, v))
    ms
  }
}

// scalastyle:on magic.number
