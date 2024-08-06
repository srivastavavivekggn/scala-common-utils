package com.srivastavavivekggn.scala.util.parse.rule

import java.time.LocalDate
import com.srivastavavivekggn.scala.util.parse.rule.context.{MapRuleEvaluationContext, RuleEvaluationContext}
import fastparse.{P, Parsed}

trait RuleHelpers {

  final val AGE = "AGE"
  final val GENDER = "GENDER"
  final val ZIP = "ZIP"
  final val YEAR = "YEAR"
  final val MONTH = "MONTH"
  final val BIOS = "BIOS"
  final val LABS = "LABS"
  final val START = "START_DATE"
  final val END = "END.DATE"
  final val NAME = "NAME"

  protected final val defaultData = Map(
    AGE -> 18,
    YEAR -> 1970,
    GENDER -> "F",
    ZIP -> "07030",
    BIOS -> Seq("a", "b", "c"),
    LABS -> Seq("x", "y", "z"),
    START -> LocalDate.of(2020, 2, 1),
    END -> LocalDate.of(2021, 1, 1),
    NAME -> ""
  )

  protected final val defaultContext = new RuleEvaluationContext {
    override def contains(key: String): Boolean = defaultData.contains(key) || key.startsWith("meta.")

    override def get(key: String): Option[Any] = key match {
      case k: String if k.startsWith("meta.") => defaultData.get(k.drop(5))
      case k: String => defaultData.get(k)
    }
  }

  def parse[T](rule: Rule[T], rules: String): T = {
    fastparse.parse(rules, rule.parser(_)) match {
      case Parsed.Success(v, idx) => v
      case f@Parsed.Failure(p, idx, e) => throw new RuntimeException("Failed to parse: " + p)
      case _ => throw new RuntimeException("Failed to parse")
    }
  }

  def assertRule(parser: P[_] => P[Boolean],
                 rules: String,
                 expected: Boolean = true,
                 msg: String = ""): Unit = {
    fastparse.parse(rules, parser(_)) match {
      case Parsed.Success(v, _) if v == expected => assert(assertion = true, msg)
      case _ => assert(assertion = false, msg)
    }
  }
}
