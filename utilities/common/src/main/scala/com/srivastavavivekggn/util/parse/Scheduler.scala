package com.srivastavavivekggn.scala.util.parse

import com.srivastavavivekggn.scala.util.lang.{DateUtils, StringUtils}
import fastparse.NoWhitespace.noWhitespaceImplicit
import fastparse._

import java.time.temporal.{TemporalAdjusters, TemporalAmount}
import java.time.{DayOfWeek, LocalDate, LocalDateTime, LocalTime, ZoneOffset}

case class Scheduler(offset: Option[ZoneOffset] = None, offsetString: Option[String] = None) {

  //  internal mapping of zone, utilizing whichever value was passed in
  private final val zone: Option[String] = offset.map(_.toString).orElse(offsetString)

  /**
    * Parse a scheduled date/time from a string
    */
  final def schedule[_: P]: P[LocalDateTime] = P(dayAndTime | dateMath | now)

  /**
    * Date keyword matching
    */
  final def dateKeyword[_: P]: P[LocalDateTime] = P(
    StringInIgnoreCase("now", "today", "tomorrow", "yesterday").!.map(_.toLowerCase).map {
      case "now" => DateUtils.zonedLocalDateTime(DateUtils.now, zone)
      case "today" => DateUtils.zonedLocalDate(DateUtils.now, zone).atStartOfDay()
      case "tomorrow" => DateUtils.zonedLocalDate(DateUtils.now, zone).atStartOfDay().plusDays(1)
      case "yesterday" => DateUtils.zonedLocalDate(DateUtils.now, zone).atStartOfDay().minusDays(1)
    }
  )

  /**
    * Capture date part as string
    */
  final def dateString[_: P]: P[String] = P(
    (digit.rep(exactly = 4) ~ StringUtils.Delimiters.DASH ~ digit.rep(exactly = 2) ~ StringUtils.Delimiters.DASH ~ digit.rep(exactly = 2)).!
  )

  /**
    * Capture time part as string
    */
  final def timeString[_: P]: P[String] = {
    // starts with optional space or 'T'
    P(("T" | space).? ~
      (
        digit.rep(exactly = 2) ~ ":" ~ // 2 digits followed by ":"
          digit.rep(exactly = 2) ~ ":" ~ // 2 digits followed by ":"
          digit.rep(exactly = 2)
        ).! ~ // capture
      (CharIn(".") ~ digit.rep).? // optional "." followed by numbers - we strip off this accuracy
      ~ "Z".? // optionally followed by a 'Z'
    )
  }

  // scalastyle:on magic.number

  /**
    * Extract temporal amount
    */
  final def temporalAmount[_: P]: P[TemporalAmount] = P(
    (CharIn("0-9").rep ~ space ~ StringIn("second", "minute", "hour", "day", "week", "month", "year") ~ "s".?).!.map(v => {
      DateUtils.Implicits.stringToTemporalAmount(v)
    })
  )

  /**
    * Extract LocalDate
    */
  final def localDate[_: P]: P[LocalDateTime] = P(dateString.!.map(LocalDate.parse).map(_.atStartOfDay()))

  /**
    * Extract LocalDateTime
    */
  final def localDateTime[_: P]: P[LocalDateTime] = P(
    (dateString ~ timeString).map(dt => LocalDateTime.parse(s"${dt._1}T${dt._2}"))
  )

  /**
    * Date math logic
    */
  final def dateMath[_: P]: P[LocalDateTime] = P(
    ((dateKeyword | localDateTime | localDate) ~ space ~ CharIn("\\+\\-").! ~ space ~ temporalAmount).map {
      case (temporal, "+", amount) => temporal.plus(amount)
      case (temporal, "-", amount) => temporal.minus(amount)
      case (temporal, _, _) => temporal
    }
  )

  /**
    * Any temporal capture
    */
  final def anyTemporal[_: P]: P[LocalDateTime] = P(dateMath | localDateTime | localDate | dateKeyword)

  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  //  INTERNAL Parsers
  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  /**
    * Keyword for 'now', no specific date or time
    */
  private def now[_: P]: P[LocalDateTime] = P(StringInIgnoreCase("now").!).map(_ => DateUtils.zonedLocalDateTime(DateUtils.now, zone))

  /**
    * A specific time or a range of times
    */
  private def scheduledTime[_: P]: P[(LocalTime, LocalTime)] = P(specificTime | betweenTimes)

  /**
    * Parse a specific time from string.
    *
    * if the given time has already passed today, this method returns tomorrow at the specified time
    */
  private def specificTime[_: P]: P[(LocalTime, LocalTime)] = P(StringInIgnoreCase("at").? ~ timeString).map(timeString => {
    val parsedTime = LocalTime.parse(timeString)
    (parsedTime, parsedTime)
  })

  /**
    * Between 2 times, if beyond the upper bound, it will add 1 day
    */
  private def betweenTimes[_: P]: P[(LocalTime, LocalTime)] = P(
    StringInIgnoreCase("between") ~ space.? ~ timeString ~ space.? ~ StringInIgnoreCase("and") ~ space.? ~ timeString
  ).map {
    case (lower, upper) =>
      val lowerBound = LocalTime.parse(lower)
      val upperBound = LocalTime.parse(upper)

      (lowerBound, upperBound)
  }

  /**
    * A specific day of the week -- if today is that day, we'll use today, otherwise get the 'next' upcoming day
    */
  private def dayOfWeek[_: P]: P[LocalDate] = P(StringInIgnoreCase("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday").!).map(d => {
    val now = DateUtils.zonedLocalDate(DateUtils.now, zone)

    // today is the target day
    if (now.getDayOfWeek.equals(DayOfWeek.valueOf(d.toUpperCase))) {
      now
    }
    else {
      now.`with`(TemporalAdjusters.next(DayOfWeek.valueOf(d.toUpperCase)))
    }
  })

  /**
    * Relative date (e.g., tomorrow)
    */
  private def relative[_: P]: P[LocalDate] = P(StringInIgnoreCase("tomorrow")).map(_ => DateUtils.zonedLocalDate(DateUtils.now, zone).plusDays(1))

  /**
    * Relative or explicit date and time
    */
  private def dayAndTime[_: P]: P[LocalDateTime] = P(
    (dateMath.map(_.toLocalDate) | dayOfWeek | relative | localDate.map(_.toLocalDate)).? ~ space.? ~ scheduledTime
  ).map {

    // both time and date
    case (Some(dt), (lowerBound, upperBound)) => (dt, (lowerBound, upperBound))

    // time only
    case (None, (lowerBound, upperBound)) => (DateUtils.zonedLocalDate(DateUtils.now, zone), (lowerBound, upperBound))

  }.map {
    case (dt, (lowerBound, upperBound)) =>
      val now = DateUtils.zonedLocalDateTime(DateUtils.now, zone)
      val nowDate = now.toLocalDate
      val nowTime = now.toLocalTime

      val baseDate = DateUtils.latestOf(nowDate, dt)

      // we are before the lower bound
      if (nowTime.isBefore(lowerBound)) {
        LocalDateTime.of(baseDate, lowerBound)
      }
      // we are after the upper bound, but the date is already beyond today
      else if (nowTime.isAfter(upperBound) && baseDate.isAfter(nowDate)) {
        LocalDateTime.of(baseDate, lowerBound)
      }
      // we are after the upper bound, need to add 1 day
      else if (nowTime.isAfter(upperBound)) {
        LocalDateTime.of(baseDate.plusDays(1), lowerBound)
      }
      // we are within the upper/lower bounds but the date is ahead of now, use lower bound
      else if (baseDate.isAfter(nowDate)) {
        LocalDateTime.of(baseDate, lowerBound)
      }
      // we are within the upper/lower bounds
      else {
        LocalDateTime.of(baseDate, nowTime)
      }
  }
}
