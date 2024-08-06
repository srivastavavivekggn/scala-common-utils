package com.srivastavavivekggn.scala.util.concurrent.context

import com.srivastavavivekggn.scala.util.concurrent.trace.{TraceableFutureTimer, TraceableFutureTimerFactory}
import org.slf4j.MDC

import scala.util.DynamicVariable

/**
  * Defines a simple wrapper for generic DynamicVariables that simply track Map[String, AnyRef] values
  */
sealed trait DynamicContext {

  /**
    * The underlying DynamicVariable
    *
    * @return the context
    */
  protected def context: DynamicContext.D

  /**
    * Sets the incoming values and executes the given thunk
    *
    * @param values the values
    * @param thunk  the thunk
    * @tparam S the return type
    * @return the result of the thunk
    */
  def withValues[S](values: Map[String, AnyRef])(thunk: => S): S = {

    val current = context.value
    val updated = values.foldLeft(current)((ctx, newVal) => ctx.updated(newVal._1, newVal._2))

    context.withValue(updated) {
      thunk
    }
  }

  /**
    * Get a value from the context
    *
    * @param key          the key to retrieve
    * @param expectedType the expected type
    * @tparam T the result value type
    * @return the value, if found, or none
    */
  def getValue[T](key: String, expectedType: Class[T]): Option[T] = context.value.get(key) match {
    case Some(v) if expectedType.isAssignableFrom(v.getClass) => Some(v.asInstanceOf[T])
    case Some(v) => throw new RuntimeException(s"Value with type ${v.getClass} does not match expected type $expectedType")
    case None => None
  }

  /**
    * Get the entire context value (i.e., the entire Map)
    *
    * @return the context value
    */
  def getContext: DynamicContext.C = context.value
}


/**
  * Companion for DynamicContext
  */
object DynamicContext {

  // simple type helpers for convenience
  type C = Map[String, AnyRef]
  type D = DynamicVariable[C]

  /**
    * Helper method to create a new dynamic variable
    *
    * @return the new dynamic variable
    */
  private def newContext = new DynamicVariable[C](Map.empty[String, AnyRef])

  /**
    * Audit context definition
    */
  case object Audit extends DynamicContext {

    final val ADMIN = "administrator"
    final val AUTH_METHOD = "method"

    override protected val context: D = newContext
  }

  /**
    * Flow context definition
    */
  case object Flow extends DynamicContext {

    final val XFLOW = "xflow"
    final val TIMER = "timer"

    override protected val context: D = newContext

    def getTimer: Option[TraceableFutureTimer] = getValue(TIMER, classOf[TraceableFutureTimer])

    override def withValues[S](values: Map[String, AnyRef])(thunk: => S): S = {

      val current = context.value
      val updated = values.foldLeft(current)((ctx, newVal) => ctx.updated(newVal._1, newVal._2))

      val xflowBefore = MDC.get(XFLOW)

      context.withValue(updated) {
        MDC.put(XFLOW, context.value.get(XFLOW).map(_.toString).getOrElse(xflowBefore))

        try thunk
        finally MDC.put(XFLOW, xflowBefore)
      }
    }
  }

  /**
    * Meta context definition
    */
  case object Meta extends DynamicContext {
    override protected val context: D = newContext
  }


  /**
    * Convenience function to add xflow value
    *
    * @param xflow the xflow
    * @param thunk the thunk to execute
    * @tparam S the result value
    * @return the result
    */
  def withFlow[S](xflow: String)(thunk: => S): S = Flow.withValues(Map(
    Flow.XFLOW -> xflow
  ))(thunk)

  /**
    * Convenience function to add xflow and a timer
    *
    * @param xflow the xflow
    * @param thunk the thunk to execute
    * @tparam S the result value
    * @return the result
    */
  def withTimedFlow[S](xflow: String)(thunk: => S): S = Flow.withValues(Map(
    Flow.XFLOW -> xflow,
    Flow.TIMER -> TraceableFutureTimerFactory.createTimer(xflow)
  ))(thunk)

  /**
    * Convenience method to add principal and auth method
    *
    * @param principal the principal
    * @param method    the auth method
    * @param thunk     the thunk to execute
    * @tparam S the result type
    * @return the result
    */
  def withAudit[S](principal: String, method: String)(thunk: => S): S = Audit.withValues(Map(
    Audit.ADMIN -> principal,
    Audit.AUTH_METHOD -> method
  ))(thunk)

  /**
    * Convenience method to add meta data to the context
    *
    * @param values the meta values to add
    * @param thunk  the thunk to execute
    * @tparam S the result type
    * @return the result
    */
  def withMeta[S](values: Map[String, AnyRef])(thunk: => S): S = Meta.withValues(values)(thunk)
}
