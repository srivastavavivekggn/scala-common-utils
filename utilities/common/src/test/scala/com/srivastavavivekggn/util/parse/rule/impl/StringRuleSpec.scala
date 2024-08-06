package com.srivastavavivekggn.scala.util.parse.rule.impl

import com.srivastavavivekggn.scala.util.BaseUtilSpec
import com.srivastavavivekggn.scala.util.parse.rule.RuleHelpers
import StringRule._

class StringRuleSpec extends BaseUtilSpec with RuleHelpers {

  private val baseRule = StringRule(GENDER, ZIP, NAME, "meta..*")

  behavior of "StringRule"

  it should "parse a string rule (no quotes)" in {
    val v = parse(baseRule, s"$GENDER $IS F")
    assert(v == (GENDER, IS, "F"))
  }

  it should "parse a string rule (with quotes)" in {
    val v = parse(baseRule, s"""$ZIP $STARTS "970"""")
    assert(v == (ZIP, STARTS, "970"))
  }

  it should "fail to parse (bad operator)" in {
    intercept[RuntimeException](
      parse(baseRule, s"$ZIP includes 070")
    )
  }

  it should "fail to parse (bad field)" in {
    intercept[RuntimeException](
      parse(baseRule, s"aGe $IS F")
    )
  }

  it should "parse strings with special characters" in {
    val v = parse(baseRule, s"$ZIP $IS 'b#sl_23'")
    assert(v == (ZIP, IS, "b#sl_23"))
  }

  it should "fail to parse (invalid string characters)" in {
    intercept[RuntimeException](
      parse(baseRule, s"$ZIP $IS 'b#sl)23'")
    )
  }

  it should "evaluate successfully" in {

    def parser[_: fastparse.P]: fastparse.P[Boolean] = baseRule.withContext(defaultContext)

    val result = fastparse.parse(s"$ZIP $IS 07030", parser(_))

    assertRule(parser(_), s"$ZIP $IS 07030")
    assertRule(parser(_), s"$ZIP $IS_NOT 97030")
    assertRule(parser(_), s"$ZIP $STARTS 070")
    assertRule(parser(_), s"$ZIP $ENDS 030")
    assertRule(parser(_), s"$ZIP $CONTAINS 703")

    assertRule(parser(_), s"""$ZIP $IS "07030" """)
    assertRule(parser(_), s"""$ZIP $IS_NOT "97030" """)
    assertRule(parser(_), s"""$ZIP $STARTS "070" """)
    assertRule(parser(_), s"""$ZIP $ENDS "030" """)
    assertRule(parser(_), s"""meta.$ZIP $CONTAINS "703" """)
  }
}
