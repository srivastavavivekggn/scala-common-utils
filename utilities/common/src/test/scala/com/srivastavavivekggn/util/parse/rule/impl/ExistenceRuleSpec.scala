package com.srivastavavivekggn.scala.util.parse.rule.impl

import com.srivastavavivekggn.scala.util.BaseUtilSpec
import com.srivastavavivekggn.scala.util.parse.rule.{RuleHelpers, RuleSet}
import com.srivastavavivekggn.scala.util.parse.rule.impl.StringRule.IS
import fastparse.P

class ExistenceRuleSpec extends BaseUtilSpec with RuleHelpers {

  private val baseRule = ExistenceRule(AGE, YEAR, MONTH, NAME, "meta..*")

  behavior of "ExistenceRule"

  it should "parse an existence rule properly" in {
    val v = parse(baseRule, s"$AGE exists")
    assert(v == (AGE, "exists"))
  }

  it should "parse an existence rule properly (extra spaces)" in {
    val v = parse(baseRule, s"$AGE         does not exist")
    assert(v == (AGE, "does not exist"))
  }

  it should "fail to parse a rule when a bad field is used" in {
    intercept[RuntimeException](
      parse(baseRule, s"${ZIP.toLowerCase} exists")
    )
  }

  it should "fail to parse a rule when a bad operator is used" in {
    intercept[RuntimeException](
      parse(baseRule, s"$AGE is bogus")
    )
  }

  it should "properly evaluate rules against a context" in {
    def inContext[_: P]: P[Boolean] = baseRule.withContext(defaultContext)

    assertRule(inContext(_), s"$AGE exists")
    assertRule(inContext(_), s"$MONTH does not exist")
    assertRule(inContext(_), s"$AGE does not exist", expected = false)
    assertRule(inContext(_), s"$YEAR does not exist", expected = false)
  }


  it should "evaluate empty and non-empty" in {

    def parser[_: fastparse.P]: fastparse.P[Boolean] = baseRule.withContext(defaultContext)

    assertRule(parser(_), s"$AGE exists")
    assertRule(parser(_), s"$NAME does not exist")
  }
}
