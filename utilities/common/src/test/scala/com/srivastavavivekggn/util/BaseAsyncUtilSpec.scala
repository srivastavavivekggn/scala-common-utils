package com.srivastavavivekggn.scala.util

import org.junit.runner.RunWith
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatestplus.junit.JUnitRunner
import org.scalatestplus.mockito.MockitoSugar

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by JohnDeverna on 8/28/17.
  */
@RunWith(classOf[JUnitRunner])
abstract class BaseAsyncUtilSpec extends AsyncFlatSpec with MockitoSugar {

  /**
   * Simple helper to create a future with an optional sleep
   *
   * @param returnVal the return value
   * @param sleep the sleep time in MS
   * @param context the execution context
   * @return the future value
   */
  def createFuture(returnVal: Int, sleep: Long = 0)(implicit context: ExecutionContext): Future[Int] = Future {
    sleep match {
      case s if (s > 0) => Thread.sleep(s)
      case _ => // do nothing
    }

    returnVal
  }(context)

}
