package com.srivastavavivekggn.scala.util.web.security

import java.security.Principal

import com.srivastavavivekggn.scala.util.lang.StringUtils
import javax.servlet.http.{HttpServletRequest, HttpServletRequestWrapper}
import org.springframework.http.HttpHeaders


case class AuthorizedRequest(request: HttpServletRequest,
                             principal: String,
                             authMethod: String,
                             private val secureId: String = null) extends HttpServletRequestWrapper(request) {

  override def getUserPrincipal = new Principal() {
    override def getName = Option(principal).getOrElse(secureId)
  }

  def hasSecureId: Boolean = StringUtils.isNotEmpty(secureId)

  def getSecureId: String = secureId

  def getSecureIdOpt: Option[String] = StringUtils.nonEmpty(secureId, trim = true)

  def getEntityOpt: Option[String] = StringUtils.nonEmpty(Option(request.getParameter("entity")))

  def getAuthHeaderOpt: Option[String] = StringUtils.nonEmpty(request.getHeader(HttpHeaders.AUTHORIZATION), trim = true)
}
