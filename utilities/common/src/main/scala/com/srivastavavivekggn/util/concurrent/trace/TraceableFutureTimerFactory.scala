package com.srivastavavivekggn.scala.util.concurrent.trace

/**
  * Timer factory interface
  */
trait TraceableFutureTimerFactory {

  /**
    * Create a new timer with the given name
    *
    * @param name the timer name
    * @return the traceable future timer
    */
  def createTimer(name: String): TraceableFutureTimer

  /**
    * Record timing for a task in this timer
    *
    * @param taskName the task name
    * @param nanos    the execute time in nanos
    */
  def record(taskName: String, nanos: Long): Unit
}

/**
  * TraceableFutureTimerFactory companion object
  */
object TraceableFutureTimerFactory {

  /**
    * The singleton timer factory configured for the system
    */
  private var timerFactory: Option[TraceableFutureTimerFactory] = None

  /**
    * Setter for the single factory instance
    *
    * @param factory the factory instance
    */
  def setFactoryInstance(factory: TraceableFutureTimerFactory): Unit = this.timerFactory = Option(factory)

  /**
    * Create a new timer with the given name
    *
    * @param name the timer name
    * @return the timer
    */
  def createTimer(name: String): TraceableFutureTimer = {
    timerFactory.map(_.createTimer(name)).getOrElse(NoOpTraceableFutureTimer(name))
  }
}