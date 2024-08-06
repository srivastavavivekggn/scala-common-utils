package com.srivastavavivekggn.scala.util.concurrent.trace

/**
  * Simple context object that can be defined and reused
  *
  * @param context the method context (e.g., class name)
  */
case class TraceableFutureContext(context: String)


object TraceableFutureContext {

  /**
    * Create a context from a class (uses the simple name)
    *
    * @param clazz the class
    * @return the context
    */
  def apply(clazz: Class[_]): TraceableFutureContext = TraceableFutureContext(clazz.getSimpleName)

}
