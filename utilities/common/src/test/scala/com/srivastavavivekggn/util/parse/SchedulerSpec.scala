package com.srivastavavivekggn.scala.util.parse

import com.srivastavavivekggn.scala.util.BaseUtilSpec
import com.srivastavavivekggn.scala.util.lang.DateUtils

import java.time.{DayOfWeek, LocalDateTime, LocalTime}

class SchedulerSpec extends BaseUtilSpec {

  behavior of "Scheduler"

  val now = DateUtils.localDateTimeNow.withNano(0)
  val dateNow = now.toLocalDate
  val timeNow = now.toLocalTime
  val beforeNow = timeNow.minusHours(1)
  val afterNow = timeNow.plusHours(1)
  val dayOfWeek = now.getDayOfWeek
  val testTime: LocalTime = LocalTime.of(1, 14, 43)

  val scheduler = Scheduler()

  def parseLocalDateTime(ruleString: String): LocalDateTime = {
    fastparse.parse(ruleString, scheduler.schedule(_)) match {
      case fastparse.Parsed.Success(v, _) => v
      case _ => throw new RuntimeException("No date parsed")
    }
  }

  it should "parse a date/time using 'now' keyword" in {
    val result = parseLocalDateTime("Now")

    // validate we are approximately close to 'now'
    assert(now.minusMinutes(1).isBefore(result) && now.plusMinutes(1).isAfter(result))
  }

  it should "parse a date/time using 'tomorrow' keyword" in {
    val result = parseLocalDateTime(s"TOMorrow at $testTime")

    assert(result.toLocalDate.equals(DateUtils.localDateNow.plusDays(1)))
    assert(result.toLocalTime.equals(testTime))
  }

  it should "parse a date/time using 'now' keyword and date math (minutes)" in {
    val result = parseLocalDateTime("Now + 10 minutes")

    // validate we are approximately close to 'now'
    assert(now.plusMinutes(9).isBefore(result) && now.plusMinutes(11).isAfter(result))
  }

  it should "parse a date/time using 'now' keyword and date math (months)" in {
    val result = parseLocalDateTime("Now + 1 month")

    // validate we are approximately close to 'now'
    assert(now.plusDays(32).isAfter(result) && now.plusDays(27).isBefore(result))
  }

  it should "parse a date/time using day of week" in {
    val result = parseLocalDateTime(s"FriDay at 23:59:59")
    assert(result.getDayOfWeek.equals(DayOfWeek.FRIDAY))

    val result2 = parseLocalDateTime(s"tuesday at 23:59:59")
    assert(result2.getDayOfWeek.equals(DayOfWeek.TUESDAY))

    val result3 = parseLocalDateTime(s"${dayOfWeek.toString} at ${afterNow.toString}")
    assert(result3.toLocalDate.equals(dateNow))
    assert(result3.toLocalTime.equals(afterNow))

    val result4 = parseLocalDateTime(s"${dayOfWeek.toString} at ${beforeNow.toString}")
    assert(result4.toLocalDate.isAfter(dateNow))
    assert(result4.toLocalTime.equals(beforeNow))
  }

  it should "parse a date/time using 'at time' (not yet passed)" in {
    val time = timeNow.plusMinutes(15)
    val result = parseLocalDateTime(s"at $time")

    // since the time has passed, it should be tomorrow
    assert(result.toLocalDate.equals(DateUtils.localDateNow))
    assert(result.toLocalTime.equals(time))
  }

  it should "parse a date/time using 'at time' (already passed)" in {

    val time = timeNow.minusSeconds(2)
    val result = parseLocalDateTime(s"at $time")

    // since the time has passed, it should be tomorrow
    assert(result.toLocalDate.equals(DateUtils.localDateNow.plusDays(1)))
    assert(result.toLocalTime.equals(time))
  }

  it should "parse a date/time using time only" in {
    val result = parseLocalDateTime(testTime.toString)
    assert(result.toLocalTime.equals(testTime))
  }

  it should "parse a time using 'between' keyword" in {

    val timeNow = DateUtils.localDateTimeNow.toLocalTime.withNano(0)
    val justBeforeNow = timeNow.minusMinutes(1)
    val beforeTime = timeNow.minusHours(1)
    val afterTime = timeNow.plusHours(1)

    val result1 = parseLocalDateTime(s"between ${beforeTime.toString} and ${afterTime.toString}")
    assert(result1.toLocalDate.equals(DateUtils.localDateNow))
    assert(result1.toLocalTime.isBefore(afterTime) && result1.toLocalTime.isAfter(beforeTime))

    val result2 = parseLocalDateTime(s"between ${beforeTime.toString} and ${justBeforeNow.toString}")
    assert(result2.toLocalDate.equals(DateUtils.localDateNow.plusDays(1)))
    assert(result2.toLocalTime.equals(beforeTime))
  }

  it should "parse a date and time using 'between' keyword and day of week" in {
    val tommorrow = DateUtils.localDateNow.plusDays(2).getDayOfWeek
    val result = parseLocalDateTime(s"${tommorrow.toString} between ${beforeNow.toString} and ${afterNow.toString}")

    assert(result.toLocalDate.equals(dateNow.plusDays(2)))
    assert(result.toLocalTime.equals(beforeNow))
  }

  it should "parse a date and time using date math and a time range" in {
    val result = parseLocalDateTime(s"now + 7 days between 09:00:00 and 21:00:00")
    assert(result.toLocalDate.equals(dateNow.plusDays(7)))
    assert(result.toLocalTime.equals(LocalTime.of(9, 0, 0)))
  }

  it should "parse a date and time using date math and a specific time" in {
    val result = parseLocalDateTime(s"now + 1 month at 10:00:00")
    assert(result.toLocalDate.equals(dateNow.plusMonths(1)))
    assert(result.toLocalTime.equals(LocalTime.of(10, 0, 0)))
  }
}
