package com.srivastavavivekggn.scala.util.concurrent

import java.time.Duration
import java.util.concurrent.TimeUnit

import com.google.common.base.Stopwatch
import com.srivastavavivekggn.scala.util.lang.StringUtils

import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.TimeUnit
import scala.concurrent.{ExecutionContext, Future}

/**
 * Simple helper object for timing tasks
 */
object TimerUtils {

  /**
   * Convenience method for creating a timer
   *
   * @param name      the timer name
   * @param autoStart true to start the timer immediately upon creation
   * @return the timer
   */
  def createTimer(name: String, autoStart: Boolean = true): Timer = {
    val t = Timer(name)
    if (autoStart) {
      t.start()
    }
    t
  }

}

/**
 * Wrapper class for a named timer
 *
 * @param name the timer name
 */
case class Timer(name: String) {

  /**
   * internal timer for the parent timer
   */
  private val parentTimer = TimedTask(name, autoStart = false)

  /**
   * Collection of named tasks that belong to this timer
   */
  private val tasks: ListBuffer[TimedTask] = ListBuffer.empty[TimedTask]

  /**
   * Start the timer if it hasn't already been started
   */
  def start(): Unit = {
    if (!parentTimer.isStarted) {
      parentTimer.start()
    }
  }

  /**
   * Stop the timer if it hasn't already been stopped
   */
  def stop(): Unit = {
    if (parentTimer.isStarted) {
      parentTimer.stop()
    }
  }

  /**
   * Determine if the timer is running
   *
   * @return true if the timer is running
   */
  def isStarted: Boolean = parentTimer.isStarted

  /**
   * Get the elapsed duration of this timer
   *
   * @return the duration
   */
  def elapsed: Duration = parentTimer.elapsed

  /**
   * Get the elapsed duration of this timer in the specified time unit
   *
   * @param unit the unit of time
   * @return the duration value in the specified unit
   */
  def elapsed(unit: TimeUnit): Long = parentTimer.elapsed(unit)

  /**
   * Get the elapsed duration of all tasks submitted to this timer
   *
   * @return the map of task-name to duration
   */
  def elapsedDetail: Map[String, Duration] = {
    tasks.toList.map(tt => tt.name -> tt.elapsed).toMap
  }

  /**
   * Get the elapsed duration of all tasks submitted to this timer in the specified time unit
   *
   * @param unit the time unit
   * @return the map of task-name to duration in the given unit
   */
  def elapsedDetail(unit: TimeUnit): Map[String, Long] = {
    tasks.toList.map(tt => tt.name -> tt.elapsed(unit)).toMap
  }

  /**
   * Submit a synchronous task to be timed
   *
   * @param name  the name of the task
   * @param thunk the code to execute
   * @tparam T the return type
   * @return the result of the execution
   */
  def timeTask[T](name: String)(thunk: => T): T = {

    // create task timer
    val timer = TimedTask(name)

    // add to task list
    tasks += timer

    // execute the thunk, stop the timer, and return the result
    try {
      val result = thunk
      timer.stop()
      result
    }
    catch {
      // any exception, stop the timer and re-throw
      case ex: Exception =>
        timer.stop()
        throw ex
    }
  }

  /**
   * Time an async task execution
   *
   * @param name  the task name
   * @param thunk the code to execute
   * @param ec    the execution context
   * @tparam T the underlying return type
   * @return the result of the execution
   */
  def timeAsyncTask[T](name: String)(thunk: => Future[T])(implicit ec: ExecutionContext): Future[T] = {

    // create task timer
    val timer = TimedTask(name)

    // add to task list
    tasks += timer

    // execute the code and on result stop the timer
    thunk.map(result => {
      timer.stop()
      result
    }).recoverWith {
      // for any exception, we'll stop the timer and return the failure
      case ex: Exception =>
        timer.stop()
        Future.failed(ex)
    }
  }

  /**
   * Write out the timer and any subtask metrics as a string
   *
   * @return the timer details as a string
   */
  override def toString: String = toString("; " + StringUtils.BLANK)

  /**
   * toString that allows you to optionally specify how to separate sub task elements.
   *
   * @param separator the separator to use
   * @return the string-ified timer
   */
  def toString(separator: String): String = {
    val sb = new StringBuilder(s"Timer ${name}, elapsed time: ${this.elapsed(TimeUnit.MILLISECONDS)}ms")

    tasks.foreach(tt => sb.append(separator).append(tt.toString))

    sb.toString
  }
}

/**
 * Wrapper class for a timed task
 *
 * @param name      the task name
 * @param autoStart true to automatically start the timer upon creation
 */
private case class TimedTask(name: String, autoStart: Boolean = true) {

  /**
   * Internally, we use a StopWatch for tracking time
   */
  private val stopWatch: Stopwatch = if (autoStart) {
    Stopwatch.createStarted()
  }
  else {
    Stopwatch.createUnstarted()
  }

  /**
   * Start the timer
   */
  def start(): Unit = stopWatch.start()

  /**
   * Stop the timer
   */
  def stop(): Unit = stopWatch.stop()

  /**
   * Determine if the timer is running
   *
   * @return true if running, false otherwise
   */
  def isStarted: Boolean = stopWatch.isRunning

  /**
   * Get elapsed duration
   *
   * @return the elapsed duration
   */
  def elapsed: Duration = stopWatch.elapsed()

  /**
   * Get the elapsed duration in the given unit
   *
   * @param unit the unit
   * @return the elapsed unit of time
   */
  def elapsed(unit: TimeUnit): Long = stopWatch.elapsed(unit)

  /**
   * Override to string to return task name and execution time
   *
   * @return the task name and ms execution
   */
  override def toString: String = s"$name=${stopWatch.elapsed(TimeUnit.MILLISECONDS)}ms"
}