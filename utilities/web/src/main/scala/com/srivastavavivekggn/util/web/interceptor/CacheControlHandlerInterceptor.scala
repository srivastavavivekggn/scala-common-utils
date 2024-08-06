package com.srivastavavivekggn.scala.util.web.interceptor

import java.util.{Calendar, GregorianCalendar, TimeZone}
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import com.srivastavavivekggn.scala.util.web.cachecontrol.{CacheControl, CachePolicy}
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter


/**
  * Interceptor to add a request header for Cache-Control. This will scan the handler Object
  * and looks for the @annotation "CacheControl", it will use the values from the annotation
  * to set the values for the header "cache-control". If no annotation is found it will create a
  * default header with the value of "no-cache, no-store, must-revalidate".
  *
  */
class CacheControlHandlerInterceptor extends HandlerInterceptorAdapter with HandlerInterceptor {

  private val HEADER_EXPIRES = "Expires"
  private val HEADER_CACHE_CONTROL = "Cache-Control"

  private var useExpiresHeader = true

  /**
    * Assigns a CacheControl header to the given response.
    *
    * @param request  the HttpServletRequest
    * @param response the HttpServletResponse
    * @param handler  the handler for the given request
    */
  protected def setCacheControlHeader(request: HttpServletRequest, response: HttpServletResponse, handler: Object): Unit = {
    val cacheControl = this.getCacheControl(request, response, handler)
    val cacheControlHeader = this.generateCacheControlHeader(cacheControl)

    Option(cacheControlHeader) match {
      case Some(header) =>
        response.setHeader(HEADER_CACHE_CONTROL, cacheControlHeader)
        if (useExpiresHeader) response.setDateHeader(HEADER_EXPIRES, generateExpiresHeader(cacheControl))

      // set the default as no-cache
      case _ => response.setHeader(HEADER_CACHE_CONTROL, createDefaultCacheControlHeader())
    }
  }

  /**
    * Returns the default cache control header value
    *
    * @return the cache control header value
    */
  protected def createDefaultCacheControlHeader(): String = {
    Seq(
      CachePolicy.NO_CACHE,
      CachePolicy.NO_STORE,
      s"${CachePolicy.MAX_AGE}=0",
      CachePolicy.MUST_REVALIDATE
    ).mkString(", ")
  }

  /**
    * Returns cache control header value from the given com.srivastavavivekggn.scala.util.web.cachecontrol.CacheControl
    * annotation.
    *
    * @param cacheControl the CacheControl annotation from which to
    *                     create the returned cache control header value
    * @return the cache control header value
    */
  protected def generateCacheControlHeader(cacheControl: CacheControl): String = {

    Option(cacheControl)
      .filterNot(c => c.maxAge() >= 0 || c.sharedMaxAge() >= 0 || c.policy().isEmpty)
      .map(control => {

        val maxAge = if (control.maxAge >= 0) {
          Seq(s"${CachePolicy.MAX_AGE}=${control.maxAge}")
        }
        else {
          Seq.empty
        }

        val sharedMax = if (cacheControl.sharedMaxAge >= 0) {
          Seq(s"s-maxage=${control.sharedMaxAge()}")
        }
        else {
          Seq.empty
        }

        val policies = Option(control.policy()).getOrElse(Array.empty)

        (maxAge ++ sharedMax ++ policies).mkString(", ")
      }).orNull
  }

  /**
    * Returns an expires header value generated from the given com.srivastavavivekggn.scala.util.web.cachecontrol.CacheControl annotation.
    *
    * @param cacheControl the CacheControl annotation from which to
    *                     create the returned expires header value
    * @return the expires header value
    */
  protected def generateExpiresHeader(cacheControl: CacheControl): Long = {
    val expires = new GregorianCalendar(TimeZone.getTimeZone("GMT"))
    if (cacheControl.maxAge >= 0) expires.add(Calendar.SECOND, cacheControl.maxAge)
    expires.getTime.getTime
  }

  /**
    * Returns the annotation specified for the given request, response and handler.
    *
    * @param request  the current HttpServletRequest
    * @param response the current HttpServletResponse
    * @param handler  the current request handler
    * @return the CacheControl annotation specified by the given handler if present; null otherwise
    */
  def getCacheControl(request: HttpServletRequest, response: HttpServletResponse, handler: Object): CacheControl = {

    Option(handler)
      .filter(_.isInstanceOf[HandlerMethod])
      .map(handle => {

        val handlerMethod = handle.asInstanceOf[HandlerMethod]
        val cacheControl = handlerMethod.getMethodAnnotation(classOf[CacheControl])

        Option(cacheControl) match {
          case Some(cache) => cache
          case _ => handlerMethod.getBeanType.getAnnotation(classOf[CacheControl])
        }

      }).orNull
  }

  /**
    * True to set an expires header when a com.srivastavavivekggn.scala.util.web.cachecontrol.CacheControl annotation is present on a handler; false otherwise.
    * Defaults to true.
    *
    * @param useExpiresHeader true to set an expires header when a CacheControl annotation is present on a handler;
    *                         false otherwise
    */
  def setUseExpiresHeader(useExpiresHeader: Boolean): Unit = {
    this.useExpiresHeader = useExpiresHeader
  }


  @throws[Exception]
  override def preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Object): Boolean = {

    // add the cache control header
    this.setCacheControlHeader(request, response, handler)

    super.preHandle(request, response, handler)
  }

}
