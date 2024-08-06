package com.srivastavavivekggn.scala.util.concurrent

import java.util.concurrent.Executor

import com.srivastavavivekggn.scala.util.lang.NumberUtils
import com.srivastavavivekggn.scala.util.system.SystemUtils
import com.typesafe.scalalogging.Logger
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future, Promise}
import scala.util.{Failure, Success}

object AsyncUtils {

  /**
   * time to wait in seconds before threadpool shutdown
   * (this only works if the threadpool is exposed to Spring as a ThreadPoolTaskExecutor @Bean)
   */
  final val AWAIT_TERM_SECONDS = 60

  /**
   * The logger for this class
   */
  private lazy val logger = Logger.apply("AsyncUtils")

  /**
   * Error reporter for thethreadpool
   */
  private lazy val errorReporter = (t: Throwable) => logger.error("ThreadPool Error", t)

  /**
   * Executor contexts
   */
  object Contexts {

    /**
     * Implicit versions of the available execution contexts
     */
    object Implicit {

      /**
       * simple alias for Scala's global execution context
       */
      implicit lazy val global = DelegatingExecutionContextExecutor(scala.concurrent.ExecutionContext.global)

      /**
       * execution context using our custom thread pool executor
       */
      implicit lazy val IO = DelegatingExecutionContextExecutor(
        ExecutionContext.fromExecutor(ThreadPool, errorReporter)
      )
    }

    /**
     * simple alias for Scala's global execution context
     */
    def global: ExecutionContextExecutor = Implicit.global

    /**
     * custom context for IO operations
     */
    def IO: ExecutionContextExecutor = ImplicitIO

    /**
     * Custom thread pool definition.
     *
     * In order for the wait-on-shutdown logic to work properly, this thread pool
     * MUST be exposed as a @Bean in a Spring application context
     */
    lazy val ThreadPool: ThreadPoolTaskExecutor = {

      // base thread count
      val numThreads = SystemUtils.getIntMultiplier("concurrent.context.numThreads", "x1")

      // The hard limit on the number of active threads that the thread factory will produce
      val maxNoOfThreads = SystemUtils.getIntMultiplier("concurrent.context.maxThreads", "x1")

      val desiredParallelism = NumberUtils.range(
        SystemUtils.getIntMultiplier("concurrent.context.minThreads", "1"),
        numThreads,
        maxNoOfThreads)

      // ensure we have at least a count of 1
      val finalThreadCount = NumberUtils.range(1, desiredParallelism, desiredParallelism)

      // The thread factory must provide additional threads to support managed blocking.
      val maxExtraThreads = SystemUtils.getIntMultiplier("concurrent.context.maxExtraThreads", "256")

      val pool = new ThreadPoolTaskExecutor()
      pool.setCorePoolSize(finalThreadCount)
      pool.setMaxPoolSize(finalThreadCount + maxExtraThreads)
      pool.setDaemon(false)
      pool.setThreadNamePrefix("io-")
      pool.setWaitForTasksToCompleteOnShutdown(true)
      pool.setAwaitTerminationSeconds(SystemUtils.getIntProperty("concurrent.context.awaitTermSeconds", AWAIT_TERM_SECONDS))

      // initialize pool
      pool.initialize()

      // return the pool
      pool
    }

    /**
     * Get an execution context that uses a single-thread execution model (specifically, the current thread that calls
     * this method)
     *
     * @return the single-thread execution context
     */
    def currentThread: ExecutionContextExecutor = ExecutionContext.fromExecutor(new Executor {
      override def execute(command: Runnable): Unit = {
        command.run()
      }
    })
  }

  /**
   * Execute the given block of code in a single thread
   *
   * @param block            the block of code to execute
   * @param executionContext the execution context to use, defaults to global context
   * @tparam R the return type
   * @return a future of the result
   */
  def sequential[R](block: ExecutionContext => Future[R])(implicit executionContext: ExecutionContext = Contexts.global): Future[R] = {

    // create a promise
    val p = Promise[R]()

    // this future block gets executed in the passed implicit 'executionContext'
    Future {

      // execute the passed 'block' of code using a new single-thread context
      // this will run on the same thread as the parent future
      block(Contexts.currentThread)
        .onComplete {
          // success - resolve promise with result
          case Success(res) => p.success(res)

          // failure - resolve promise with failure
          case Failure(ex) => p.failure(ex)
        }
    }

    // return the promise's future
    p.future
  }

  /**
   * Execute a block of code using the given execution context
   *
   * @param block            the block of code to execute
   * @param executionContext the execution context
   * @tparam R the return type
   * @return the return future
   */
  def nonSequential[R](block: ExecutionContext => Future[R])(implicit executionContext: ExecutionContext = Contexts.global): Future[R] = {
    block(executionContext)
  }


  /**
   * Map over the given list of elements using the provided operation.  Utilizes Future.sequence to turn List[Future[_]]
   * into a Future[List]
   *
   * @param elements the elements to map
   * @param op       the operation
   * @param exec     the execution context
   * @tparam T the element type
   * @tparam Z the result type
   * @return the future List of result types
   */
  def map[T, Z](elements: List[T], op: T => Future[Z])(implicit exec: ExecutionContext): Future[List[Z]] = {
    Future.sequence(elements.map(op))
  }

  /**
   * Perform a mapping function on the given list of elements, where the mapping function returns a Future
   *
   * @param elements the elements to map over
   * @param op       the operation which takes the element type returns a Future
   * @param exec     the execution context
   * @tparam T the element type
   * @tparam Z the future type
   * @return a Future[List[Z]]
   **/
  def sequentialMap[T, Z](elements: List[T], op: T => Future[Z])(implicit exec: ExecutionContext): Future[List[Z]] = {
    elements match {

      // nothing passed, just return empty list
      case Nil => Future.successful(List.empty)

      // single element execute OP and return result as a List
      case head :: Nil => op(head).map(r => List(r))

      // 2 or more elements
      case head :: tail =>

        // takes the cumulative list and the next element to process
        // each subsequent step adds to the list and passes it along to the next step
        def foldFn(left: Future[List[Z]], right: T): Future[List[Z]] = {
          left.flatMap(l => op(right).map(r => l ++ List(r)))
        }

        // we use fold so we can flatmap our way down any number of elements sequentially
        // start with 'tail' because 'head' is used to seed the operation
        tail.foldLeft(
          op(head).map(h => List(h)) // perform 'op' on head as the seed for the fold operation
        )(foldFn)
    }
  }

  /**
   * Perform a sequential fold operation which passes the results to each subsequent
   *
   * @param elements the element list
   * @param op       the operation
   * @param exec     the execution context
   * @tparam T the incoming value type
   * @tparam Z the outgoing result type
   * @return the list of result values
   */
  def sequentialFold[T, Z](elements: List[T], op: (T, List[Z]) => Future[Z])(implicit exec: ExecutionContext): Future[List[Z]] = {
    elements match {

      // nothing passed, just return empty list
      case Nil => Future.successful(List.empty)

      // single element execute OP and return result as a List
      case head :: Nil => op(head, List.empty).map(r => List(r))

      // 2 or more elements
      case head :: tail =>

        // takes the cumulative list and the next element to process
        // each subsequent step adds to the list and passes it along to the next step
        def foldFn(left: Future[List[Z]], right: T): Future[List[Z]] = {
          left.flatMap(l => op(right, l).map(r => l ++ List(r)))
        }

        // we use fold so we can flatmap our way down any number of elements sequentially
        // start with 'tail' because 'head' is used to seed the operation
        tail.foldLeft(
          op(head, List.empty).map(h => List(h)) // perform 'op' on head as the seed for the fold operation
        )(foldFn)
    }
  }


  /**
   * Execute a series of operations on a single element
   *
   * @param element           the element to operate on
   * @param continueOnFailure true to continue even if an operation fails
   * @param ops               the operations to execute
   * @param exec              the execution context
   * @tparam E the element type
   * @return the element
   */
  def executeChain[E](element: E,
                      continueOnFailure: Boolean,
                      ops: ((E) => Future[E])*
                     )(implicit exec: ExecutionContext): Future[E] = {
    executeChain(element, continueOnFailure, (_: Throwable) => {}, ops: _*)
  }

  /**
   * Execute a series of operations on a single element
   *
   * @param element           the element to operate on
   * @param continueOnFailure true to continue even if an operation fails
   * @param errorReporter     the error reporter
   * @param ops               the operations to execute
   * @param exec              the execution context
   * @tparam E the element type
   * @return the element
   */
  def executeChain[E](element: E,
                      continueOnFailure: Boolean,
                      errorReporter: Throwable => Unit,
                      ops: ((E) => Future[E])*
                     )(implicit exec: ExecutionContext): Future[E] = {

    ops.foldLeft(Future.successful(element))((e, op) => {
      e.flatMap(elem => {
        op(elem).recoverWith {

          // if we want to continue on failure, just report and pass along the same element
          case e: Exception if continueOnFailure =>
            errorReporter(e)
            Future.successful(elem)

          case e: Exception =>
            errorReporter(e)
            Future.failed(e)
        }
      })
    })
  }
}
