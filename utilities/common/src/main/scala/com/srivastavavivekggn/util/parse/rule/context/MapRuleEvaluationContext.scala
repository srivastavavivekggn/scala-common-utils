package com.srivastavavivekggn.scala.util.parse.rule.context

case class MapRuleEvaluationContext(data: Map[String, Any] = Map.empty) extends RuleEvaluationContext {

  override def contains(key: String): Boolean = data.contains(key)

  override def get(key: String): Option[Any] = data.get(key)
}
