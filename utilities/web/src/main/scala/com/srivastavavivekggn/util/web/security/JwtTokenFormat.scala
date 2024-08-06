package com.srivastavavivekggn.scala.util.web.security

sealed abstract class JwtTokenFormat(val name: String)

/**
  * Defines the possible formats for a JWT Token
  *
  * The format is ultimately used to determine the proper way to create and validate JWT claims
  */
object JwtTokenFormat {

  case object LEGACY extends JwtTokenFormat("legacy")

  case object METHOD_AND_URL extends JwtTokenFormat("methodUrl")

  case object MANUAL extends JwtTokenFormat("manual")

  private val values = Seq(LEGACY, METHOD_AND_URL, MANUAL)
  private val fastLookup = values.map(v => v.name -> v).toMap

  def forName(name: String): Option[JwtTokenFormat] = fastLookup.get(name)
}
