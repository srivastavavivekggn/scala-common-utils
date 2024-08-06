package com.srivastavavivekggn.scala.util.parse.rule.impl

import com.srivastavavivekggn.scala.util.BaseUtilSpec
import com.srivastavavivekggn.scala.util.parse.rule.RuleHelpers
import com.srivastavavivekggn.scala.util.parse.rule.impl.StringArrayRule._

class StringArrayRuleSpec extends BaseUtilSpec with RuleHelpers {

  private val baseRule = StringArrayRule(GENDER, ZIP)

  behavior of "StringArrayRule"

  it should "parse a string array rule" in {
    parse(baseRule, s"""$GENDER $FOUND_IN ["M", "F", "U"]""")
  }

  it should "fail to parse (bad array - missing quotes)" in {
    intercept[RuntimeException](
      parse(baseRule, s"""gender $FOUND_IN ["M", F, "U"]""")
    )
  }

  it should "fail to parse (bad array - missing bracket)" in {
    intercept[RuntimeException](
      parse(baseRule, s"""$GENDER $FOUND_IN ["M", F, "U" """)
    )
  }

  it should "fail to parse (bad operator)" in {
    intercept[RuntimeException](
      parse(baseRule, s"""$GENDER within ["M", "F", "U"]""")
    )
  }

  it should "fail to parse (bad field)" in {
    intercept[RuntimeException](
      parse(baseRule, s"""aGE $FOUND_IN ["M", "F", "U"]""")
    )
  }

  it should "evaluate successfully" in {

    def parser[_: fastparse.P]: fastparse.P[Boolean] = baseRule.withContext(defaultContext)

    assertRule(parser(_), s"""$GENDER $FOUND_IN ["F", "U"]""")
    assertRule(parser(_), s"""$GENDER $NOT_FOUND_IN ["M", "U"]""")
  }
}
