package com.srivastavavivekggn.scala.util.parse.rule.impl

import com.srivastavavivekggn.scala.util.parse.rule.Rule
import com.srivastavavivekggn.scala.util.parse.rule.context.RuleEvaluationContext
import com.srivastavavivekggn.scala.util.parse.rule.impl.NumberArrayRule.{evaluateRule, operator}
import com.srivastavavivekggn.scala.util.parse.{numberArray, space}
import fastparse.NoWhitespace.noWhitespaceImplicit
import fastparse.P


case class NumberArrayRule(fields: String*) extends Rule[(String, String, Seq[Int])] with FieldAware {
  override def parser[_: P]: P[(String, String, Seq[Int])] = P(fieldsParser ~ space ~ operator ~ space ~ numberArray)

  override def withContext[_: P](ctx: RuleEvaluationContext): P[Boolean] = {
    parser.map(v => evaluateRule(v, ctx))
  }
}


object NumberArrayRule extends ArrayRules {

  def evaluateRule(capture: (String, String, Seq[Int]), context: RuleEvaluationContext): Boolean = {
    capture match {
      case (leftKey, op, right) if context.contains(leftKey) =>

        val left = context.get(leftKey).map(_.toString.toInt)

        val result = op match {
          case FOUND_IN if left.isDefined => right.contains(left.get)
          case NOT_FOUND_IN if left.isDefined => !right.contains(left.get)
          case _ => false
        }

        result

      case _ => false
    }
  }
}
