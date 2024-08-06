package com.srivastavavivekggn.scala.util.web.interceptor

import javax.servlet.http.{HttpServletRequest, HttpServletResponse}

import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter

class RequestContextInterceptor extends HandlerInterceptorAdapter {

  override def preHandle(request: HttpServletRequest,
                         response: HttpServletResponse,
                         handler: scala.Any): Boolean = {

    val attributes = RequestContextHolder.getRequestAttributes
    RequestContextHolder.setRequestAttributes(attributes, true)

    true
  }
}
