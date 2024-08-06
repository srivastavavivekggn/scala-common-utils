package com.srivastavavivekggn.scala.util.parse.rule.impl

import com.srivastavavivekggn.scala.util.lang.StringUtils
import com.srivastavavivekggn.scala.util.parse.rule.Rule
import com.srivastavavivekggn.scala.util.parse.rule.context.RuleEvaluationContext
import com.srivastavavivekggn.scala.util.parse.rule.impl.ExistenceRule.{evaluateRule, operator}
import com.srivastavavivekggn.scala.util.parse.space
import fastparse.NoWhitespace.noWhitespaceImplicit
import fastparse.{P, StringInIgnoreCase}

/**
  * Rule that checks for existence (present or missing) of a field
  * @param fields the fields
  */
case class ExistenceRule(fields: String*) extends Rule[(String, String)] with FieldAware {
  override def parser[_: P]: P[(String, String)] = P(fieldsParser ~ space ~ operator)

  override def withContext[_: P](ctx: RuleEvaluationContext): P[Boolean] = {
    parser.map(v => evaluateRule(v, ctx))
  }
}

object ExistenceRule {

  final val EXISTS = "exists"

  final val NOT_EXISTS = "does not exist"

  def operator[_: P]: P[String] = P(StringInIgnoreCase(EXISTS, NOT_EXISTS)).!

  def evaluateRule(capture: (String, String),
                   context: RuleEvaluationContext): Boolean = {

    capture match {
      case (key, op) =>

        op match {

          // we are expecting it to exist
          case EXISTS => context.contains(key) && !valueIsEmpty(context.get(key))

          // either the key is missing, or the value is empty
          case NOT_EXISTS => !context.contains(key) || valueIsEmpty(context.get(key))

          case _ => false
        }

      case _ => false
    }
  }

  def valueIsEmpty(v: Option[Any]): Boolean = {
    v.isEmpty || v.exists(v => v == null || None.equals(v) || StringUtils.isEmpty(v.toString))
  }
}
