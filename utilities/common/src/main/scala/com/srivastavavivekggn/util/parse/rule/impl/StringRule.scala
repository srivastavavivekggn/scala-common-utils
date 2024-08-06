package com.srivastavavivekggn.scala.util.parse.rule.impl

import com.srivastavavivekggn.scala.util.lang.StringUtils
import com.srivastavavivekggn.scala.util.parse.rule.Rule
import com.srivastavavivekggn.scala.util.parse.rule.context.RuleEvaluationContext
import com.srivastavavivekggn.scala.util.parse.rule.impl.StringRule.{evaluateRule, operator}
import com.srivastavavivekggn.scala.util.parse.{quotedString, space, string}
import fastparse.NoWhitespace.noWhitespaceImplicit
import fastparse.{P, StringInIgnoreCase}

case class StringRule(fields: String*) extends Rule[(String, String, String)] with FieldAware {

  override def parser[_: P]: P[(String, String, String)] = P(fieldsParser ~ space ~ operator ~ space ~ (quotedString | string))

  override def withContext[_: P](ctx: RuleEvaluationContext): P[Boolean] = {
    parser.map(v => evaluateRule(v, ctx))
  }
}

object StringRule {

  final val IS = "is"
  final val IS_NOT = "is not"
  final val STARTS = "starts with"
  final val ENDS = "ends with"
  final val CONTAINS = "contains"

  def operator[_: P]: P[String] = P(StringInIgnoreCase(IS, IS_NOT, STARTS, ENDS, CONTAINS).!)

  def evaluateRule(capture: (String, String, String), context: RuleEvaluationContext): Boolean = {
    capture match {
      case (leftKey, op, right) if context.contains(leftKey) =>
        val left: Option[String] = context.get(leftKey).map(_.toString)

        val result = op match {
          case IS => left.getOrElse(StringUtils.EMPTY).equalsIgnoreCase(right)
          case IS_NOT => !left.getOrElse(StringUtils.EMPTY).equalsIgnoreCase(right)

          case STARTS => left.map(_.toLowerCase).exists(_.startsWith(right.toLowerCase))
          case ENDS => left.map(_.toLowerCase).exists(_.endsWith(right.toLowerCase))
          case CONTAINS => left.map(_.toLowerCase).exists(_.contains(right.toLowerCase))

          case _ => false
        }

        result

      case _ => false
    }
  }
}
