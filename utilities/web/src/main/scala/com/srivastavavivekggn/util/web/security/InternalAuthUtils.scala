package com.srivastavavivekggn.scala.util.web.security

import com.srivastavavivekggn.scala.util.TypeAlias.JLong
import com.srivastavavivekggn.scala.util.crypto.HashUtils
import com.srivastavavivekggn.scala.util.lang.StringUtils
import com.srivastavavivekggn.scala.util.random.RandomUtils
import com.typesafe.scalalogging
import org.slf4j.Logger
import org.springframework.http.{HttpHeaders, HttpRequest}

import javax.servlet.http.HttpServletRequest
import scala.util.{Failure, Try}

/**
  * Utility to generate and validate internal service auth headers
  */
object InternalAuthUtils {

  /**
    * The authorization header scheme (replaces Basic, Bearer, etc)
    */
  val internalAuth = "Internal"

  /**
    * Length of randomly generated string
    */
  private val randomLength = 12

  /**
    * Convenience for sripping scheme from auth header
    */
  private val internalAuthLength = internalAuth.length

  /**
    * default log impl
    */
  private val defaultLogger = new DummyLogger

  /**
    * Generate SHA256 hash to use as the auth header
    *
    * @param authSecret the auth secret
    * @param method     the http method being used
    * @param url        the full URL being called
    * @param ts         the timestamp
    * @param rand       the random value
    * @param prefix     the header key prefix
    * @return the auth hash
    */
  private[security] def getAuthHash(authSecret: String,
                                    method: String,
                                    url: String,
                                    ts: String,
                                    rand: String,
                                    prefix: String): String = {
    HashUtils.sha256(s"$authSecret|${method.toUpperCase}|$url|$ts|$rand|$prefix", useHex = true)
  }

  /**
    * Generates a set of HttpHeaders for internal service auth.
    *
    * This method ALWAYS uses the last secret in the array
    *
    * @param authSecret       the auth secret list
    * @param serviceIndicator the service name/id
    * @param method           the http method
    * @param url              the url
    * @return the set of headers for auth
    */
  def generateHeaders(authSecret: Array[String],
                      serviceIndicator: String,
                      method: String,
                      url: String): HttpHeaders = {
    assert(Option(authSecret).isDefined && authSecret.length > 0, "One or more Auth Secrets must be supplied")
    generateHeaders(authSecret(authSecret.length - 1), serviceIndicator, method, url)
  }

  /**
    * Generates a set of HttpHeaders for internal service auth
    *
    * @param authSecret       the auth secret
    * @param serviceIndicator the service name/id
    * @param method           the http method
    * @param url              the url
    * @return the set of headers for auth
    */
  def generateHeaders(authSecret: String,
                      serviceIndicator: String,
                      method: String,
                      url: String): HttpHeaders = {

    assert(StringUtils.isNotEmpty(authSecret), "Auth Secret must be supplied")
    assert(StringUtils.isNotEmpty(serviceIndicator), "Service Indicator must be supplied")

    val headers = new HttpHeaders

    val ts = "" + System.currentTimeMillis
    val rand = RandomUtils.getRandomString(randomLength)

    val auth = getAuthHash(authSecret, method, url, ts, rand, serviceIndicator)

    headers.add(HttpHeaders.AUTHORIZATION, s"$internalAuth $auth")
    headers.add(s"x-$serviceIndicator-ts", ts)
    headers.add(s"x-$serviceIndicator-rand", rand)

    headers
  }

  /**
    * Generate auth headers for the given request
    *
    * @param authSecret       the auth secret
    * @param serviceIndicator the service indicator
    * @param request          the http request
    * @return the HttpHeaders
    */
  def generateHeaders(authSecret: String,
                      serviceIndicator: String,
                      request: HttpServletRequest): HttpHeaders = {
    generateHeaders(authSecret, serviceIndicator, request.getMethod, request.getRequestURL.toString)
  }

  def generateHeaders(authSecret: String,
                      serviceIndicator: String,
                      request: HttpRequest): HttpHeaders = {
    generateHeaders(authSecret, serviceIndicator, request.getMethod.name(), request.getURI.toString)
  }

  /**
    * Authorize the given request
    *
    * @param authSecret       the auth secret
    * @param serviceIndicator the service indicator
    * @param request          the http request
    * @throws AccessDeniedException if the auth is invalid
    */
  @throws[AccessDeniedException]
  def authorize(authSecret: String,
                serviceIndicator: String,
                request: HttpServletRequest): Unit = authorize(authSecret, serviceIndicator, request, defaultLogger)

  @throws[AccessDeniedException]
  def authorize(authSecret: String,
                serviceIndicator: String,
                request: HttpRequest): Unit = authorize(authSecret, serviceIndicator, request, defaultLogger)

  /**
    * Authorize the given request
    *
    * @param authSecret       the auth secret
    * @param serviceIndicator the service indicator
    * @param request          the http request
    * @param logger           the logger
    * @throws AccessDeniedException if the auth is invalid
    */
  @throws[AccessDeniedException]
  def authorize(authSecret: String,
                serviceIndicator: String,
                request: HttpServletRequest,
                logger: AuthLogger): Unit = {
    authorize(authSecret, serviceIndicator, request, request.getMethod, request.getRequestURL.toString, logger)
  }

  @throws[AccessDeniedException]
  def authorize(authSecret: String,
                serviceIndicator: String,
                request: HttpRequest,
                logger: AuthLogger): Unit = {
    authorize(authSecret, serviceIndicator, request, request.getMethod.name(), request.getURI.toString, logger)
  }


  /**
    * Authorize the given request plus url + method
    *
    * @param authSecret       the auth secret
    * @param serviceIndicator the service indicator
    * @param request          the http request
    * @param method           the http method
    * @param url              the url to authorize
    */
  def authorize(authSecret: String,
                serviceIndicator: String,
                request: HttpServletRequest,
                method: String,
                url: String): Unit = {
    authorize(authSecret, serviceIndicator, request, method, url, defaultLogger)
  }

  def authorize(authSecret: String,
                serviceIndicator: String,
                request: HttpRequest,
                method: String,
                url: String): Unit = {
    authorize(authSecret, serviceIndicator, request, method, url, defaultLogger)
  }

  @throws[AccessDeniedException]
  def authorize(authSecret: String,
                serviceIndicator: String,
                request: HttpServletRequest,
                method: String,
                url: String,
                logger: AuthLogger): Unit = {
    authorize(authSecret, serviceIndicator, (h) => request.getHeader(h), method, url, logger)
  }

  @throws[AccessDeniedException]
  def authorize(authSecret: String,
                serviceIndicator: String,
                request: HttpRequest,
                method: String,
                url: String,
                logger: AuthLogger): Unit = {
    authorize(authSecret, serviceIndicator, (h) => request.getHeaders.getFirst(h), method, url, logger)
  }

  /**
    * Authorize the given request plus url + method
    *
    * @param authSecret       the auth secret
    * @param serviceIndicator the service indicator
    * @param request          the http request
    * @param method           the http method
    * @param url              the url to authorize
    * @param logger           the logger
    * @throws AccessDeniedException if the auth is invalid
    */
  @throws[AccessDeniedException]
  def authorize(authSecret: String,
                serviceIndicator: String,
                getHeader: (String) => String,
                method: String,
                url: String,
                logger: AuthLogger): Unit = {

    val authHeader = StringUtils.nonEmpty(getHeader(HttpHeaders.AUTHORIZATION), trim = true)
      .map(_.drop(internalAuthLength + 1))

    val timestampHeader = StringUtils.nonEmpty(getHeader(s"x-$serviceIndicator-ts"), trim = true)
    val timestampValue = timestampHeader.map[JLong](_.toLong)
    val random = StringUtils.nonEmpty(getHeader(s"x-$serviceIndicator-rand"), trim = true)

    if (timestampHeader.isEmpty || random.isEmpty) {
      throw new AccessDeniedException("Missing required authorization data")
    }

    if (timestampValue.forall(_ < System.currentTimeMillis - (1000 * 60 * 5))) {
      throw new AccessDeniedException("Request too old")
    }

    if (timestampValue.forall(_ > System.currentTimeMillis + (1000 * 60 * 5))) {
      throw new AccessDeniedException("Request skew too large")
    }

    val expected = getAuthHash(authSecret, method, url, timestampHeader.orNull, random.orNull, serviceIndicator)

    logger.debug(
      String.format(
        "Internal auth request: ts = %s, rand = %s, method = %s, url = %s, service = %s, original header = %s",
        timestampHeader.orNull, random.orNull, method, url, serviceIndicator,
        getHeader(HttpHeaders.AUTHORIZATION)
      )
    )

    if (!StringUtils.isEqual(expected, authHeader)) {
      logger.debug(
        String.format("Internal Auth Failure: expected %s but got %s", expected, authHeader)
      )

      throw new AccessDeniedException("Not Authorized")
    }
  }


  /**
    * Authorization method that allows for multiple secrets and determines if the incoming
    * request can be authorized using any
    *
    * @param authSecrets      the array of auth secrets
    * @param serviceIndicator the service indicator
    * @param request          the http request
    */
  def authorize(authSecrets: Array[String],
                serviceIndicator: String,
                request: HttpServletRequest): Unit = authorize(authSecrets, serviceIndicator, request, defaultLogger)

  def authorize(authSecrets: Array[String],
                serviceIndicator: String,
                request: HttpRequest): Unit = authorize(authSecrets, serviceIndicator, request, defaultLogger)

  /**
    * Authorization method that allows for multiple secrets and determines if the incoming
    * request can be authorized using any
    *
    * @param authSecrets      the array of auth secrets
    * @param serviceIndicator the service indicator
    * @param request          the http request
    * @param logger           the logger
    */
  def authorize(authSecrets: Array[String],
                serviceIndicator: String,
                request: HttpServletRequest,
                logger: AuthLogger): Unit = {
    authorize(authSecrets, serviceIndicator, request, request.getMethod, request.getRequestURL.toString, logger)
  }

  def authorize(authSecrets: Array[String],
                serviceIndicator: String,
                request: HttpRequest,
                logger: AuthLogger): Unit = {
    authorize(authSecrets, serviceIndicator, request, request.getMethod.name(), request.getURI.toString, logger)
  }

  /**
    * Authorization method that allows for multiple secrets and determines if the incoming
    * request can be authorized using any
    *
    * @param authSecrets      the array of auth secrets
    * @param serviceIndicator the service indicator
    * @param request          the http request
    * @param method           the http method
    * @param url              the url
    */
  def authorize(authSecrets: Array[String],
                serviceIndicator: String,
                request: HttpServletRequest,
                method: String,
                url: String): Unit = authorize(authSecrets, serviceIndicator, request, method, url, defaultLogger)

  def authorize(authSecrets: Array[String],
                serviceIndicator: String,
                request: HttpRequest,
                method: String,
                url: String): Unit = authorize(authSecrets, serviceIndicator, request, method, url, defaultLogger)

  /**
    * Authorization method that allows for multiple secrets and determines if the incoming
    * request can be authorized using any
    *
    * @param authSecrets      the array of auth secrets
    * @param serviceIndicator the service indicator
    * @param request          the http request
    * @param method           the http method
    * @param url              the url
    * @param logger           the logger
    */
  def authorize(authSecrets: Array[String],
                serviceIndicator: String,
                request: HttpServletRequest,
                method: String,
                url: String,
                logger: AuthLogger): Unit = {

    logger.debug("Authorizing internal for service " + serviceIndicator)

    // authorize using each secret
    val authAttempts = authSecrets.map(as => Try(authorize(as, serviceIndicator, request, method, url, logger)))

    // all attempts failed
    if (authAttempts.forall(_.isFailure)) {

      // get the exception from the first failure (or create new failure if none exist)a
      throw authAttempts.headOption.flatMap {
        case Failure(ex) => Some(ex)
        case _ => None
      }.getOrElse(new AccessDeniedException("Not Authorized"))
    }
  }

  def authorize(authSecrets: Array[String],
                serviceIndicator: String,
                request: HttpRequest,
                method: String,
                url: String,
                logger: AuthLogger): Unit = {

    logger.debug("Authorizing internal for service " + serviceIndicator)

    // authorize using each secret
    val authAttempts = authSecrets.map(as => Try(authorize(as, serviceIndicator, request, method, url, logger)))

    // all attempts failed
    if (authAttempts.forall(_.isFailure)) {

      // get the exception from the first failure (or create new failure if none exist)a
      throw authAttempts.headOption.flatMap {
        case Failure(ex) => Some(ex)
        case _ => None
      }.getOrElse(new AccessDeniedException("Not Authorized"))
    }
  }

  trait AuthLogger {
    def debug(msg: String): Unit
  }

  // no-op logger
  class DummyLogger extends AuthLogger {
    override def debug(msg: String): Unit = ()
  }

  // slf4j wrapper
  case class Slf4JAuthLogger(log: Logger) extends AuthLogger {
    override def debug(msg: String): Unit = log.debug(msg)
  }

  // scala logging wrapper
  case class ScalaLoggingAuthLogger(log: scalalogging.Logger) extends AuthLogger {
    override def debug(msg: String): Unit = log.debug(msg)
  }
}
