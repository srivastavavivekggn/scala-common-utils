package com.srivastavavivekggn.scala.util.test.mock

import org.mockito.ArgumentMatchers.any

import scala.concurrent.{ExecutionContext, Future}

trait MockHelpers {

  def anyEC: ExecutionContext = any(classOf[ExecutionContext])

  /**
    * This allows us to make a non-future into a future, helpful for writing mocks
    *
    * @param el the value
    * @tparam T the value type
    * @return a future of that value
    */
  implicit def asFuture[T](el: T): Future[T] = Future.successful(el)

}
