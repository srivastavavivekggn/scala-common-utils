package com.srivastavavivekggn.scala.util.parse.rule.impl

import com.srivastavavivekggn.scala.util.parse.rule.Rule
import com.srivastavavivekggn.scala.util.parse.rule.context.RuleEvaluationContext
import com.srivastavavivekggn.scala.util.parse.rule.impl.ArrayOfStringRule.{evaluateRule, operator}
import com.srivastavavivekggn.scala.util.parse.{space, stringArray}
import fastparse.NoWhitespace.noWhitespaceImplicit
import fastparse.P

case class ArrayOfStringRule(fields: String*) extends Rule[(String, String, Seq[String])] with FieldAware {

  override def parser[_: P]: P[(String, String, Seq[String])] = P(fieldsParser ~ space ~ operator ~ space ~ stringArray)

  override def withContext[_: P](ctx: RuleEvaluationContext): P[Boolean] = {
    parser.map(v => evaluateRule(v, ctx))
  }
}

object ArrayOfStringRule extends ArrayOfRules {

  def evaluateRule(capture: (String, String, Seq[String]),
                   context: RuleEvaluationContext): Boolean = {
    capture match {
      case (leftKey, op, right) if context.contains(leftKey) =>

        val left = context.get(leftKey) match {
          case Some(l: Seq[_]) => l.map(_.toString.toLowerCase)
          case _ => Seq.empty[String]
        }

        val result = op match {
          // one from 'right' exists in 'left'
          case HAS_ONE_OF => right.map(_.toLowerCase).exists(r => left.contains(r))

          // all from 'right' exist in 'left' (i.e., we can't find any case where 'right' isn't found in 'left')
          case HAS_ALL_OF => right.map(_.toLowerCase).forall(r => left.contains(r))

          // no match
          case _ => false
        }

        result

      case _ => false
    }
  }
}
