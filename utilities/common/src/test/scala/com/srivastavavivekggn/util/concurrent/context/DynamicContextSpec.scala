package com.srivastavavivekggn.scala.util.concurrent.context

import com.srivastavavivekggn.scala.util.BaseAsyncUtilSpec
import com.srivastavavivekggn.scala.util.concurrent.AsyncUtils
import com.srivastavavivekggn.scala.util.lang.StringUtils
import org.slf4j.MDC

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class DynamicContextSpec extends BaseAsyncUtilSpec {

  private val adminName = "testman"

  private val global = AsyncUtils.Contexts.global
  private val scalaUtilities = AsyncUtils.ContextsIO


  behavior of "DynamicContext"

  it should "pass contexts through thread pools" in {

    DynamicContext.Audit.withValues(Map(DynamicContext.Audit.ADMIN -> adminName)) {
      Future {

        val current = DynamicContext.Audit.getValue(DynamicContext.Audit.ADMIN, classOf[String])
        assert(StringUtils.isEqual(current, adminName))

        val inner = Future {  
          val current = DynamicContext.Audit.getValue(DynamicContext.Audit.ADMIN, classOf[String])
          assert(StringUtils.isEqual(current, adminName))

          current
        }

        Await.result(inner, Duration.Inf)
      }(global)
    }.map(outer => assert(outer.isDefined))
  }


  it should "set/unset the xFlow in the MDC" in {

    val xflow = "testflow"

    // ensure it is empty to begin with
    assert(StringUtils.isEmpty(MDC.get(DynamicContext.Flow.XFLOW)))

    DynamicContext.Flow.withValues(Map(DynamicContext.Flow.XFLOW -> xflow)) {

      Future {

        val ctx = DynamicContext.Flow.getContext
        val outer = DynamicContext.Flow.getValue(DynamicContext.Flow.XFLOW, classOf[String])
        assert(StringUtils.isEqual(outer, xflow))
        assert(StringUtils.isEqual(outer, MDC.get(DynamicContext.Flow.XFLOW)))

        val inner = Future {
          val current = DynamicContext.Flow.getValue(DynamicContext.Flow.XFLOW, classOf[String])
          assert(StringUtils.isEqual(current, xflow))
          assert(StringUtils.isEqual(current, MDC.get(DynamicContext.Flow.XFLOW)))

          current
        }

        Await.result(inner, Duration.Inf)
      }(global)

    }.map(outer => assert(outer.isDefined))
  }
}
