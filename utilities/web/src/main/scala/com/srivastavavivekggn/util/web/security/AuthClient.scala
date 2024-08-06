package com.srivastavavivekggn.scala.util.web.security

import com.srivastavavivekggn.scala.util.web.security.Authorized.AuthConfig
import javax.servlet.http.HttpServletRequest

import scala.concurrent.{ExecutionContext, Future}

/**
  * The abstract AuthResult class and corresponding Success and Failure types
  */
sealed abstract class AuthResult

case class AuthSuccess(user: String = null, secureId: String = null, authMethod: String) extends AuthResult

case class AuthFailure(reason: String = "", ex: Throwable = null, status: Option[Int] = None) extends AuthResult

case class AuthRequired() extends AuthResult

case class AuthException(ex: Throwable) extends AuthResult


/**
  * AuthClient interface -- requires only a single method to be implemented
  * for authorizing API requests
  */
trait AuthClient {

  /**
    * Check that the provided auth header (Basic, Bearer, etc.) is allowed to access the resource(s)
    * at the give requestURL and method type
    *
    * @param request    the http request
    * @param authHeader the Authorization header
    * @param action     the action being performed
    * @param resource   the resource being acted upon
    * @return an { @see AuthResult} value - either AuthSuccess or AuthFailure
    */
  def isAuthorized(request: HttpServletRequest,
                   authHeader: String,
                   action: String,
                   resource: String,
                   config: AuthConfig)(implicit ex: ExecutionContext): Future[AuthResult]
}

object AuthClient {
  final val BEARER = "Bearer"

  final val SSO = "SSO"

  final val BASIC = "Basic"

  final val JWT = "JWT"

  final val ALL = Seq(BEARER, SSO, BASIC, JWT)
}