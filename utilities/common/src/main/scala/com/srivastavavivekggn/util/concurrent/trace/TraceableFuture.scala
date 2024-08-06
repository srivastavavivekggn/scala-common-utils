package com.srivastavavivekggn.scala.util.concurrent.trace

import java.util.concurrent.atomic.AtomicLong

import com.srivastavavivekggn.scala.util.concurrent.context.DynamicContext
import com.srivastavavivekggn.scala.util.lang.StringUtils

import scala.concurrent.Future.unit
import scala.concurrent.{ExecutionContext, Future}

/**
  * Traceable Future - simple extension of scala Future that enables tracing/timing of method calls
  *
  * @tparam T the future type
  */
trait TraceableFuture[+T] extends Future[T]


object TraceableFuture {

  /**
    * Internal counter
    */
  private val counter = new AtomicLong(0)

  /**
    * Create a wrapped future that times execution
    *
    * @param name the task name
    * @param body the future body
    * @param ec   the execution context
    * @tparam T the future type
    * @return the future result
    */
  def apply[T](name: String)(body: => T)(implicit ec: ExecutionContext, tContext: TraceableFutureContext): Future[T] = {
    apply(tContext.context, name)(body)
  }

  /**
    * Create a wrapped future that times execution
    *
    * @param clazz the class owning the method being traced
    * @param name  the method name
    * @param body  the future body
    * @param ec    the execution context
    * @tparam T the future result type
    * @return the wrapped future
    */
  def apply[T](clazz: Class[_], name: String)(body: => T)(implicit ec: ExecutionContext): Future[T] = {
    apply(clazz.getSimpleName, name)(body)
  }

  /**
    * Create a wrapped future that times execution
    *
    * @param className the class name
    * @param name      the method name
    * @param body      the future body
    * @param ec        the execution context
    * @tparam T the future result type
    * @return the wrapped future
    */
  def apply[T](className: String, name: String)(body: => T)(implicit ec: ExecutionContext): Future[T] = {

    val task = s"${StringUtils.camelCaseToUnderscore(className)}.${StringUtils.camelCaseToUnderscore(name)}"
    val id = counter.incrementAndGet()

    // wrap the body execution with a call to start and subsequently stop the timer
    unit
      .map(_ => DynamicContext.Flow.getTimer.foreach(_.start(task, id)))
      .map(_ => body)
      .andThen {
        case _ => DynamicContext.Flow.getTimer.foreach(_.stop(task, id))
      }
  }
}
