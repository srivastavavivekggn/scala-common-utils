package com.srivastavavivekggn.scala.util.lang

import java.time._
import java.time.format.{DateTimeFormatter, DateTimeParseException}
import java.time.temporal.ChronoUnit.{DAYS, MONTHS}
import java.time.temporal._
import java.util.Date
import java.util.regex.Pattern

import com.srivastavavivekggn.scala.util.TypeAlias.JLong


/**
  * Helper interface for manipulating dates based on Joda Time library.
  *
  * By default, this uses UTC.
  *
  */
// scalastyle:off number.of.methods
trait DateUtils {

  /**
    * The timezone this util is operating on
    *
    * @return the time zone
    */
  protected def offset: Option[ZoneId] = Some(ZoneOffset.UTC)

  /**
    * Get the instant right now
    *
    * @return the instant now
    */
  def now: Instant = offset match {
    case Some(tz) => Instant.now(Clock.system(tz))
    case _ => Instant.now()
  }

  /**
    * Get the local date now
    *
    * @return the local date
    */
  def localDateNow: LocalDate = offset match {
    case Some(tz) => LocalDate.now(Clock.system(tz))
    case _ => LocalDate.now()
  }

  /**
    * Get the local date and time now
    *
    * @return the local date and time now
    */
  def localDateTimeNow: LocalDateTime = offset match {
    case Some(tz) => LocalDateTime.now(Clock.system(tz))
    case _ => LocalDateTime.now()
  }

  /**
    * Get an instant from the provided millis long
    *
    * @param millis the epoch millis
    * @return the instant
    */
  def fromEpochMilli(millis: JLong): Instant = {
    Instant.ofEpochMilli(millis)
  }

  /**
    * Get the millis long from a LocalDateTime
    *
    * @param localDateTime the LocalDateTime
    * @return the millis long
    */
  def toEpochMilli(localDateTime: LocalDateTime): JLong = {
    localDateTime.toInstant(ZoneOffset.UTC).toEpochMilli
  }

  def localDateTimeFromEpochMilli(millis: JLong): LocalDateTime = {
    LocalDateTime.ofInstant(fromEpochMilli(millis), offset.getOrElse(ZoneId.systemDefault()))
  }

  def localDateFromEpochMilli(millis: JLong): LocalDate = {
    localDateTimeFromEpochMilli(millis).toLocalDate
  }

  /**
    * Get the current date as a java date
    *
    * @return the java date
    */
  def javaDateNow: Date = new Date()

  /**
    * Get the DateTime for the start of today
    *
    * @return the datetime today (zero hour, minute, second)
    */
  def today: Instant = now.truncatedTo(ChronoUnit.DAYS)

  /**
    * Get the DateTime for the start of tomorrow
    *
    * @return the datetime tomorrow (zero hour, minute, second)
    */
  def tomorrow: Instant = today.plus(1, ChronoUnit.DAYS)

  /**
    * Truncate to year
    *
    * @return
    */
  def startOfYear: LocalDate = localDateNow.`with`(ChronoField.DAY_OF_YEAR, 1)

  /**
    * Start of month
    *
    * @return the date representing the start of the now month
    */
  def startOfMonth: LocalDate = localDateNow.`with`(ChronoField.DAY_OF_MONTH, 1)

  /**
    * Get the next first-of-the-month starting with the given dt
    *
    * @param dt the dt
    * @return the provided dt if it is already the 1st of the month, otherwise the first of the next month
    */
  def nextFirstOfMonth(dt: LocalDate): LocalDate = if (dt.getDayOfMonth == 1) {
    dt
  }
  else {
    dt.withDayOfMonth(1).plusMonths(1)
  }

  /**
    * Return any day in any given week
    *
    * @param dt    the localDate
    * @param day   the day of the week
    * @param wkNum the week number
    * @return the date
    */
  def dayOfWeekLocalDate(dt: LocalDate, day: DayOfWeek, wkNum: Int): LocalDate = {
    dt.`with`(WeekFields.of(day, 7).getFirstDayOfWeek).plusWeeks(wkNum)
  }

  /**
    * Convert an instant to a local date/time
    *
    * @param i the instant
    * @return the local datetime
    */
  def toLocalDateTime(i: Instant): LocalDateTime = offset match {
    case Some(tz) => LocalDateTime.ofInstant(i, tz)
    case _ => LocalDateTime.ofInstant(i, ZoneId.systemDefault())
  }

  /**
    * Convert an instant to a local date
    *
    * @param i the instant
    * @return the local date
    */
  def toLocalDate(i: Instant): LocalDate = toLocalDateTime(i).toLocalDate

  /**
    * Convert a local date to an instant plus 12 hours
    *
    * @param ld
    * @return the instant adding (12 hour, zero minute, zero second)
    */
  def toMiddayInstant(ld: LocalDate): Instant = toMiddayInstant(toInstant(ld))

  /**
    * Set the time of day for the given instant to noon
    *
    * @param i the instant
    * @return the instant with adjusted hour to noon
    */
  // scalastyle:off magic.number
  def toMiddayInstant(i: Instant): Instant = i.truncatedTo(ChronoUnit.DAYS).plus(12, ChronoUnit.HOURS)

  // scalastyle:on magic.number

  /**
    * Get the zoneId and add support for SHORT_IDS
    * ex MST/HST/EST
    *
    * @param zone the zone
    * @return the zone id
    */
  def getZoneId(zone: String): ZoneId = if (ZoneId.SHORT_IDS.containsKey(zone)) {
    ZoneId.of(ZoneId.SHORT_IDS.get(zone))
  } else {
    ZoneId.of(zone)
  }


  /**
    * Convert a local date to an instant
    *
    * @param ld the local dae
    * @return the instant
    */
  def toInstant(ld: LocalDate): Instant = ld.atStartOfDay(offset.getOrElse(ZoneId.systemDefault())).toInstant

  /**
    * Convert a local date to an instant in the given timezone
    * @param ld the local date
    * @param timezone the time zone
    * @return the instant
    */
  def toInstant(ld: LocalDate, timezone: Option[String]): Instant = {
    val z = timezone.map(getZoneId).orElse(offset)
    ld.atStartOfDay(z.getOrElse(ZoneId.systemDefault())).toInstant
  }

  /**
    * Get a java date from the given local date
    *
    * @param ld the local date
    * @return the java date
    */
  def toJavaDate(ld: LocalDate): Date = Date.from(toInstant(ld))

  /**
    * Format the given date using the provided formatter
    *
    * @param dt     the temporal
    * @param fmt    the format
    * @return
    */
  def format(dt: Temporal = now, fmt: DateTimeFormatter = DateUtils.YMD_FORMAT): String = fmt.format(dt)

  /**
    * Format an instant
    * @param i the instant
    * @return the formatted instant
    */
  def formatInstant(i: Instant): String = format(i, DateTimeFormatter.ISO_INSTANT)

  /**
    * Try parsing a local date using the 3 pre-defined formats
    *
    * @param dateString the date string to parse
    * @return
    */
  def parseLocalDate(dateString: String): LocalDate = {

    val formats = Seq(
      DateTimeFormatter.ISO_LOCAL_DATE,
      DateUtils.SLASHES_FORMAT,
      DateUtils.SHORT_SLASHES_FORMAT,
      DateUtils.YMD_FORMAT
    )

    val start: Option[LocalDate] = None

    formats.foldLeft(start)((localDate, format) => {
      localDate.orElse({
        try {
          Option(LocalDate.parse(dateString, format))
        }
        catch {
          case d: DateTimeParseException => None
        }
      })
    }) match {
      case Some(dt) => dt
      case _ => throw new RuntimeException(s"Unrecognized Date Format: $dateString")
    }
  }

  /**
    * Calculate the rough-average age of a person between 2 dates
    *
    * By rough-average, we mean if it's 6 months or greater we add 1 year
    *
    * @param birthdate the birth date
    * @param startDate the start date
    * @param endDate   the end date
    * @return the average age
    */
  def averageAge(birthdate: LocalDate, startDate: LocalDate, endDate: LocalDate): Int = {

    val ageAtStart = Period.between(birthdate, startDate).getYears
    val ageAtEnd = Period.between(birthdate, endDate).getYears

    // if member is the same age for the entire program
    if (ageAtStart == ageAtEnd) {
      ageAtStart
    }
    // if this program lasts more than 1 year, you could have more than 1 birthday
    else if (DAYS.between(startDate, endDate) >= 366) {

      val daysInProgram = DAYS.between(startDate, endDate)

      // determine the age for each day of the program
      val totalYears: Double = (0 to DAYS.between(startDate, endDate).toInt)
        .map(d => Period.between(birthdate, startDate.plusDays(d)).getYears.toDouble)
        .sum

      // determine avg age during program
      val averageAge: Double = totalYears / daysInProgram

      // round
      Math.round(averageAge).toInt
    }
    // your age changes during the program at most once
    else {
      val nextBday = nextBirthdayAfter(birthdate, startDate)

      if (DAYS.between(startDate, nextBday) > DAYS.between(nextBday, endDate)) {
        ageAtStart
      }
      else {
        ageAtEnd
      }
    }
  }


  /**
    * Get the next birthday after a certain date
    *
    * @param birthdate the birthdate
    * @param dt        the date to match against
    * @return the birthday that falls after the given date
    */
  def nextBirthdayAfter(birthdate: LocalDate, dt: LocalDate): LocalDate = {

    if (birthdate.withYear(dt.getYear).isBefore(dt)) {
      birthdate.withYear(dt.getYear + 1)
    }
    else {
      birthdate.withYear(dt.getYear)
    }
  }

  /**
    * Calculate the number of days between start and end
    *
    * @param start the start date
    * @param end   the end date
    * @return the number of days between
    */
  def daysBetween(start: LocalDate, end: LocalDate): Int = {
    DAYS.between(start, end).toInt
  }

  /**
    * Get the number of months between 2 dates
    *
    * @param start the start date
    * @param end   the end date
    * @return the number of months
    */
  def monthsBetween(start: LocalDate, end: LocalDate): Int = {
    MONTHS.between(start, end).toInt
  }

  /**
    * Find the earliest date in a set of dates
    *
    * @param dt the set of dates
    * @return the earliest non-null date
    */
  def earliestOf(dt: LocalDate*): LocalDate = {
    dt.filterNot(_ == null).sortWith((a, b) => a.isBefore(b)).head
  }

  /**
    * Find the lastest date in a set of dates
    *
    * @param dt the set of dates
    * @return the latest non-null date
    */
  def latestOf(dt: LocalDate*): LocalDate = {
    dt.filterNot(_ == null).sortWith((a, b) => a.isAfter(b)).head
  }

  /**
    * Determine if the given date is between the start (inclusive) and end (exclusive) dates
    *
    * @param dt    the dt
    * @param start the start
    * @param end   the end
    * @return true if the date falls between the start and end
    */
  def isBetween(dt: LocalDate, start: LocalDate, end: LocalDate): Boolean = {
    !dt.isBefore(start) && dt.isBefore(end)
  }

  /**
    * Determine if the given date is between the start (inclusive) and end (exclusive) OPTION dates
    *
    * If a date is empty we assume true for that part
    *
    * @param dt    the date to check
    * @param start the start date
    * @param end   the end date
    * @return true if the given date is between the start/end dates
    */
  def isBetween(dt: LocalDate, start: Option[LocalDate], end: Option[LocalDate]): Boolean = {
    isBetween(dt, start.getOrElse(dt), end.getOrElse(dt.plusDays(1)))
  }

  /**
    * Lookback from the given baseDate either a specified temporal amount or to a specific date
    *
    * @param baseDate       the base date
    * @param temporalOrDate the temporal amount or specific date
    * @return the determined date
    */
  def lookback(baseDate: LocalDate, temporalOrDate: String): LocalDate = {
    if (StringUtils.isEmpty(temporalOrDate)) {
      baseDate
    }
    else if (DateUtils.isValidTemporalString(temporalOrDate)) {
      baseDate.minus(DateUtils.Implicits.stringToTemporalAmount(temporalOrDate))
    }
    else {
      parseLocalDate(temporalOrDate)
    }
  }
  /**
    * Get a zoned local date from the given instant and zone
    *
    * @param i    the instant
    * @param zone the zone
    * @return the local date
    */
  def zonedLocalDate(i: Instant, zone: Option[String]): LocalDate = {
    zonedLocalDateTime(i, zone).toLocalDate
  }

  /**
    * Get a zoned local datetime from the given instant
    *
    * @param i    the instant
    * @param zone the zone
    * @return the LocalDateTime
    */
  def zonedLocalDateTime(i: Instant, zone: Option[String]): LocalDateTime = {
    val tz = zone.map(getZoneId).orElse(offset)

    tz match {
      case Some(z) => i.atZone(z).toLocalDateTime
      case _ => toLocalDateTime(i)
    }
  }
}

/**
  * DateUtils object - defaults to current JVM timezone
  */
object DateUtils extends DateUtils {

  final val DEFAULT_TIMEZONE = "UTC" // TODO: move to configuration somewhere

  /**
    * formats/parses as yyyyMMdd
    */
  final val YMD_FORMAT = DateTimeFormatter.BASIC_ISO_DATE

  /**
    * formats/parses as MM/dd/yyyy
    */
  final val SLASHES_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy")

  /**
    * formats/parses as M/d/y
    */
  final val SHORT_SLASHES_FORMAT = DateTimeFormatter.ofPattern("M/d/yy")

  /**
    * formats as MMM d, yyyy   e.g, Feb 3, 2014
    */
  final val SHORT_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy")

  /**
    * Pattern for checking temporal strings
    */
  final lazy val TEMPORAL_STRING = Pattern.compile("^(?<amount>\\d+)(\\s+(?<unit>second|minute|hour|day|week|month|year)s?)?$")

  /**
    * Wildcard value for temporal strings
    */
  final val TEMPORAL_WILDCARD = "*"

  /**
    * DateUtil object using UTC time zone
    */
  final object UTC extends DateUtils {
    override protected val offset = Some(ZoneOffset.UTC)
  }

  /**
    * DateUtil object using the default time zone
    */
  final object LOCAL extends DateUtils {
    override protected val offset = Some(ZoneId.systemDefault())
  }


  /**
    * Is the given string a valid temporal string (e.g., 1 year, 30 days, etc.)
    *
    * @param str the string to test
    * @return true if it's a valid temporal, false otherwise
    */
  final def isValidTemporalString(str: String): Boolean = {
    Option(str).map(_.toLowerCase).exists(s => TEMPORAL_WILDCARD.equals(s) || TEMPORAL_STRING.matcher(s.trim).matches())
  }


  /**
    * Break a temporal string into it's components (quantity and unit)
    *
    * @param stringVal the temporal string
    * @return the quantity and unit
    */
  final def toTemporalComponents(stringVal: String): Option[(Int, String)] = {

    // if wildcard, then we won't return anything
    if (TEMPORAL_WILDCARD.equals(stringVal)) {
      None
    }
    // otherwise, use the temporal regex matcher
    else {
      val matcher = TEMPORAL_STRING.matcher(stringVal.toLowerCase)
      matcher.find()

      Some((
        Option(matcher.group("amount")).map(Integer.parseInt).getOrElse(0),
        Option(matcher.group("unit")).getOrElse("month")
      ))
    }
  }

  object Implicits {

    /**
      * Implicitly convert an Option[String] into a temporal amount
      *
      * @param stringOpt the option[string]
      * @return the temporal amount
      */
    implicit def stringOptToTemporalAmount(stringOpt: Option[String]): TemporalAmount = stringToTemporalAmount(stringOpt.orNull)

    /**
      * Implicitly converts a string into a temporal amount
      *
      * @param stringVal the string value to convert
      * @return the temporal amount
      */
    implicit def stringToTemporalAmount(stringVal: String): TemporalAmount = {

      if (isValidTemporalString(stringVal)) {

        // if wildcard, then we'll fake a large period config
        val (amount, unit) = toTemporalComponents(stringVal).getOrElse((999, "year"))

        unit match {
          case "second" => Duration.ofSeconds(amount)
          case "minute" => Duration.ofMinutes(amount)
          case "hour" => Duration.ofHours(amount)
          case "day" => Period.ofDays(amount)
          case "week" => Period.ofWeeks(amount)
          case "month" => Period.ofMonths(amount)
          case "year" => Period.ofYears(amount)
          case _ => Period.ZERO
        }
      }
      else {
        Period.ZERO
      }
    }


    /**
      * Enables sorting by localdate
      */
    implicit val localDateOrdering: Ordering[LocalDate] = (x: LocalDate, y: LocalDate) => x.compareTo(y)

    /**
      * Enhance the localDate class to add some extra methods
      *
      * @param localDate the local date to wrap
      */
    implicit class RichLocalDate(localDate: LocalDate) {

      def isBeforeOrOn(dt: LocalDate): Boolean = localDate.isBefore(dt) || localDate.isEqual(dt)

      def isAfterOrOn(dt: LocalDate): Boolean = localDate.isAfter(dt) || localDate.isEqual(dt)
    }

  }

}
// scalastyle:on number.of.methods
