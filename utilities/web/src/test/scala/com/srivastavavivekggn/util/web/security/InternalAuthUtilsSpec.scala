package com.srivastavavivekggn.scala.util.web.security

import com.srivastavavivekggn.scala.util.test.SimpleFlatSpec
import org.springframework.http.HttpHeaders
import org.springframework.mock.web.MockHttpServletRequest

class InternalAuthUtilsSpec extends SimpleFlatSpec {

  val service = "scalaUtilities"
  val secret1 = "abc123"
  val secret2 = "def456"

  private def mockRequest: MockHttpServletRequest = {
    val request = new MockHttpServletRequest();
    request.setScheme("https")
    request.setServerName("api.xxxxx.com")
    request.setServerPort(443)
    request.setRequestURI("")
    request.addParameter("q1", "a")
    request.addParameter("q2", "b")
    request.setMethod("GET")
    request
  }

  behavior of "InternalAuthUtils.generateHeaders"

  it should "generate headers properly" in {

    val req = mockRequest
    val headers = InternalAuthUtils.generateHeaders(secret1, service, req)

    assert(headers.get(HttpHeaders.AUTHORIZATION).get(0).startsWith(InternalAuthUtils.internalAuth))
    assert(!headers.get("x-ts").isEmpty)
    assert(!headers.get("x-rand").isEmpty)
  }


  it should "authorize request successfully" in {

    val req = mockRequest
    val headers = InternalAuthUtils.generateHeaders(secret1, service, req)
    headers.forEach((k, v) => req.addHeader(k, v.get(0)))

    InternalAuthUtils.authorize(secret1, "scalaUtilities", req)
  }

  it should "deny request when URL has changed" in {

    val req = mockRequest
    val headers = InternalAuthUtils.generateHeaders(secret1, service, req)
    headers.forEach((k, v) => req.addHeader(k, v.get(0)))

    req.setRequestURI("/not")

    intercept[AccessDeniedException] {
      InternalAuthUtils.authorize(secret1, "scalaUtilities", req)
    }
  }

  it should "authorize properly using multiple secrets" in {

    val req = mockRequest
    val headers = InternalAuthUtils.generateHeaders(secret2, service, req)
    headers.forEach((k, v) => req.addHeader(k, v.get(0)))

    InternalAuthUtils.authorize(Array(secret1, secret2, "abacd"), "scalaUtilities", req)
  }

  it should "deny access properly using multiple secrets" in {

    val req = mockRequest
    val headers = InternalAuthUtils.generateHeaders(secret2, service, req)
    headers.forEach((k, v) => req.addHeader(k, v.get(0)))

    intercept[AccessDeniedException] {
      InternalAuthUtils.authorize(Array("sdlkfjds", "sfkjdsf", "sdfksjfs", "yrurtee"), "scalaUtilities", req)
    }
  }


  it should "authorize using direct method and url" in {

    // authorize the mock request
    val req = mockRequest
    val headers = InternalAuthUtils.generateHeaders(secret2, service, req)
    headers.forEach((k, v) => req.addHeader(k, v.get(0)))

    // validate using method and URL
    InternalAuthUtils.authorize(secret2, "scalaUtilities", req, "GET", "https://api.xxxxx.com")
  }

  it should "fail to authorize using direct method and url" in {

    // authorize the mock request
    val req = mockRequest
    val headers = InternalAuthUtils.generateHeaders(secret2, service, req)
    headers.forEach((k, v) => req.addHeader(k, v.get(0)))

    // send wrong method
    intercept[AccessDeniedException] {
      InternalAuthUtils.authorize(secret2, "scalaUtilities", req, "POST", "https://api.xxxxx.com")
    }
  }
}
