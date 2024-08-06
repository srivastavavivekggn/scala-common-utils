package com.srivastavavivekggn.scala.util.parse.rule

import com.srivastavavivekggn.scala.util.parse.rule.RuleSet.{andOrCapture, evalRuleSetTree}
import com.srivastavavivekggn.scala.util.parse.rule.context.RuleEvaluationContext
import com.srivastavavivekggn.scala.util.parse.space
import com.typesafe.scalalogging.StrictLogging
import fastparse.NoWhitespace._
import fastparse.Parsed.Extra
import fastparse._

/**
 *
 * @param factors
 */
case class RuleSet(context: RuleEvaluationContext, factors: Rule[_]*) extends StrictLogging {

  private def finalFactor[_: P]: P[Boolean] = factors.toList match {
    case Nil => throw new RuntimeException("At least 1 Rule must be passed")
    case head :: Nil => head.withContext(context)
    case head :: tail => tail.foldLeft(head.withContext(context)) {
      case (left, right) => P(left | right.withContext(context))
    }
  }

  private def wrapped[_: P]: P[Boolean] = P("(" ~/ ruleSetParser ~ ")")

  private def singleOrWrapped[_: P]: P[Boolean] = P(finalFactor | wrapped)

  // the rule set parser joins factors using And/Or and repeats
  private def ruleSetParser[_: P]: P[Boolean] = P(space.? ~ singleOrWrapped ~ (andOrCapture ~/ singleOrWrapped).rep ~ space.?).map(evalRuleSetTree)

  // finally, return the parser that will capture everything as a ruleSet
  def parser[_: P]: P[Boolean] = P(Start ~ space.? ~ ruleSetParser ~ space.? ~ End)

  /**
   *
   * @param rules
   * @return
   */
  def evaluate(rules: String): Boolean = {
    parse(rules, parser(_)) match {
      case Parsed.Success(v, _) => v
      case Parsed.Failure(p: String, _: Int, _: Extra) => throw new RuntimeException(s"Invalid rule set: $p")
      case _ => throw new RuntimeException(s"Invalid rule set")
    }
  }
}


/**
 * Helper object that enables creation of parsers that will join boolean parsers
 * together using AND/OR syntax
 */
object RuleSet {

  // AND join
  final val AND = "AND"

  // OR join
  final val OR = "OR"

  // capture for join operator
  private final def andOrCapture[_: P]: P[String] = P(space.? ~ StringIn(AND, OR).! ~ space.?)

  // helper method that allows us to compare rules using the appropriate Join language (And/Or)
  private def evalRuleSetTree(tree: (Boolean, Seq[(String, Boolean)])): Boolean = {
    val (base, ops) = tree

    ops.foldLeft(base) {
      case (left, (op, right)) => op match {
        case AND => left && right
        case OR => left || right
        case _ => throw new RuntimeException(s"Invalid rule join: $op")
      }
    }
  }
}
