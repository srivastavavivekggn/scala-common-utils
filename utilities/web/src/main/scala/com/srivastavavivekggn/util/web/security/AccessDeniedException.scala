package com.srivastavavivekggn.scala.util.web.security

class AccessDeniedException(message: String, cause: Throwable, val status: Option[Int]=None) extends RuntimeException(message, cause) {

  def this(message: String) = this(message, null)

}
