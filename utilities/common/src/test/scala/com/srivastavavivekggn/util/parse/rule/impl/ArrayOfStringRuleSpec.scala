package com.srivastavavivekggn.scala.util.parse.rule.impl

import com.srivastavavivekggn.scala.util.BaseUtilSpec
import com.srivastavavivekggn.scala.util.parse.rule.RuleHelpers
import com.srivastavavivekggn.scala.util.parse.rule.impl.ArrayOfStringRule.{HAS_ALL_OF, HAS_ONE_OF}
import fastparse.P

class ArrayOfStringRuleSpec extends BaseUtilSpec with RuleHelpers {

  private val baseRule = ArrayOfStringRule(BIOS, LABS)

  behavior of "ArrayOfStringRule"

  it should "parse an array of string rule" in {
    parse(baseRule, s"""$BIOS $HAS_ONE_OF ["a", "t", "W"]""")
  }

  it should "fail to parse (bad array - missing quote)" in {
    intercept[RuntimeException](
      parse(baseRule, s"""$BIOS $HAS_ONE_OF ["M", "F, "U"]""")
    )
  }

  it should "fail to parse (bad array - missing bracket)" in {
    intercept[RuntimeException](
      parse(baseRule, s"""$BIOS $HAS_ONE_OF ["M", F, "U" """)
    )
  }

  it should "fail to parse (bad operator)" in {
    intercept[RuntimeException](
      parse(baseRule, s"""$LABS found in ["M", "F", "U"]""")
    )
  }

  it should "fail to parse (bad field)" in {
    intercept[RuntimeException](
      parse(baseRule, s"""genDER $HAS_ONE_OF ["M", "F", "U"]""")
    )
  }

  it should "evaluate successfully" in {

    def parser[_: P]: P[Boolean] = baseRule.withContext(defaultContext)

    assertRule(parser(_), s"""$BIOS $HAS_ONE_OF ["b", "D"]""")
    assertRule(parser(_), s"""$LABS $HAS_ALL_OF ["x", "y", "z"]""")

    assertRule(parser(_), s"""$BIOS $HAS_ONE_OF ["Q", "D"]""", expected = false)
    assertRule(parser(_), s"""$LABS $HAS_ALL_OF ["x", "y", "t"]""", expected = false)
  }
}
