package com.srivastavavivekggn.scala.util.web.security

import com.srivastavavivekggn.scala.util.concurrent.AsyncUtils
import com.srivastavavivekggn.scala.util.concurrent.context.DynamicContext
import com.srivastavavivekggn.scala.util.lang.StringUtils
import com.srivastavavivekggn.scala.util.web.xflow.XFlowUtils
import javax.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders

import scala.concurrent.{ExecutionContext, Future}

object Authorized {

  /**
    * Generic auth config trait, implementations may vary
    */
  trait AuthConfig {
    def isValidAuthType(authHeader: String): Boolean

    def allowedTypes: Seq[String]
  }

  /**
    * Companion object
    */
  object AuthConfig {
    /**
      * Maintains backwards compatibility
      *
      * @param authTypes the auth types
      * @return the SimpleAuthConfig
      */
    def apply(authTypes: String*): SimpleAuthConfig = {
      SimpleAuthConfig(authTypes: _*)
    }
  }

  /**
    * Simple wrapper for auth types collection that can be implicitly defined on a class level
    *
    * @param authTypes the auth types
    */
  case class SimpleAuthConfig(authTypes: String*) extends AuthConfig {

    override def allowedTypes: Seq[String] = authTypes

    override def isValidAuthType(authHeader: String): Boolean = {
      StringUtils.isEmpty(authHeader) || authTypes.isEmpty || authTypes.exists(authHeader.startsWith)
    }
  }

  /**
    * Allows composition of multiple authConfigs
    *
    * @param delegates the auth configs to delegate to
    */
  case class CompositeAuthConfig(delegates: AuthConfig*) extends AuthConfig {

    override def allowedTypes: Seq[String] = delegates.flatMap(_.allowedTypes).distinct

    override def isValidAuthType(authHeader: String): Boolean = {
      delegates.exists(_.isValidAuthType(authHeader))
    }

    def getConfigsForType(authHeader: String): Seq[AuthConfig] = {
      delegates.filter(_.isValidAuthType(authHeader))
    }
  }

  /**
    * Auth needed exception
    */
  final private lazy val AUTH_NEEDED = new AuthorizationRequiredException("Expected authorization but not found")

  /**
    * Internal method that performs authorization and if successful, executes the body
    *
    * @param body       the block to be executed only if authorization is successful
    * @param authHeader the authorization header
    * @param action     the action being performed
    * @param resource   the resource being acted upon
    * @param ctx        the implicit execution context
    * @param authClient the implicit authClient
    * @tparam T the body return type
    * @return the wrapped body future
    */
  private def wrapBody[T](body: AuthorizedRequest => Future[T],
                          authHeader: String,
                          action: String,
                          resource: String
                         )(implicit authClient: AuthClient,
                           request: HttpServletRequest,
                           authConfig: AuthConfig,
                           ctx: ExecutionContext): Future[T] = {

    // now, let the client determine if the auth is valid
    authClient.isAuthorized(request, authHeader, action, resource, authConfig)(AsyncUtils.ContextsIO)
      .flatMap {

        // auth was successful, call the body function
        case s: AuthSuccess => DynamicContext.withAudit(s.user, s.authMethod) {
          body(
            AuthorizedRequest(request, principal = s.user, authMethod = s.authMethod, secureId = s.secureId)
          )
        }

        // failure cases
        case f: AuthFailure => throw new AccessDeniedException(f.reason, f.ex, f.status)
        case e: AuthException => throw new RuntimeException("AuthException", e.ex)
        case _: AuthRequired => throw AUTH_NEEDED
      }
  }

  /**
    * Wrap the method body using only implicits
    *
    * @param f          the method body
    * @param authClient the auth client
    * @param request    the implicit http request
    * @param ctx        the exec context
    * @tparam T the body return type
    * @return the wrapped method body
    */
  def apply[T](f: AuthorizedRequest => Future[T])(implicit authClient: AuthClient,
                                                  authConfig: AuthConfig,
                                                  request: HttpServletRequest,
                                                  ctx: ExecutionContext): Future[T] = {
    apply(request)(f)
  }

  /**
    * Wrap the method body using implicits and defined authConfig
    *
    * @param authConfig
    * @param f
    * @param authClient
    * @param request
    * @param ctx
    * @tparam T
    * @return
    */
  def apply[T](authConfig: AuthConfig)(f: AuthorizedRequest => Future[T])(implicit authClient: AuthClient,
                                                                          request: HttpServletRequest,
                                                                          ctx: ExecutionContext): Future[T] = {
    apply(request, authConfig)(f)
  }

  /**
    * Alternate method that accepts an implicit authConfig
    *
    * @param request    the http request
    * @param f          the body fn
    * @param authClient the auth client
    * @param authConfig the auth config
    * @param ctx        the exec context
    * @tparam T the body return type
    * @return the body
    */
  def apply[T](request: HttpServletRequest)(f: AuthorizedRequest => Future[T])(implicit authClient: AuthClient,
                                                                               authConfig: AuthConfig,
                                                                               ctx: ExecutionContext): Future[T] = {
    apply(request, authConfig)(f)
  }

  /**
    * Wrap method body using the given request
    *
    * @param request    the request
    * @param authConfig the auth config
    * @param f          the body fn
    * @param authClient the auth client
    * @param ctx        the execution context
    * @tparam T the body return type
    * @return the body result
    */
  def apply[T](request: HttpServletRequest,
               authConfig: AuthConfig)(f: AuthorizedRequest => Future[T])(implicit authClient: AuthClient,
                                                                          ctx: ExecutionContext): Future[T] = {
    apply(
      request,
      request.getHeader(HttpHeaders.AUTHORIZATION)
    )(f)(authClient, authConfig, ctx)
  }

  /**
    * Wrap method body using given request
    *
    * @param request    the http request
    * @param authHeader the authorization header
    * @param f          the body fn
    * @param authClient the auth client
    * @param ctx        the execution context
    * @tparam T the body return type
    * @return the body result
    */
  def apply[T](request: HttpServletRequest,
               authHeader: String)(f: AuthorizedRequest => Future[T])(implicit authClient: AuthClient,
                                                                      authConfig: AuthConfig,
                                                                      ctx: ExecutionContext): Future[T] = {
    apply(
      authHeader,
      request.getMethod,
      request.getRequestURL.toString
    )(f)(authClient, request, authConfig, ctx)
  }


  /**
    * Wrap method body using given request
    *
    * @param authHeader the auth header
    * @param action     the action
    * @param resource   the resource
    * @param authConfig the auth config
    * @param f          the body function
    * @param authClient the auth client
    * @param request    the http request
    * @param ctx        the execution context
    * @tparam T the return type
    * @return the result of the body execution
    */
  def apply[T](authHeader: String,
               action: String,
               resource: String,
               authConfig: AuthConfig
              )(f: AuthorizedRequest => Future[T])(implicit authClient: AuthClient,
                                                   request: HttpServletRequest,
                                                   ctx: ExecutionContext): Future[T] = {
    apply(
      authHeader,
      action,
      resource
    )(f)(authClient, request, authConfig, ctx)
  }

  /**
    * Wrap method body, passing all needed params manually
    *
    * @param authHeader the auth header
    * @param action     the action
    * @param resource   the resource
    * @param f          the method body
    * @param authClient the auth client
    * @param ctx        the exec context
    * @tparam T the body return type
    * @return the wrapped body
    */
  def apply[T](authHeader: String,
               action: String,
               resource: String)(f: AuthorizedRequest => Future[T])(implicit authClient: AuthClient,
                                                                    request: HttpServletRequest,
                                                                    authTypes: AuthConfig,
                                                                    ctx: ExecutionContext): Future[T] = {
    DynamicContext.withTimedFlow(XFlowUtils.getXFlow(request)) {
      wrapBody(f, authHeader, action, resource)
    }
  }
}
