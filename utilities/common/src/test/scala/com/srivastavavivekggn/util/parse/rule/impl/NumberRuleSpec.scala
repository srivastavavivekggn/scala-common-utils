package com.srivastavavivekggn.scala.util.parse.rule.impl

import com.srivastavavivekggn.scala.util.BaseUtilSpec
import com.srivastavavivekggn.scala.util.parse.rule.RuleHelpers

class NumberRuleSpec extends BaseUtilSpec with RuleHelpers {

  private val baseRule = NumberRule(AGE, YEAR)

  behavior of "NumberRule"

  it should "parse a numeric rule properly" in {
    val v = parse(baseRule, s"$AGE >= 1")
    assert(v == (AGE, ">=", 1))
  }

  it should "parse a numeric rule properly (extra spaces)" in {
    val v = parse(baseRule, s"$AGE       >=                    1")
    assert(v == (AGE, ">=", 1))
  }

  it should "parse a numeric rule properly (large number)" in {
    val v = parse(baseRule, s"$AGE <= ${Int.MaxValue}")
    assert(v == (AGE, "<=", Int.MaxValue))
  }

  it should "parse a rule when space is missing between operator and field" in {
    val v = parse(baseRule, s"$AGE>= 1")
    assert(v == (AGE, ">=", 1))
  }

  it should "fail to parse a numeric rule properly (larger than max int)" in {
    intercept[RuntimeException](
      parse(baseRule, s"$AGE <= 3147483647")
    )
  }

  it should "fail to parse a rule with a non-numeric value" in {
    intercept[RuntimeException](
      parse(baseRule, s"$AGE >= Cheese")
    )
  }

  it should "fail to parse a rule when a bad field is used" in {
    intercept[RuntimeException](
      parse(baseRule, s"${AGE.toLowerCase} >= Cheese")
    )
  }

  it should "fail to parse a rule when a bad operator is used" in {
    intercept[RuntimeException](
      parse(baseRule, s"$AGE x 1")
    )
  }

  it should "properly handle negative numbers" in {
    val v = parse(baseRule, s"$AGE >= -999")
    assert(v == (AGE, ">=", -999))
  }

  it should "properly evaluate rules against a context" in {
    def inContext[_: fastparse.P]: fastparse.P[Boolean] = baseRule.withContext(defaultContext)

    assertRule(inContext(_), s"$AGE > 17")
    assertRule(inContext(_), s"$AGE >= 18")
    assertRule(inContext(_), s"$AGE = 18")
    assertRule(inContext(_), s"$AGE != 17")
    assertRule(inContext(_), s"$AGE < 19")
    assertRule(inContext(_), s"$AGE <= 18")
  }

  it should "properly evaluate rules when field is missing from context" in {
    def inContext[_: fastparse.P]: fastparse.P[Boolean] = baseRule.withContext(defaultData.view.filterKeys(_ != YEAR).toMap)

    assertRule(inContext(_), s"$YEAR = 2", expected = false)
  }
}
