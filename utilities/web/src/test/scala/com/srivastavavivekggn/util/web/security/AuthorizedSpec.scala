package com.srivastavavivekggn.scala.util.web.security

import com.srivastavavivekggn.scala.util.concurrent.AsyncUtils
import com.srivastavavivekggn.scala.util.test.SimpleAsyncFlatSpec
import com.srivastavavivekggn.scala.util.web.security.Authorized.AuthConfig

import javax.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders
import org.springframework.mock.web.MockHttpServletRequest

import scala.concurrent.{ExecutionContext, Future}

class AuthorizedSpec extends SimpleAsyncFlatSpec {

  // simple body function that returns successful assertion
  val futureSuccess = (auth: AuthorizedRequest) => {
    Future.successful(assert(true))
  }

  val alwaysPassAuthClient = new AuthClient {
    override def isAuthorized(request: HttpServletRequest,
                              authHeader: String,
                              action: String,
                              resource: String,
                              config: AuthConfig)(implicit ex: ExecutionContext): Future[AuthResult] = {
      Future.successful(AuthSuccess(authMethod = ""))
    }
  }

  val alwaysFailAuthClient = new AuthClient {
    override def isAuthorized(request: HttpServletRequest,
                              authHeader: String,
                              action: String,
                              resource: String,
                              config: AuthConfig)(implicit ex: ExecutionContext): Future[AuthResult] = {
      Future.successful(AuthFailure())
    }
  }

  behavior of "AuthorizedSpec"

  it should "successfully authenticate" in {
    val ac = AuthConfig("Bearer")
    val req = new MockHttpServletRequest()
    req.addHeader(HttpHeaders.AUTHORIZATION, "Bearer sdlkjflsdkj")

    Authorized(futureSuccess)(alwaysPassAuthClient, ac, req, AsyncUtils.ContextsIO)
  }

  it should "fail based on bad auth type" in {
    val ac = AuthConfig("Basic")
    val req = new MockHttpServletRequest()
    req.addHeader(HttpHeaders.AUTHORIZATION, "Bearer sdlkjflsdkj")

    try {
      Authorized(futureSuccess)(alwaysPassAuthClient, ac, req, AsyncUtils.ContextsIO)
    }
    catch {
      case _: AccessDeniedException => Future.successful(assert(true))
      case e: Exception => Future.failed(e)
    }
  }


  it should "not call the body function on auth failure" in {

    val ac = AuthConfig("Bearer")
    val req = new MockHttpServletRequest()
    req.addHeader(HttpHeaders.AUTHORIZATION, "Bearer sdlkjflsdkj")

    var marker = false

    val shouldNotBeCalled = (auth: AuthorizedRequest) => {
      marker = true
      Future.successful(assert(false, "Body function should not have been called"))
    }

    Authorized(shouldNotBeCalled)(alwaysFailAuthClient, ac, req, AsyncUtils.ContextsIO)
      .recoverWith {
        // we got access denied, and the body function was not called
        case _: AccessDeniedException if !marker => Future.successful(assert(true))
        case e: Exception => Future.failed(e)
      }
  }
}
