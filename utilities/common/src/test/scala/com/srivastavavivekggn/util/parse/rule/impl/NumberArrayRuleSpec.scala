package com.srivastavavivekggn.scala.util.parse.rule.impl

import com.srivastavavivekggn.scala.util.BaseUtilSpec
import com.srivastavavivekggn.scala.util.parse.rule.RuleHelpers
import com.srivastavavivekggn.scala.util.parse.rule.impl.NumberArrayRule._

class NumberArrayRuleSpec extends BaseUtilSpec with RuleHelpers {

  private val baseRule = NumberArrayRule(AGE, YEAR)

  behavior of "NumberArrayRule"

  it should "parse a number array rule" in {
    parse(baseRule, s"$AGE $FOUND_IN [1, 2, -3, 4]")
  }

  it should "fail to parse (bad array - missing number)" in {
    intercept[RuntimeException](
      parse(baseRule, s"$AGE $FOUND_IN [, 2, -3, 4]")
    )
  }

  it should "fail to parse (bad array - missing bracket)" in {
    intercept[RuntimeException](
      parse(baseRule, s"$AGE $FOUND_IN [1, 2, -3, 4")
    )
  }

  it should "fail to parse (bad operator)" in {
    intercept[RuntimeException](
      parse(baseRule, s"$AGE inside [1, 2, 3, 4]")
    )
  }

  it should "fail to parse (bad field)" in {
    intercept[RuntimeException](
      parse(baseRule, s"${AGE.toLowerCase} $FOUND_IN [1, 2, 3, 4]")
    )
  }

  it should "evaluate successfully" in {

    def parser[_: fastparse.P]: fastparse.P[Boolean] = baseRule.withContext(defaultContext)

    assertRule(parser(_), s"$AGE $FOUND_IN [17, 18, 19, 20]")
    assertRule(parser(_), s"$AGE $FOUND_IN [17, 18, 19, 20]")
    assertRule(parser(_), s"$AGE $NOT_FOUND_IN [22, 23, 24, 25]")
    assertRule(parser(_), s"$AGE $NOT_FOUND_IN [22, 23, 24, 25]")
  }
}
