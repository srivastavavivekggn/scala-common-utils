package com.srivastavavivekggn.scala.util.parse.rule.context

trait RuleEvaluationContext {

  def contains(key: String): Boolean

  def get(key: String): Option[Any]
}
