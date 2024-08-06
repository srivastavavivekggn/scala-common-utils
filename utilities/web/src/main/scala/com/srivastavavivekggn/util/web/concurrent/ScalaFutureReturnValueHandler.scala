package com.srivastavavivekggn.scala.util.web.concurrent

import org.springframework.core.MethodParameter
import org.springframework.util.ClassUtils
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.async.WebAsyncUtils
import org.springframework.web.method.support.{AsyncHandlerMethodReturnValueHandler, ModelAndViewContainer}

import scala.concurrent.Future

/**
  * Allows Spring to handle scala.concurrent.Future return types from @Controller/@RestControllers
  */
class ScalaFutureReturnValueHandler
  extends AsyncHandlerMethodReturnValueHandler {

  /**
    * Whether the given return value represents asynchronous computation.
    *
    * @param returnValue the return value
    * @param returnType  the return type
    * @return <code>true</code> if the return value is asynchronous.
    */
  override def isAsyncReturnValue(returnValue: Any,
                                  returnType: MethodParameter): Boolean = returnValue != null && supportsReturnType(returnType)

  /**
    * Whether the given <code>MethodParameter</code> method return type is
    * supported by this handler.
    *
    * @param returnType the method return type to check
    * @return <code>true</code> if this handler supports the supplied return type;
    *         <code>false</code> otherwise
    */
  override def supportsReturnType(returnType: MethodParameter): Boolean = ClassUtils.isAssignable(classOf[Future[_]], returnType.getParameterType)

  /**
    * Handle the given return value by adding attributes to the model and
    * setting a view or setting the
    * <code>ModelAndViewContainer.setRequestHandled</code> flag to <code>true</code>
    * to indicate the response has been handled directly.
    *
    * @param returnValue  the value returned from the handler method
    * @param returnType   the type of the return value. This type must have
    *                     previously been passed to { @link #supportsReturnType} which must
    *                     have returned <code>true</code>.
    * @param mavContainer the ModelAndViewContainer for the current request
    * @param webRequest   the current request
    */
  override def handleReturnValue(returnValue: Any,
                                 returnType: MethodParameter,
                                 mavContainer: ModelAndViewContainer,
                                 webRequest: NativeWebRequest): Unit = {

    if (returnValue == null) {
      mavContainer.setRequestHandled(true)
    }
    else {
      val future = classOf[Future[_]].cast(returnValue)

      WebAsyncUtils
        .getAsyncManager(webRequest)
        .startDeferredResultProcessing(
          ScalaFutureDeferredResult(future),
          mavContainer
        )
    }
  }
}
