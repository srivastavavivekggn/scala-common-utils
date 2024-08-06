package com.srivastavavivekggn.scala.util.parse.rule

import com.srivastavavivekggn.scala.util.parse.rule.context.{MapRuleEvaluationContext, RuleEvaluationContext}
import fastparse._

trait Rule[T] {

  def parser[_: P]: P[T]

  def withContext[_: P](ctx: RuleEvaluationContext): P[Boolean]

  final def withContext[_: P](ctx: Map[String, Any]): P[Boolean] = withContext(MapRuleEvaluationContext(ctx))
}