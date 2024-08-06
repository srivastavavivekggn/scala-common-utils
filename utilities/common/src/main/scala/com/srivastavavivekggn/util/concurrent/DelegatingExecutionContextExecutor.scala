package com.srivastavavivekggn.scala.util.concurrent

import com.srivastavavivekggn.scala.util.concurrent.context.DynamicContext.{Audit, C, Flow, Meta}
import org.slf4j.MDC

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

case class DelegatingExecutionContextExecutor(delegate: ExecutionContext) extends ExecutionContextExecutor {

  override def execute(command: Runnable): Unit = {

    // get current values
    val mdcContext = MDC.getCopyOfContextMap
    val audit: C = Audit.getContext
    val flow: C = Flow.getContext
    val meta: C = Meta.getContext

    def beforeRun(): Unit = setContextMap(mdcContext)

    def afterRun(): Unit = {}

    delegate.execute(new Runnable {
      override def run(): Unit = {

        beforeRun()

        Audit.withValues(audit) {
          Flow.withValues(flow) {
            Meta.withValues(meta) {

              try command.run()
              finally afterRun()

            }
          }
        }
      }
    })
  }

  private[this] def setContextMap(context: java.util.Map[String, String]): Unit = {
    if (context == null) {
      MDC.clear()
    } else {
      MDC.setContextMap(context)
    }
  }

  override def reportFailure(cause: Throwable): Unit = delegate.reportFailure(cause)
}
