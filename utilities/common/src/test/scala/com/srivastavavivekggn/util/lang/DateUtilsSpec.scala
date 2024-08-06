package com.srivastavavivekggn.scala.util.lang

import java.time._
import java.time.temporal.{ChronoField, ChronoUnit, TemporalAmount}
import java.util.Date

import com.srivastavavivekggn.scala.util.BaseUtilSpec

class DateUtilsSpec extends BaseUtilSpec {

  behavior of "DateUtils"

  it must "get today's date truncated" in {
    val dt = DateUtils.toLocalDateTime(DateUtils.today)
    assert(dt.getLong(ChronoField.HOUR_OF_DAY) == 0, "Hour should be zero")
    assert(dt.getLong(ChronoField.MINUTE_OF_HOUR) == 0, "Minute should be zero")
    assert(dt.getLong(ChronoField.SECOND_OF_MINUTE) == 0, "Second should be zero")
    assert(dt.getLong(ChronoField.NANO_OF_SECOND) == 0, "Millis should be zero")
  }

  it must "get tomorrow's date truncated" in {
    val tomorrow = DateUtils.tomorrow
    val today = DateUtils.today
    assert(tomorrow.plus(-1, ChronoUnit.DAYS).compareTo(today) == 0)
  }

  it must "determine the next birthday after a certain date" in {

    val birth = LocalDate.of(1980, 5, 1)

    val test1 = DateUtils.nextBirthdayAfter(birth, LocalDate.of(2018, 4, 1))
    val test2 = DateUtils.nextBirthdayAfter(birth, LocalDate.of(2018, 5, 1))
    val test3 = DateUtils.nextBirthdayAfter(birth, LocalDate.of(2018, 5, 2))

    assert(test1.equals(LocalDate.of(2018, 5, 1)))
    assert(test2.equals(LocalDate.of(2018, 5, 1)))
    assert(test3.equals(LocalDate.of(2019, 5, 1)))
  }

  it must "calculate the average age" in {

    val birth = LocalDate.of(1970, 5, 1)

    val tests = Seq(
      // no change in age
      (LocalDate.of(2018, 1, 1), LocalDate.of(2018, 4, 1), 47),

      // 1 birthday during program, closer to end date
      (LocalDate.of(2017, 11, 1), LocalDate.of(2018, 7, 1), 47),

      // 1 birthday during program, closer to start date
      (LocalDate.of(2018, 2, 1), LocalDate.of(2019, 2, 1), 48),

      // multi-year program
      // 1/1/18 - 5/1/18: 47 -- 5 months
      // 5/1/18 to 5/1/19: 48 -- full year
      // 5/1/19 to 5/1/20: 49 -- full year
      // 5/1/20 to 5/1/21: 50 -- full year
      // 5/1/21 to 12/1/21: 51 -- 7 months
      (LocalDate.of(2018, 1, 1), LocalDate.of(2021, 12, 1), 49)
    )

    tests.foreach(t => {
      assert(DateUtils.averageAge(birth, t._1, t._2) == t._3)
    })
  }

  it must "truncate to year" in {
    val startOfYear = DateUtils.startOfYear.plusDays(10)
    val date = DateUtils.toLocalDateTime(DateUtils.today)
    assert(startOfYear.getLong(ChronoField.DAY_OF_YEAR) == 11, "count of days should be 11")
    assert(startOfYear.getYear == date.getYear)
  }


  it must "determine a valid temporal string" in {
    // good
    assert(DateUtils.isValidTemporalString("1 week"))
    assert(DateUtils.isValidTemporalString("6 months"))
    assert(DateUtils.isValidTemporalString(" 1 year "))
    assert(DateUtils.isValidTemporalString(" 1  "))
    assert(DateUtils.isValidTemporalString("14"))
    assert(DateUtils.isValidTemporalString("*"))

    // bad
    assert(!DateUtils.isValidTemporalString("1week"))
    assert(!DateUtils.isValidTemporalString("1 week extra"))
    assert(!DateUtils.isValidTemporalString(" 1.5"))
    assert(!DateUtils.isValidTemporalString("* years"))
  }

  it must "convert a string to a TemporalAmount implicitly" in {

    import DateUtils.Implicits._

    val a: Seq[TemporalAmount] = Seq(
      null.asInstanceOf[String], "2 weeks", "1 day", "1.5 years", "5 months", "2weeks", "7", Option(""), None, Some("1 hour"), Some("*")
    )

    assert(a(0).equals(Period.ZERO))
    assert(a(1).get(ChronoUnit.DAYS) == 14)
    assert(a(2).get(ChronoUnit.DAYS) == 1)
    assert(a(3).get(ChronoUnit.DAYS) == 0 && a(3).get(ChronoUnit.MONTHS) == 0 && a(3).get(ChronoUnit.YEARS) == 0)
    assert(a(4).get(ChronoUnit.DAYS) == 0 && a(4).get(ChronoUnit.MONTHS) == 5)
    assert(a(5).get(ChronoUnit.DAYS) == 0 && a(5).get(ChronoUnit.MONTHS) == 0 && a(5).get(ChronoUnit.YEARS) == 0)
    assert(a(6).get(ChronoUnit.DAYS) == 0 && a(6).get(ChronoUnit.MONTHS) == 7)
    assert(a(7).equals(Period.ZERO))
    assert(a(8).equals(Period.ZERO))
    assert(a(9).equals(Duration.ofHours(1)))
    assert(a(10).equals(Period.ofYears(999)))
  }

  it must "return the earliest date" in {

    val dates = List(
      LocalDate.parse("2017-01-01"), LocalDate.parse("2017-01-02"), LocalDate.parse("2018-03-04"), LocalDate.parse("2020-01-01")
    )

    val result = DateUtils.earliestOf(dates: _*)
    assert(result.equals(LocalDate.parse("2017-01-01")))
  }


  it should "properly get an instant" in {


    val d = LocalDate.parse("2017-12-01")

    val i = d.atStartOfDay(ZoneOffset.UTC).toInstant
    val i2 = d.atStartOfDay(ZoneId.systemDefault()).toInstant
    val i3 = d.atStartOfDay().toInstant(ZoneOffset.UTC)
    val i4 = d.atStartOfDay().toInstant(ZoneOffset.ofHours(0))


    val j1 = Date.from(i)
    val j2 = Date.from(i2)
    val j3 = Date.from(i3)
    val j4 = Date.from(i4)

    val x = 32
  }


  it must "parse a date string using multiple formats" in {

    assert(DateUtils.parseLocalDate("2018-01-01").equals(LocalDate.of(2018, 1, 1)))
    assert(DateUtils.parseLocalDate("02/02/2018").equals(LocalDate.of(2018, 2, 2)))
    assert(DateUtils.parseLocalDate("20180313").equals(LocalDate.of(2018, 3, 13)))

    assertThrows[RuntimeException] {
      DateUtils.parseLocalDate("01-01-2018")
    }
  }


  it must "properly look back using a temporal amount" in {

    val start = LocalDate.of(2018, 1, 1)

    assert(DateUtils.lookback(start, "1 month").equals(LocalDate.of(2017, 12, 1)))
    assert(DateUtils.lookback(start, "1 year").equals(LocalDate.of(2017, 1, 1)))
    assert(DateUtils.lookback(start, null).equals(LocalDate.of(2018, 1, 1)))
  }


  it must "properly look back using a specific date" in {

    val start = LocalDate.of(2018, 1, 1)

    assert(DateUtils.lookback(start, "2017-11-01").equals(LocalDate.of(2017, 11, 1)))
    assert(DateUtils.lookback(start, "02/02/2017").equals(LocalDate.of(2017, 2, 2)))
  }


  it must "properly determine the zoned instant" in {

    val start = LocalDate.of(2018, 2, 1)

    val startInstant = DateUtils.toInstant(start)
    assert(startInstant.toEpochMilli == 1517443200000L)

    val ny = DateUtils.toInstant(start, Some("America/New_York"))
    assert(ny.toEpochMilli == 1517461200000L)

    val chicago = DateUtils.toInstant(start, Some("America/Chicago"))
    assert(chicago.toEpochMilli == 1517464800000L)
  }

  it must "propertly determine the noon hour of the given day" in {

    val start = LocalDate.of(2018, 2, 1)

    // get midday instant
    val noon = DateUtils.toMiddayInstant(start)
    assert(DateUtils.toLocalDateTime(noon).get(ChronoField.HOUR_OF_DAY) == 12)

    // add 5 hours to noon and get midday instant again
    val j = DateUtils.toMiddayInstant(noon.plus(5, ChronoUnit.HOURS))
    assert(DateUtils.toLocalDateTime(j).get(ChronoField.HOUR_OF_DAY) == 12)
  }

  behavior of "DateUtils.getZoneId"

  it must "pass for all valid timezones and SHORT_IDS" in {
    assert(DateUtils.getZoneId("MST").getId.equalsIgnoreCase("-07:00"))
    assert(DateUtils.getZoneId("America/New_York") != null)
    assert(DateUtils.getZoneId("-05:00") != null)
  }

  it must "fail when an invalid timezone is passed" in {
    intercept[RuntimeException](
      DateUtils.getZoneId("bogus")
    )
  }

  behavior of "DateUtils.nextFirstOfMonth"

  it must "return the provided date when it is already the first" in {
    val result = DateUtils.nextFirstOfMonth(LocalDate.parse("2022-05-01"))
    assertResult(LocalDate.parse("2022-05-01"))(result)
  }

  it must "return the first of the next month (same year)" in {
    val result = DateUtils.nextFirstOfMonth(LocalDate.parse("2022-02-28"))
    assertResult(LocalDate.parse("2022-03-01"))(result)
  }

  it must "return the first of the next month (leap year)" in {
    val result = DateUtils.nextFirstOfMonth(LocalDate.parse("2020-02-29"))
    assertResult(LocalDate.parse("2020-03-01"))(result)
  }

  it must "return the first of the next month (next year)" in {
    val result = DateUtils.nextFirstOfMonth(LocalDate.parse("2022-12-02"))
    assertResult(LocalDate.parse("2023-01-01"))(result)
  }
}
