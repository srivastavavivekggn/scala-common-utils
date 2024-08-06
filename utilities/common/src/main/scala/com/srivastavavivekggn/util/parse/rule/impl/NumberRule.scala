package com.srivastavavivekggn.scala.util.parse.rule.impl

import com.srivastavavivekggn.scala.util.parse.rule.Rule
import com.srivastavavivekggn.scala.util.parse.rule.context.RuleEvaluationContext
import com.srivastavavivekggn.scala.util.parse.rule.impl.NumberRule.operator
import com.srivastavavivekggn.scala.util.parse.{number, space}
import fastparse.NoWhitespace.noWhitespaceImplicit
import fastparse.{P, StringIn}

case class NumberRule(fields: String*) extends Rule[(String, String, Int)] with FieldAware {

  override def parser[_: P]: P[(String, String, Int)] = P(fieldsParser ~ space ~ operator ~ space ~ number)

  override def withContext[_: P](ctx: RuleEvaluationContext): P[Boolean] = {
    parser.map(v => NumberRule.evaluateRule(v, ctx))
  }
}


object NumberRule {

  final val LT = "<"
  final val LTE = "<="
  final val EQ = "="
  final val NEQ = "!="
  final val GT = ">"
  final val GTE = ">="

  def operator[_: P]: P[String] = P(StringIn(LT, LTE, EQ, NEQ, GT, GTE).!)

  def evaluateRule(capture: (String, String, Int),
                   context: RuleEvaluationContext): Boolean = {

    capture match {
      case (leftKey, op, right) if context.contains(leftKey) =>
        val left = context.get(leftKey).map(_.toString.toInt).get

        val result = op match {
          case LT => left < right
          case LTE => left <= right
          case EQ => left == right
          case NEQ => left != right
          case GT => left > right
          case GTE => left >= right
          case _ => false
        }

        result

      case _ => false
    }
  }

}