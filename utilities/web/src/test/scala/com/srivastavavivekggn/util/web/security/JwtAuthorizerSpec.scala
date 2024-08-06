package com.srivastavavivekggn.scala.util.web.security

import com.srivastavavivekggn.scala.util.test.SimpleFlatSpec

class JwtAuthorizerSpec extends SimpleFlatSpec {

  // setup the authorizer
  val authorizer = JwtAuthorizer("HMF6yBp+psF0jg/mVO4BDNwkG2tHuFc2U/FqLpBBl9Q=", 1209600)

  val user = "test.user"

  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  behavior of "JwtAuthorizer.createJwt"
  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

  it should "create a methodAndUrl token when no format is provided" in {

    val result = authorizer.createJwt(
      user,
      "GET:https://api.qa.xxxxx.com/program",
      "get:https://api.qa.xxxxx.com/member/program",
      "pOST:https://api.qa.xxxxx.com/program",
      "deLetE:https://api.qa.xxxxx.com/program"
    )

    val claims = authorizer.getClaims(result._1)

    assert(claims.get("format").contains(JwtTokenFormat.METHOD_AND_URL.name))
    assert(claims.contains("https://api.qa.xxxxx.com/program"))
    assert(claims.contains("https://api.qa.xxxxx.com/member/program"))
    assert(claims.get("https://api.qa.xxxxx.com/program").contains("DELETE,GET,POST"))
  }


  it should "create a legacy JWT format" in {
    val result = authorizer.createJwt(
      JwtTokenFormat.LEGACY,
      user,
      "GET:https://api.qa.xxxxx.com/program",
      "GET:https://api.qa.xxxxx.com/member/program",
      "POST:https://api.qa.xxxxx.com/program",
      "DELETE:https://api.qa.xxxxx.com/program"
    )

    val claims = authorizer.getClaims(result._1)

    assert(claims.get("format").contains(JwtTokenFormat.LEGACY.name))
    assert(claims.contains("GET:https://api.qa.xxxxx.com/program"))
    assert(claims.contains("GET:https://api.qa.xxxxx.com/member/program"))
    assert(claims.contains("DELETE:https://api.qa.xxxxx.com/program"))
  }


  it should "create a manual key/value JWT" in {
    val result = authorizer.createJwt(
      user,
      ("alpha", "beta"),
      ("gamma", "delta"),
      ("phi", "omega")
    )

    val claims = authorizer.getClaims(result._1)

    assert(claims.get("format").contains(JwtTokenFormat.MANUAL.name))
    assert(claims.get("alpha").contains("beta"))
    assert(claims.get("gamma").contains("delta"))
    assert(claims.get("phi").contains("omega"))
  }

  it should "throw an exception when trying to create a Manual format via the legacy string method" in {
    assertThrows[IllegalArgumentException] {
      authorizer.createJwt(JwtTokenFormat.MANUAL, user, "GET:https://api.qa.xxxxx.com/program")
    }
  }

  it should "throw an exception if attempting to override a standard JWT claim" in {
    assertThrows[IllegalArgumentException] {
      authorizer.createJwt(JwtTokenFormat.METHOD_AND_URL, user, ("test", "valid"), ("sub", "alpha"))
    }
  }

  it should "create a JWT with complex claims" in {

    val subMap: java.util.Map[String, String] = new java.util.HashMap()
    subMap.put("XXXX", "test")

    val result = authorizer.createJwt(user, ("test", subMap))

    val parsed = authorizer.getClaims(result._1)

    parsed.get("test") match {
      case Some(map: java.util.HashMap[String, String]) => assert("test".equals(map.get("XXXX")))
      case _ => fail("Expected hash map claim")
    }
  }

  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  behavior of "JwtAuthorizer.validateJwt"
  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

  it should "perform legacy validation logic" in {
    val result = authorizer.createJwt(
      JwtTokenFormat.LEGACY,
      user,
      "GET:https://api.qa.xxxxx.com/program",
      "GET:https://api.qa.xxxxx.com/member/program",
      "POST:https://api.qa.xxxxx.com/program",
      "DELETE:https://api.qa.xxxxx.com/program"
    )

    val success1 = authorizer.validateJwt(result._1, "GET:https://api.qa.xxxxx.com/program")
    val success2 = authorizer.validateJwt(result._1, "POST:https://api.qa.xxxxx.com/program", "DELETE:https://api.qa.xxxxx.com/program")
    assert(success1.isInstanceOf[AuthSuccess])
    assert(success2.isInstanceOf[AuthSuccess])

    val failure1 = authorizer.validateJwt(result._1, "GET:https://api.qa.xxxxx.comdestination")
    val failure2 = authorizer.validateJwt(result._1, "GET:https://api.qa.xxxxx.com/program", "PUT:https://api.qa.xxxxx.com/program")
    assert(failure1.isInstanceOf[AuthFailure])
    assert(failure2.isInstanceOf[AuthFailure])
  }

  it should "perform method+url validation logic" in {

    val result = authorizer.createJwt(
      JwtTokenFormat.METHOD_AND_URL,
      user,
      "GET:https://api.qa.xxxxx.com/program",
      "GET:https://api.qa.xxxxx.com/member/program",
      "POST:https://api.qa.xxxxx.com/program",
      "DELETE:https://api.qa.xxxxx.com/program"
    )

    val success1 = authorizer.validateJwt(result._1, "GET:https://api.qa.xxxxx.com/program")
    val success2 = authorizer.validateJwt(result._1, "POST:https://api.qa.xxxxx.com/program", "DELETE:https://api.qa.xxxxx.com/program")
    assert(success1.isInstanceOf[AuthSuccess])
    assert(success2.isInstanceOf[AuthSuccess])

    val failure1 = authorizer.validateJwt(result._1, "GET:https://api.qa.xxxxx.comdestination")
    val failure2 = authorizer.validateJwt(result._1, "GET:https://api.qa.xxxxx.com/program", "PUT:https://api.qa.xxxxx.com/program")
    assert(failure1.isInstanceOf[AuthFailure])
    assert(failure2.isInstanceOf[AuthFailure])
  }

  it should "perform manual validation logic in" in {

    val result = authorizer.createJwt(
      user,
      ("alpha", "beta"),
      ("gamma", "delta"),
      ("phi", "omega")
    )

    val success1 = authorizer.validateJwt(result._1, JwtClaimMatcher(_ => true, _ => true).tuple)
    val success2 = authorizer.validateJwt(result._1, ((claimKey: String) => claimKey.equals("alpha"), (claimValue: AnyRef) => "beta".equals(claimValue)))
    val success3 = authorizer.validateJwt(result._1,
      ((claimKey: String) => claimKey.equals("alpha"), (claimValue: AnyRef) => "beta".equals(claimValue)),
      ((claimKey: String) => claimKey.equals("gamma"), (claimValue: AnyRef) => "delta".equals(claimValue)),
      ((claimKey: String) => claimKey.equals("phi"), (claimValue: AnyRef) => "omega".equals(claimValue))
    )

    assert(success1.isInstanceOf[AuthSuccess])
    assert(success2.isInstanceOf[AuthSuccess])
    assert(success3.isInstanceOf[AuthSuccess])

    val failure1 = authorizer.validateJwt(result._1, JwtClaimMatcher(_ => false, _ => true).tuple)
    val failure2 = authorizer.validateJwt(result._1, JwtClaimMatcher(_ => true, _ => false).tuple)
    val failure3 = authorizer.validateJwt(result._1, ((claimKey: String) => claimKey.equals("gamma"), (claimValue: AnyRef) => "omega".equals(claimValue)))
    val failure4 = authorizer.validateJwt(result._1,
      ((claimKey: String) => claimKey.equals("alpha"), (claimValue: AnyRef) => "beta".equals(claimValue)),
      ((claimKey: String) => claimKey.equals("gamma"), (claimValue: AnyRef) => "omega".equals(claimValue))
    )

    assert(failure1.isInstanceOf[AuthFailure])
    assert(failure2.isInstanceOf[AuthFailure])
    assert(failure3.isInstanceOf[AuthFailure])
    assert(failure4.isInstanceOf[AuthFailure])
  }

  it should "throw an exception if using a manual token with legacy validation method" in {

    val result = authorizer.createJwt(
      user,
      ("alpha", "beta"),
      ("gamma", "delta"),
      ("phi", "omega")
    )

    assertThrows[IllegalArgumentException] {
      authorizer.validateJwt(result._1, "alpha:beta")
    }
  }


  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  behavior of "JwtAuthorizer.refreshJwt"
  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

  it should "copy over all claims into a new JWT" in {

    // create token and get claims
    val result = authorizer.createJwt(
      user,
      "GET:https://api.qa.xxxxx.com/program",
      "get:https://api.qa.xxxxx.com/member/program",
      "pOST:https://api.qa.xxxxx.com/program",
      "deLetE:https://api.qa.xxxxx.com/program"
    )
    val claims = authorizer.getClaims(result._1)


    // create manual token and get claims
    val manualResult = authorizer.createJwt(
      user,
      ("alpha", "beta"),
      ("gamma", "delta"),
      ("phi", "omega")
    )
    val manualClaims = authorizer.getClaims(manualResult._1)

    val refreshed = authorizer.refreshJwt(result._1)
    val refreshedManual = authorizer.refreshJwt(manualResult._1);

    // original dates should be less than or equal to refreshed dates
    assert(result._2.compareTo(refreshed._2) <= 0)
    assert(manualResult._2.compareTo(refreshedManual._2) <= 0)

    // ensure claims are the same
    assertResult(claims)(authorizer.getClaims(refreshed._1))
    assertResult(manualClaims)(authorizer.getClaims(refreshedManual._1))
  }
}
