package com.srivastavavivekggn.scala.util.logging

import com.srivastavavivekggn.scala.util.concurrent.Timer
import com.typesafe.scalalogging.Logger
import org.slf4j.MDC

import java.util.concurrent.TimeUnit
import scala.concurrent.Future

object LoggingUtils {

  final val SPEED_KEY = "speed"

  implicit class RichLogger(logger: Logger) {

    def timed[T](speed: Long, fn: (Logger) => T): T = {
      MDC.put(SPEED_KEY, speed.toString)

      try {
        fn(logger)
      }
      finally {
        MDC.remove(SPEED_KEY)
      }
    }


    def timed[T](timer: Timer, fn: (Logger) => T): T = {
      timed(timer.elapsed(TimeUnit.MILLISECONDS), fn)
    }


    /**
      * Stop timer and log
      *
      * @param timer the timer to stop and log
      */
    def stopTimerAndLog(timer: Timer): Unit = {
      timer.stop()
      timed(timer, _.trace(timer.toString(" / ")))
    }

    /**
      * Stop timer, log, and return the result
      *
      * @param timer  the timer
      * @param result the result to return
      * @tparam T the result type
      * @return the result
      */
    def stopTimerAndLog[T](timer: Timer, result: T): T = {
      stopTimerAndLog(timer)
      result
    }

    /**
      * Stop timer, log result, and recover from error (this should be used from a .recoverWith block
      *
      * @param timer the timer
      * @param ex    the exception
      * @return the failed Future
      */
    def stopTimerAndRecover(timer: Timer, ex: Throwable): Future[Throwable] = {
      stopTimerAndLog(timer)
      Future.failed(ex)
    }
  }

}
