package com.srivastavavivekggn.scala.util.concurrent.trace

trait TraceableFutureTimer {

  def tracerName: String

  def start(name: String, id: Long): Unit

  def stop(name: String, id: Long): Unit
}

/**
  * Default timer implementation that does nothing
  * @param tracerName the name of the tracer
  */
case class NoOpTraceableFutureTimer(tracerName: String) extends TraceableFutureTimer {

  override def start(name: String, id: Long): Unit = ()

  override def stop(name: String, id: Long): Unit = ()

}