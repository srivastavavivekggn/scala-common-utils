package com.srivastavavivekggn.scala.util.parse.rule

import com.srivastavavivekggn.scala.util.BaseUtilSpec
import com.srivastavavivekggn.scala.util.parse.rule.impl.{ArrayOfStringRule, ExistenceRule, NumberArrayRule, NumberRule, StringArrayRule, StringRule}
import fastparse._

class RuleSetSpec extends BaseUtilSpec with RuleHelpers {

  behavior of "RuleSet"

  private val numberRule = NumberRule(AGE)
  private val numberArrayRule = NumberArrayRule(YEAR)
  private val stringRule = StringRule(GENDER, ZIP)
  private val stringArrayRule = StringArrayRule(GENDER)
  private val arrayOfStringRule = ArrayOfStringRule(BIOS, LABS)
  private val existenceRule = ExistenceRule(MONTH, YEAR)

  private def defaultRuleset: RuleSet = RuleSet(
    defaultContext, numberRule, stringRule, numberArrayRule, stringArrayRule, arrayOfStringRule, existenceRule
  )

  it should "parse a simple rule set (1 rules)" in {
    assert(defaultRuleset.evaluate(s"$AGE >= 18"))
    assert(defaultRuleset.evaluate(s"$GENDER is F"))
    assert(defaultRuleset.evaluate(s"$YEAR found in [1969, 1970, 1971]"))
    assert(defaultRuleset.evaluate(s"$BIOS has one of [a, z]"))
    assert(defaultRuleset.evaluate(s"$MONTH does not exist"))
  }

  it should "parse a simple rule set (2 rules)" in {
    val result = defaultRuleset.evaluate(s"$AGE >= 18 AND $AGE < 100")
    assert(result)
  }

  it should "parse a simple rule set (2 rules with parenthesis)" in {
    assert(defaultRuleset.evaluate(s"($AGE >= 18 AND $GENDER is F)"))
  }

  it should "parse a simple rule set (3 rules)" in {
    assert(defaultRuleset.evaluate(s"$AGE >= 18 AND $AGE < 99 AND $GENDER is F"))
  }

  it should "parse a simple rule set (3 rules with parenthesis)" in {
    assert(defaultRuleset.evaluate(s"($AGE >= 18 AND $AGE < 99) OR $GENDER is M"))
  }

  it should "parse a simple rule set (4 rules with parenthesis)" in {
    assert(defaultRuleset.evaluate("(AGE >= 17 AND GENDER is F) OR (AGE < 44 AND GENDER is M)"))
  }

  it should "parse nested complex rules" in {
    val rule = s"""$MONTH does not exist AND ($YEAR found in [1969, 1970, 1971] OR $AGE < 18) AND ($GENDER is M OR ($GENDER is F AND $BIOS has one of [a]))"""
    assert(defaultRuleset.evaluate(rule))

    // failing on Bios
    val rule2 = s"""($YEAR found in [1969, 1970, 1971] OR $AGE < 18) AND $MONTH does not exist AND ($GENDER is M OR ($GENDER is F AND $BIOS has one of [z]))"""
    assert(!defaultRuleset.evaluate(rule2))

    // failing on Year
    val rule3 = s"""($YEAR found in [1969, 1971] OR $AGE < 18) AND ($GENDER is M OR ($GENDER is F AND $BIOS has one of [a])) AND $MONTH does not exist"""
    assert(!defaultRuleset.evaluate(rule3))
  }

  it should "fail to parse when a bad join word is used" in {
    intercept[RuntimeException](
      defaultRuleset.evaluate(s"$AGE >= 18 X $AGE < 99")
    )
  }

  it should "handle a multi-line ruleset" in {
    val rule =
      s"""
         |$MONTH does not exist
         |  AND (
         |    $YEAR found in [1969,
         |                 1970,
         |                 1971
         |                ] OR $AGE < 18
         |  )
         |  AND (
         |    $GENDER is M OR (
         |        $GENDER is F AND $BIOS has one of [a]
         |    )
         |)
         |""".stripMargin

    assert(defaultRuleset.evaluate(rule))
  }

  it should "fail to parse when one of the sub-clauses is invalid" in {
    intercept[RuntimeException](
      defaultRuleset.evaluate(s"($AGE is 18 AND $AGE < 99) OR GENDER is F")
    )
  }

}
