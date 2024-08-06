package com.srivastavavivekggn.scala.util.web.concurrent

import org.springframework.web.context.request.async.DeferredResult

import scala.concurrent.Future
import scala.util.{Failure, Success}
import com.srivastavavivekggn.scala.util.concurrent.AsyncUtils.Contexts.Implicit.global

/**
  * Simple deferred result extension wrapper for a scala.concurrent.Future
  *
  * @param f the future to wrap
  * @tparam T the result type of the future
  */
case class ScalaFutureDeferredResult[T](val f: Future[T]) extends DeferredResult[T] {

  f.onComplete {
    case Success(_: Unit) => this.setResult(Void.TYPE.asInstanceOf[T])
    case Success(result) => this.setResult(result)
    case Failure(ex) => this.setErrorResult(ex)
  }

}
