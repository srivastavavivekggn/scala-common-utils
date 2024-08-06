package com.srivastavavivekggn.scala.util.parse.rule.impl

import com.srivastavavivekggn.scala.util.lang.DateUtils
import com.srivastavavivekggn.scala.util.parse.rule.Rule
import com.srivastavavivekggn.scala.util.parse.rule.context.RuleEvaluationContext
import com.srivastavavivekggn.scala.util.parse.{Scheduler, space}
import fastparse.NoWhitespace.noWhitespaceImplicit
import fastparse._

import java.time.LocalDateTime

case class TemporalRule(fields: String*) extends Rule[(String, String, LocalDateTime)] with FieldAware {

  private val scheduler: Scheduler = Scheduler()

  override def parser[_: P]: P[(String, String, LocalDateTime)] = {
    P(fieldsParser ~ space ~ (NumberRule.operator | TemporalRule.operator) ~ space ~ scheduler.anyTemporal)
  }

  override def withContext[_: P](ctx: RuleEvaluationContext): P[Boolean] = {
    parser.map(v => TemporalRule.evaluateRule(v, ctx, scheduler))
  }
}

object TemporalRule {

  final val LT = "before"
  final val LTE = "before or equal to"
  final val EQ = "equal to"
  final val NEQ = "not equal to"
  final val GT = "after"
  final val GTE = "after or equal to"

  def operator[_: P]: P[String] = P(StringIn(LT, LTE, EQ, NEQ, GT, GTE).!)

  /**
    * Evaluate date comparison rules
    *
    * @param capture the capture
    * @param context the context
    * @return true if the rule passes, false otherwise
    */
  def evaluateRule(capture: (String, String, LocalDateTime),
                   context: RuleEvaluationContext,
                   scheduler: Scheduler): Boolean = {

    capture match {
      case (leftKey, op, right) if context.contains(leftKey) =>

        val leftString = context.get(leftKey).map(_.toString).orNull

        fastparse.parse(leftString, scheduler.anyTemporal(_)) match {
          case Parsed.Success(left, _: Int) =>

            val leftMillis = DateUtils.toEpochMilli(left)
            val rightMillis = DateUtils.toEpochMilli(right)

            op match {
              case LT | NumberRule.LT => leftMillis < rightMillis
              case LTE | NumberRule.LTE => leftMillis <= rightMillis
              case EQ | NumberRule.EQ => leftMillis == rightMillis
              case NEQ | NumberRule.NEQ => leftMillis != rightMillis
              case GT | NumberRule.GT => leftMillis > rightMillis
              case GTE | NumberRule.GTE => leftMillis >= rightMillis
              case _ => false
            }

          // failure case
          case _ => false
        }

      case _ => false
    }
  }
}
