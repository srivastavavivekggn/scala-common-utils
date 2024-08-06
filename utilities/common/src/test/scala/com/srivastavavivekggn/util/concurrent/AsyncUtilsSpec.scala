package com.srivastavavivekggn.scala.util.concurrent

import com.srivastavavivekggn.scala.util.BaseAsyncUtilSpec

import scala.concurrent.{ExecutionContext, Future}

class AsyncUtilsSpec extends BaseAsyncUtilSpec {

  behavior of "AsyncUtils"

  it should "execute code asynchronously" in {

    val ctx = AsyncUtils.Contexts.global

    Future
      .firstCompletedOf(
        Seq(
          createFuture(1, sleep = 250)(ctx),
          createFuture(2)(ctx)
        )
      )(ctx)
      .map(res => {
        // future 2 should complete first since future1 was sleeping
        assert(res == 2, "Future 2 should finish first")
      })(ctx)
  }


  it should "execute code sequentially" in {

    @volatile var finalVal = 0
    @volatile var thread1Name = ""

    AsyncUtils.sequential { ctx =>

      // execute the future and map in the same ctx
      createFuture(1, sleep = 250)(ctx).map(r => {
        finalVal = r
        thread1Name = Thread.currentThread().getName
      })(ctx)

      // execute the future and map in the same ctx
      createFuture(2)(ctx).map(r => {
        finalVal = r
        assert(Thread.currentThread().getName equals thread1Name)
      })(ctx)

    }(AsyncUtils.Contexts.global).map(res =>
      assert(finalVal == 2, "Future 2 should finish last")
    )
  }


  it should "create a single thread executor" in {

    // execute in the multi-threaded global context
    Future {

      // now that we have a thread, create a single-thread context
      val ctx = AsyncUtils.Contexts.currentThread

      // execute both futures using and firstCompletedOf using the single-thread context
      Future
        .firstCompletedOf(
          Seq(
            createFuture(1, sleep = 250)(ctx),
            createFuture(2)(ctx)
          )
        )(ctx)

    }(AsyncUtils.Contexts.global).flatMap(res => {
      // future 1 should complete first even though it has a sleep
      res.map(r => assert(r == 1, "Future 1 should finish first"))
    })
  }


  it should "exeucte sequentially a mapping function over futures" in {

    val ctx = AsyncUtils.Contexts.global

    Future {

      val elements = List(10, 20, 30, 40, 50, 60, 70, 80, 90, 100)

      def addOne(el: Int): Future[Int] = Future {

        // sleep is shorter on each subsequent el
        Thread.sleep(1000 - (el * 10))

        el + 1
      }(AsyncUtils.ContextsIO)

      AsyncUtils.sequentialMap(elements, addOne)

    }(ctx).flatMap(res => {
      res.map(lst =>
        assert(lst == List(11, 21, 31, 41, 51, 61, 71, 81, 91, 101))
      )
    })
  }

  it should "propagate a failure of a single future through sequentialMap call" in {

    val ctx = AsyncUtils.Contexts.global

    val els = List(1, 2, 3, 4, 5, 7, 8, 10)
    val mapper: (Int) => Future[Int] = { el: Int =>
      if(el % 3 == 0) {
        Future.failed(new RuntimeException("failed on 3!"))
      } else {
        Future.successful(el)
      }
    }

    AsyncUtils.sequentialMap(els, mapper)
      .map(e => assert(false, "future failure didn't propagate"))
      .recoverWith {
        case ex: Exception => assert(true, "failure propagated")
      }
  }

  it should "fold over incoming list and pass alongn results to each subsequent invocation" in {

    val ctx = AsyncUtils.Contexts.global

    val els = Range(1, 11).toList

    def op(i: Int, l: List[Int]): Future[Int] = {

      // check to ensure the passed in list has the values we're expecting
      if (i == 1) {
        assert(l.isEmpty)
      }
      else if (i == 2) {
        assert(l.head == 2)
      }
      else if (i == 4) {
        assert(l.sum == 12)
      }

      Future.successful(i * 2)
    }

    AsyncUtils.sequentialFold(els, op)
      .map(l => assert(l.size == 10 && l.sum == 110))
  }


  behavior of "AsyncUtils.executeChain"

  it should "execute a series of commands against a single element" in {

    val ops = Seq[(String) => Future[String]](
      (s) => {Future.successful(s + "b")},
      (s) => {Future.successful(s + "c")},
      (s) => {Future.successful(s + "d")},
      (s) => {Future.successful(s + "e")},
      (s) => {Future.successful(s + "f")},
      (s) => {Future.successful(s + "g")}
    )

    AsyncUtils.executeChain("a", continueOnFailure = false, ops: _*)
      .map(result => {
        assert(result.equals("abcdefg"))
      })
  }

  it should "stop executing operations when failure occurs in chain" in {

    val ops = Seq[(String) => Future[String]](
      (s) => {Future.successful(s + "b")},
      (s) => {Future.successful(s + "c")},
      (s) => {Future.successful(s + "d")},
      (s) => {Future.failed(new RuntimeException("bad operation"))},
      (s) => {Future.successful(s + "f")},
      (s) => {Future.successful(s + "g")}
    )

    AsyncUtils.executeChain("a", continueOnFailure = false, ops: _*)
      .map(result => {
        assert(false, "Failure expected")
      })
      .recoverWith {
        case ex: Exception => assert(true)
      }
  }

  it should "continue executing operations despite failure in chain" in {

    val ops = Seq[(String) => Future[String]](
      (s) => {Future.successful(s + "b")},
      (s) => {Future.successful(s + "c")},
      (s) => {Future.successful(s + "d")},
      (s) => {Future.failed(new RuntimeException("bad operation"))},
      (s) => {Future.successful(s + "f")},
      (s) => {Future.successful(s + "g")}
    )

    AsyncUtils.executeChain("a", continueOnFailure = true, ops: _*)
      .map(result => {
        assert(result.equals("abcdfg"))
      })
  }


  it should "report errors and continue executing operations despite failure in chain" in {

    val ops = Seq[(String) => Future[String]](
      (s) => {Future.successful(s + "b")},
      (s) => {Future.successful(s + "c")},
      (s) => {Future.successful(s + "d")},
      (s) => {Future.failed(new RuntimeException("bad operation"))},
      (s) => {Future.successful(s + "f")},
      (s) => {Future.successful(s + "g")}
    )

    var failedCount = 0

    AsyncUtils
      .executeChain("a", continueOnFailure = true, (t: Throwable) => {failedCount = failedCount + 1}, ops: _*)
      .map(result => {
        assert(result.equals("abcdfg"))
        assert(failedCount == 1)
      })
  }
}
