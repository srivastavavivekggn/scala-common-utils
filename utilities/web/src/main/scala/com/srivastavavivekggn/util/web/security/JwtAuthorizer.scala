package com.srivastavavivekggn.scala.util.web.security

import com.srivastavavivekggn.scala.util.TypeAlias.JLong
import com.srivastavavivekggn.scala.util.collection.CollectionUtils
import com.srivastavavivekggn.scala.util.crypto.EncodeUtils
import com.srivastavavivekggn.scala.util.lang.{DateUtils, StringUtils}
import com.srivastavavivekggn.scala.util.web.security.JwtTokenFormat.{LEGACY, MANUAL, METHOD_AND_URL, forName}
import com.typesafe.scalalogging.StrictLogging
import io.jsonwebtoken.security.SignatureException
import io.jsonwebtoken.{Claims, Jws, Jwts, MissingClaimException}

import java.util.Date
import javax.crypto.spec.SecretKeySpec

/**
  * Utility for creating / authorizing JWT tokens
  *
  * @param jwtSecretKey the Base64 encoded secret key
  * @param ttl          the lifetime of a token, in seconds
  */
case class JwtAuthorizer(jwtSecretKey: String, ttl: JLong) extends StrictLogging {

  private final val FORMAT_KEY = "format"

  /**
    * Standard JWT claims
    */
  private final val standardClaims = List(
    Claims.ISSUER,
    Claims.SUBJECT,
    Claims.AUDIENCE,
    Claims.EXPIRATION,
    Claims.NOT_BEFORE,
    Claims.ISSUED_AT,
    Claims.ID
  )

  /**
    * Convert the string secret into an actual secret key instance
    *
    * @return the secret key spec
    */
  private def getKey = {
    val keyBytes = EncodeUtils.decode(jwtSecretKey)
    new SecretKeySpec(keyBytes, "HmacSHA256")
  }

  /**
    * Create a JWT for the given user and provided scopes.  This assumes
    * scopes are being provided in {Method}:{Url} format
    *
    * @param user             the user
    * @param scope            the scope
    * @param additionalScopes any additional scopes
    * @return the JWT string and expiration date
    */
  def createJwt(user: String, scope: String, additionalScopes: String*): (String, Date) = {
    createJwt(METHOD_AND_URL, user, scope, additionalScopes: _*)
  }

  /**
    * Create a JWT for the given user and provided scopes, in the specified format
    *
    * @param format           the jwt format
    * @param user             the user
    * @param scope            the scope
    * @param additionalScopes any additional scopes
    * @return the token and expiration date
    */
  def createJwt(format: JwtTokenFormat, user: String, scope: String, additionalScopes: String*): (String, Date) = {

    val allScopes = (Seq(scope) ++ additionalScopes).distinct

    val finalClaims: Seq[(String, AnyRef)] = format match {
      case LEGACY =>
        // add all the scopes as the claim key, with 'true' as the claim value
        allScopes.map(s => (s, "-"))

      // this is updated support for legacy-formatted scopes (i.e., {Method}:{Url})
      case METHOD_AND_URL =>
        scopesToUrlAndMethodTuples(allScopes)
          .view.mapValues(_.mkString(StringUtils.COMMA))
          .toList

      // this is not allowed, manual processing needs to use the dedicated methods
      case MANUAL => throw new IllegalArgumentException("MANUAL format must use createJwtWithKeyValueClaims")
    }

    createJwt(format, user, finalClaims: _*)
  }


  /**
    * Create a JWT for the given user and provided scopes. This provides more fine-grained
    * control over the key value
    *
    * @param user   the user
    * @param claims the jwt claims (tuples of key/value)
    * @return the JWT string
    */
  def createJwt(user: String, claims: (String, AnyRef)*): (String, Date) = {
    createJwt(MANUAL, user, claims: _*)
  }

  /**
    * Create a JWT for the given user and provided scopes. This provides more fine-grained
    * control over the key value
    *
    * @param format the token format
    * @param user   the user
    * @param claims the jwt claims (tuples of key/value)
    * @return the JWT string
    */
  def createJwt(format: JwtTokenFormat, user: String, claims: (String, AnyRef)*): (String, Date) = {

    // determine expiration
    val expirationDate = new Date(DateUtils.now.plusSeconds(ttl).toEpochMilli)

    // build the base JWT
    val b = Jwts.builder()
      .subject(user)
      .expiration(expirationDate)
      .claim(FORMAT_KEY, format.name)

    // add all the claims
    claims.foreach(s => {
      require(!standardClaims.contains(s._1), () => s"'${s._1}' is a JWT standard claim key and cannot be used")
      b.claim(s._1, s._2)
    })

    // return signed JWT and expiration date
    (b.signWith(getKey, Jwts.SIG.HS256).compact(), expirationDate)
  }

  /**
    * Get the user (subject) from the jwt
    *
    * @param token the existing jwt
    * @return the subject
    */
  def getUser(token: String): String = {
    val claims = parseJwt(token).getPayload
    claims.getSubject
  }

  /**
    * Get the token format
    *
    * @param token the token
    * @return the format
    */
  def getFormat(token: String): String = {
    val claims = parseJwt(token).getPayload
    claims.get(FORMAT_KEY, classOf[String])
  }

  /**
    * Extract claims
    *
    * @param token the token
    * @return the map of key/value pairs
    */
  def getClaims(token: String): Map[String, AnyRef] = {
    val claims = parseJwt(token).getPayload

    CollectionUtils.asScalaSetOrEmpty(claims.keySet())
      .filterNot(standardClaims.contains)
      .map(key => key -> claims.get(key, classOf[AnyRef]))
      .toMap
  }

  /**
    * Refresh a valid existing JWT
    *
    * @param existing the existing token
    * @return the new token
    */
  def refreshJwt(existing: String): (String, Date) = {

    val claims = parseJwt(existing).getPayload

    // determine which format this JWT is in based on the format claim (default to legacy)
    val tokenFormat: JwtTokenFormat = StringUtils.nonEmpty(
      claims.get(FORMAT_KEY, classOf[String]), trim = true
    ).flatMap(forName).getOrElse(LEGACY)

    // get all non-standard claims to copy over, leaving off format
    val keys = CollectionUtils.asScalaSetOrEmpty(claims.keySet())
      .filterNot(k => standardClaims.contains(k) || FORMAT_KEY.equals(k))
      .toList

    // create a new jwt with the same subject and scopes
    createJwt(tokenFormat, claims.getSubject, keys.map(k => k -> claims.get(k, classOf[AnyRef])): _*)
  }

  /**
    * Validate that the JWT contains the expected claims (uses a startsWith on the claim key)
    *
    * @param token                    the jwt token
    * @param expectedClaim            the expected claim
    * @param additionalExpectedClaims any additional expected claims
    * @return the auth result
    */
  def validateJwt(token: String, expectedClaim: String, additionalExpectedClaims: String*): AuthResult = {

    try {
      val result = parseJwt(token)

      // determine which format this JWT is in based on the format claim (default to legacy)
      val tokenFormat: JwtTokenFormat = StringUtils.nonEmpty(
        result.getPayload.get(FORMAT_KEY, classOf[String]), trim = true
      ).flatMap(forName).getOrElse(LEGACY)

      val allExpected = (Seq(expectedClaim) ++ additionalExpectedClaims).distinct

      // based on format, get the seq of claim validator functions
      val claimMatchers: Seq[(String => Boolean, AnyRef => Boolean)] = tokenFormat match {

        // legacy matching simply looks at claim key and uses a startsWith
        case LEGACY =>
          allExpected.map(expected => (claimKey => expected.startsWith(claimKey), _ => true))

        // updated methodAndUrl logic, checks that the URL matches (startsWith) and the method is accounted for
        case METHOD_AND_URL =>
          scopesToUrlAndMethodTuples(allExpected)
            .view
            .map(v => {
              val expectedMethodList = v._2
              val keyMatcher = (claimKey: String) => {
                v._1.startsWith(claimKey)
              }

              // ensure ALL http verbs are accounted for in the claim list
              val valueMatcher = (claimValue: AnyRef) => {
                val claimMethodList = claimValue.toString.split(StringUtils.COMMA)
                val intersect = claimMethodList.intersect(expectedMethodList)
                intersect.lengthCompare(expectedMethodList.size) == 0
              }

              (keyMatcher, valueMatcher)
            }).toSeq

        case MANUAL => throw new IllegalArgumentException("MANUAL format must use 'matchers' validation method")
      }

      validateJwt(token, claimMatchers: _*)
    }
    catch {
      case m: MissingClaimException => AuthException(m)
      case e: SignatureException => AuthException(e)
    }
  }


  def validateJwt(token: String, matchers: (String => Boolean, AnyRef => Boolean)*): AuthResult = {
    matchers.map(m => JwtClaimMatcher(m._1, m._2)).toList match {
      case Nil => AuthFailure("No permission requested")
      case head :: Nil => validateJwt(token, head)
      case head :: tail => validateJwt(token, head, tail: _*)
    }
  }

  /**
    * Validate that the JWT contains the expected claims (uses a startsWith on the claim key)
    *
    * @param token    the jwt token
    * @param matchers the set of claim key/value matchers
    * @return the auth result
    */
  def validateJwt(token: String, matcher: JwtClaimMatcher, additionalMatchers: JwtClaimMatcher*): AuthResult = {

    try {
      val result = parseJwt(token)

      val claims = CollectionUtils.asScalaSetOrEmpty(result.getPayload.keySet())

      def matchesClaim(claimKey: String, keyMatcher: String => Boolean, valueMatcher: AnyRef => Boolean): Boolean = {
        keyMatcher.apply(claimKey) &&
          valueMatcher.apply(result.getPayload.get(claimKey, classOf[AnyRef]))
      }

      // for each expectedClaim, there exists a JWT claim key that matches the beginning of the expectedClaim
      if ((Seq(matcher) ++ additionalMatchers).forall(matcher => claims.exists(c => matchesClaim(c, matcher._1, matcher._2)))) {
        AuthSuccess(user = result.getPayload.getSubject, authMethod = AuthClient.JWT)
      }
      else {
        logger.error(s"JWT Failure: user ${result.getPayload.getSubject} has ${claims.mkString(", ")}")
        AuthFailure("Permission not granted")
      }
    }
    catch {
      case m: MissingClaimException => AuthException(m)
      case e: SignatureException => AuthException(e)
    }
  }

  /**
    * Simple parse method
    *
    * @param token the token to parse
    * @return the claims
    */
  def parseJwt(token: String): Jws[Claims] = {
    Jwts.parser.verifyWith(getKey).build().parseSignedClaims(token)
  }

  /**
    * Simple utility for parsing {Method}:{Url} scopes into a map where the URL is the key and the
    * set of Methods is the value
    *
    * @param scopes the scopes
    * @return the map
    */
  private def scopesToUrlAndMethodTuples(scopes: Seq[String]): Map[String, Seq[String]] = {
    scopes.map(_.split(StringUtils.COLON, 2))
      .map(arr => arr(0) -> arr(1))
      .groupBy(_._2)
      .view
      .mapValues(v => v.map(_._1.toUpperCase).distinct.sorted)
      .toMap
  }
}
