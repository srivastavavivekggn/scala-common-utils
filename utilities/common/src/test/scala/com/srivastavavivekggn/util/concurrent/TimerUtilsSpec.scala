package com.srivastavavivekggn.scala.util.concurrent

import java.util.concurrent.TimeUnit

import com.srivastavavivekggn.scala.util.BaseAsyncUtilSpec

class TimerUtilsSpec extends BaseAsyncUtilSpec {

  behavior of "TimerUtils"

  it should "create a timer in an un-started state" in {
    val timer = TimerUtils.createTimer("test1", autoStart = false)
    assert(!timer.isStarted)
  }

  it should "create a timer in a started state" in {
    val timer = TimerUtils.createTimer("test1")
    assert(timer.isStarted)
  }

  it should "time 3 independent tasks" in {

    val ctx = AsyncUtils.Contexts.global

    val timer = TimerUtils.createTimer("test1")
    timer.start()

    timer.timeAsyncTask("task1")({
      createFuture(1, 250)
    }).flatMap(r1 => {
      timer.timeAsyncTask("task2") {
        createFuture(2, 150)
      }
    }).flatMap(r2 => {
      timer.timeAsyncTask("task3") {
        createFuture(3, 500)
      }
    }).map(r3 => {
      val detail = timer.elapsedDetail
      assert(detail.size == 3)
      assert(timer.elapsed(TimeUnit.MILLISECONDS) > 900)
    })
  }

}
