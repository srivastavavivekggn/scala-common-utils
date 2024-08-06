package com.srivastavavivekggn.scala.util.parse.rule.impl

import com.srivastavavivekggn.scala.util.BaseUtilSpec
import com.srivastavavivekggn.scala.util.lang.DateUtils
import com.srivastavavivekggn.scala.util.parse.rule.RuleHelpers

import java.time.{LocalDate, LocalDateTime}

class TemporalRuleSpec extends BaseUtilSpec with RuleHelpers {

  private val baseRule = TemporalRule(START, END)

  // scalastyle:off magic.number
  private val localDateTime = LocalDateTime.of(2020, 2, 14, 14, 33, 22)
  private val localDate = LocalDate.of(2020, 2, 14)
  // scalastyle:on magic.number

  behavior of "TemporalRule"

  it should "parse a rule with a keyword" in {
    Seq("now", "ToDay", "tomorRoW", "Yesterday").foreach(keyword => {
      val v = parse(baseRule, s"$START < $keyword")
      assert(DateUtils.isBetween(v._3.toLocalDate, DateUtils.localDateNow.minusDays(1), DateUtils.localDateNow.plusDays(2)))
    })
  }

  it should "parse a rule with a fixed date" in {
    val v = parse(baseRule, s"$START < $localDate")
    assert(v._1 == START)
    assert(v._3.toLocalDate.equals(localDate))
  }

  it should "parse a rule with a fixed date and time (T separator)" in {
    val v = parse(baseRule, s"$START < $localDateTime")
    assert(v._3.equals(localDateTime))
  }

  it should "parse a rule with a fixed date and time (space separator)" in {
    val v = parse(baseRule, s"$START < ${localDateTime.toString.replace("T", " ")}")
    assert(v._3.equals(localDateTime))
  }

  it should "parse a rule with a fixed date and time (Z after)" in {
    val v = parse(baseRule, s"$START < ${localDateTime.toString}Z")
    assert(v._3.equals(localDateTime))
  }

  it should "parse a rule with a keyword and date math" in {
    val v = parse(baseRule, s"$START < now + 3 days")
    assert(v._1 == START)
    assert(v._2 == "<")
    assert(v._3.toLocalDate.equals(DateUtils.localDateTimeNow.plusDays(3).toLocalDate))
  }

  it should "parse a rule with a fixed date and date math" in {
    val v = parse(baseRule, s"$START < $localDate - 2 years")
    assert(v._3.toLocalDate.equals(localDate.minusYears(2)))
  }

  it should "parse a rule with a fixed datetime and date math" in {
    val v = parse(baseRule, s"$START < $localDateTime - 1 month")
    assert(v._3.equals(localDateTime.minusMonths(1)))
  }

  it should "evaluate successfully" in {

    def parser[_: fastparse.P]: fastparse.P[Boolean] = baseRule.withContext(defaultContext)

    // numeric operators
    assertRule(parser(_), s"$START < $localDate")
    assertRule(parser(_), s"$START != $localDate")
    assertRule(parser(_), s"$START <= $localDate")
    assertRule(parser(_), s"$START > 2019-01-01")
    assertRule(parser(_), s"$START >= 2020-01-21T10:40:00")
    assertRule(parser(_), s"$START = 2020-02-01")

    // text operators
    assertRule(parser(_), s"$START before $localDate")
    assertRule(parser(_), s"$START not equal to $localDate")
    assertRule(parser(_), s"$START before or equal to $localDate")
    assertRule(parser(_), s"$START after 2019-01-01")
    assertRule(parser(_), s"$START after or equal to 2020-01-21T10:40:00")
    assertRule(parser(_), s"$START equal to 2020-02-01")

    assertRule(parser(_), s"$START > 2020-02-01", expected = false)
    assertRule(parser(_), s"$START after 2020-02-01", expected = false)
  }
}
