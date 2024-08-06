package com.srivastavavivekggn.scala.util.web.security

case class JwtClaimMatcher(keyMatcher: String => Boolean, valueMatcher: AnyRef => Boolean) {
  def tuple: (String => Boolean, AnyRef => Boolean) = (keyMatcher, valueMatcher)
}

object JwtClaimMatcher {

  implicit def claimMatcherAsTuple(matcher: JwtClaimMatcher): (String => Boolean, AnyRef => Boolean) = {
    matcher.tuple
  }

}
