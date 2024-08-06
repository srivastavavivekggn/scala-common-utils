package com.srivastavavivekggn.scala.util.parse.rule.impl

import com.srivastavavivekggn.scala.util.parse.rule.Rule
import com.srivastavavivekggn.scala.util.parse.rule.context.RuleEvaluationContext
import com.srivastavavivekggn.scala.util.parse.rule.impl.StringArrayRule.{evaluateRule, operator}
import com.srivastavavivekggn.scala.util.parse.{space, stringArray}
import fastparse.NoWhitespace.noWhitespaceImplicit
import fastparse.P

case class StringArrayRule(fields: String*) extends Rule[(String, String, Seq[String])] with FieldAware {
  override def parser[_: P]: P[(String, String, Seq[String])] = P(fieldsParser ~ space ~ operator ~ space ~ stringArray)

  override def withContext[_: P](ctx: RuleEvaluationContext): P[Boolean] = {
    parser.map(v => evaluateRule(v, ctx))
  }
}

object StringArrayRule extends ArrayRules {

  def evaluateRule(capture: (String, String, Seq[String]), context: RuleEvaluationContext): Boolean = {
    capture match {
      case (leftKey, op, right) if context.contains(leftKey) =>

        val left = context.get(leftKey).map(_.toString.toLowerCase).orNull

        val result = op match {
          case FOUND_IN => right.map(_.toLowerCase).contains(left)
          case NOT_FOUND_IN => !right.map(_.toLowerCase).contains(left)
          case _ => false
        }

        result

      case _ => false
    }
  }
}
