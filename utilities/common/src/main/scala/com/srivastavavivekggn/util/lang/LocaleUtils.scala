package com.srivastavavivekggn.scala.util.lang

import java.time.temporal.Temporal
import java.util.Locale
import com.srivastavavivekggn.scala.util.lang.i18n._
import com.srivastavavivekggn.scala.util.unit.MeasurementSystem
import org.springframework.util.StringUtils.parseLocaleString

/**
  * Utility methods for dealing with locales (with and without measurement systems)
  */
object LocaleUtils {

  /**
    * Metric measurement extension
    */
  final val metricMeasurement = "-u-ms-metric"

  /**
    * Imperial measurement extension
    */
  final val imperialMeasurement = "-u-ms-ussystem"

  /**
    * map of locales and measurement system
    * see https://arnoldmedia.jira.com/wiki/spaces/IM/pages/1040581228/International+Translations+Multi-Language
    */
  final val localesUsingImperialUnits = List(
    "en-US", "es-US",                       // US market
    "en-LR", "vai-Latn-LR", "vai-Vaii-LR",  // Liberia
    "my-MM"                                 // Burma / Myanmar
  )

  /**
    * Default locale detail provider
    */
  final val defaultLocaleDetail = new EnglishUsLocaleDetail

  /**
    * Map the full set of detail providers
    */
  final val localeDetailProviders: Map[Locale, LocaleDetail] = LocaleDetailProviders.ALL.map(d => d.locale -> d).toMap

  /**
    * Get a specific detail provider for the given locale
    * @param l the locale
    * @return the detail provider, or the default if a more specific one is not found
    */
  def getLocaleDetail(l: Locale): LocaleDetail = {
    // look for full exact match
    localeDetailProviders.get(l)
      // drop any measurement system and try again
      .orElse(localeDetailProviders.get(LocaleUtils.getLocaleWithoutMeasurement(l)))
      // drop any variants and look for lang + country
      .orElse(localeDetailProviders.get(new Locale(l.getLanguage, l.getCountry)))
      // look for lang only
      .orElse(localeDetailProviders.get(new Locale(l.getLanguage)))
      // get default
      .getOrElse(defaultLocaleDetail)
  }


  object DateTimeFormat {
    def format(dt: Temporal, fmt: String, locale: Locale): String = {
      val detail = getLocaleDetail(locale)
      detail.formatDate(dt, fmt)
    }
  }

  /**
    * Get the locale with a specified measurement system extension.
    *
    * If locale already has the measurement system, just returns
    *
    * @param l the locale
    * @return the locale with measurement system appended
    */
  def getLocaleWithMeasurement(l: String): String = StringUtils.nonEmpty(l, trim = true) match {
    case Some(locale) if locale.endsWith(imperialMeasurement) || locale.endsWith(metricMeasurement) => locale
    case Some(locale) if localesUsingImperialUnits.contains(locale) => locale + imperialMeasurement
    case Some(locale) => locale + metricMeasurement
    case _ => l
  }

  /**
    * Get a locale without the measurement system
    *
    * @param l the locale
    * @return the locale without the measurement system appended
    */
  def getLocaleWithoutMeasurement(l: String): String = StringUtils.nonEmpty(l, trim = true) match {
    case Some(locale) if locale.endsWith(imperialMeasurement) => locale.replace(imperialMeasurement, "")
    case Some(locale) if locale.endsWith(metricMeasurement) =>  locale.replace(metricMeasurement, "")
    case _ => l
  }

  /**
    * Get an updated locale that does not include any measurement system
    *
    * @param l the locale
    * @return the updated locale with measurement system stripped off
    */
  def getLocaleWithoutMeasurement(l: Locale): Locale = getValidLocale(getLocaleWithoutMeasurement(l.toLanguageTag))

  /**
    * Get an updated locale that includes any measurement system
    *
    * @param l the locale
    * @return the updated locale including measurement system
    */
  def getLocaleWithMeasurement(l: Locale): Locale = getValidLocale(getLocaleWithMeasurement(l.toLanguageTag))

  /**
    * Get the appropriate measurement system for the given locale
    *
    * @param l the locale
    * @return the measurement system
    */
  def getMeasurementSystem(l: Locale): MeasurementSystem = getMeasurementSystem(l.toLanguageTag)

  /**
    * Get the appropriate measurement system for the given locale
    *
    * @param l the locale
    * @return the measurement system
    */
  def getMeasurementSystem(l: String): MeasurementSystem = getLocaleWithMeasurement(l) match {
    case locale: String if locale.endsWith(imperialMeasurement) => MeasurementSystem.IMPERIAL
    case _ => MeasurementSystem.METRIC
  }

  /**
    * Helper method to validate passed locale
    *
    * @param locale the locale to check
    */
  def getValidLocale(locale: String): Locale = {
    require(StringUtils.isNotEmpty(locale), "A valid locale string is required")
    LocaleUtils.parseLocale(locale)
  }

  /**
    * Parse a string into a Locale
    *
    * @param locale the locale string
    * @return the parsed locale object
    */
  def parseLocale(locale: String): Locale = {

    val fromTag = Locale.forLanguageTag(locale)

    // unable to parse using forLanguageTag (i.e., Root locale returned), try using another method
    if (fromTag.equals(Locale.ROOT)) {
      Option(parseLocaleString(locale)) match {
        case Some(l) => l
        case None => throw LocaleNotFoundException(s"$locale could not be parsed into a valid Locale")
      }
    }
    else {
      fromTag
    }
  }

  /**
    * Exception wrapper for cases where locale cannot be determined
    *
    * @param msg the error message
    */
  case class LocaleNotFoundException(msg: String) extends RuntimeException(msg)

}
