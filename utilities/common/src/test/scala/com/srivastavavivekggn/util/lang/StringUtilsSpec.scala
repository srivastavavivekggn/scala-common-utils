package com.srivastavavivekggn.scala.util.lang

import com.srivastavavivekggn.scala.util.BaseUtilSpec
import com.srivastavavivekggn.scala.util.j.lang.JavaStringUtils

import java.util
import java.util.Collections

class StringUtilsSpec extends BaseUtilSpec {

  final val alpha = "alpha"
  final val ualpha = "ALPHA"
  final val hi = "hi"
  final val spaces = "    "

  behavior of "StringUtils.isEmpty"

  it must "return true for a null string" in {
    assert(StringUtils.isEmpty(None.orNull.asInstanceOf[String]))
    assert(JavaStringUtils.isEmpty(None.orNull.asInstanceOf[String]))
  }

  it must "return true for None" in {
    assert(StringUtils.isEmpty(None))
  }

  it must "return true for an empty string" in {
    assert(StringUtils.isEmpty(""))
    assert(JavaStringUtils.isEmpty(""))
  }

  it must "return true for an empty Option[String]" in {
    assert(StringUtils.isEmpty(Option("")))
  }

  it must "return false for a string with content" in {
    assertResult(false)(StringUtils.isEmpty(hi))
    assertResult(false)(JavaStringUtils.isEmpty(hi))
  }

  it must "return false for an Option[String] with content" in {
    assertResult(false)(StringUtils.isEmpty(Option(hi)))
  }


  behavior of "StringUtils.isNotEmpty"

  it must "return true for a non-empty string" in {
    assert(StringUtils.isNotEmpty(hi))
    assert(JavaStringUtils.isNotEmpty(hi))
  }

  it must "return true for a whitespace only string" in {
    assert(StringUtils.isNotEmpty(" "))
    assert(JavaStringUtils.isNotEmpty(" "))
  }

  it must "return false for an empty string" in {
    assertResult(false)(StringUtils.isNotEmpty(""))
    assertResult(false)(JavaStringUtils.isNotEmpty(""))
  }

  it must "return true for a non-empty Option[String]" in {
    assert(StringUtils.isNotEmpty(Option(hi)))
  }

  it must "return true for a whitespace only Option[String]" in {
    assert(StringUtils.isNotEmpty(Option(" ")))
  }

  it must "return false for an empty Option[String]" in {
    assertResult(false)(StringUtils.isNotEmpty(Option("")))
  }


  behavior of "StringUtils.nonEmpty"

  it must "return None when null is passed in" in {
    assert(StringUtils.nonEmpty(None.orNull.asInstanceOf[String], trim = true).isEmpty)
  }

  it must "return None when an empty string is passed in" in {
    assert(StringUtils.nonEmpty("", trim = true).isEmpty)
    assert(!JavaStringUtils.nonEmpty("", java.lang.Boolean.TRUE).isPresent)
  }

  it must "return None when a whitespace only string is passed in" in {
    assert(StringUtils.nonEmpty(spaces, trim = true).isEmpty)
  }

  it must "return the passed value when a whitespace only string is passed in and not trimmed" in {
    assert(StringUtils.nonEmpty(spaces, trim = false).isDefined)
  }


  it must "return None when a None is passed in" in {
    assert(StringUtils.nonEmpty(None, trim = true).isEmpty)
  }

  it must "return None when an empty Option[String] is passed in" in {
    assert(StringUtils.nonEmpty(Option(""), trim = true).isEmpty)
  }

  it must "return None when a whitespace only Option[String] is passed in" in {
    assert(StringUtils.nonEmpty(Option(spaces), trim = true).isEmpty)
  }


  it must "return the passed value when a non-trimmed whitespace only Option[String] is passed in" in {
    assert(StringUtils.nonEmpty(Option(spaces), trim = false).isDefined)
  }


  behavior of "StringUtils.camelCaseToSpace"

  it must "return the spaced string" in {

    assert(StringUtils.camelCaseToSpace("myCamelCase").equals("My Camel Case"))
    assert(StringUtils.camelCaseToSpace("MyCamelCase2").equals("My Camel Case 2"))
    assert(StringUtils.camelCaseToSpace(classOf[BigDecimal].getSimpleName).equals("Big Decimal"))
  }


  behavior of "StringUtils.isEqual"

  it must "return true when equal strings are passed" in {
    assert(StringUtils.isEqual(alpha, alpha, alpha))
  }

  it must "return true when equal strings and options are passed" in {
    assert(StringUtils.isEqual(alpha, Some(alpha), Option(alpha)))
  }

  it must "return false when non-equal strings are passed" in {
    assert(!StringUtils.isEqual(alpha, "beta"))
  }

  it must "return false when non-equal strings/options are passed" in {
    assert(!StringUtils.isEqual(Option(alpha), "beta"))
  }

  it must "properly handle a null string" in {
    assert(!StringUtils.isEqual(hi, None.orNull.asInstanceOf[String]))
  }

  it must "throw a Runtime exception when a non-string or non-string-option are passed" in {
    assertThrows[RuntimeException](StringUtils.isEqual(hi, Seq()))
  }

  it must "throw a Runtime exception when non-string option is passed" in {
    assertThrows[RuntimeException](StringUtils.isEqual(hi, Some(1)))
  }

  behavior of "StringUtils.isEqualIgnoreCase"

  it must "return true when equal strings are passed" in {
    assert(StringUtils.isEqualIgnoreCase(alpha, alpha, alpha))
  }

  it must "return true when equal mixed case strings are passed" in {
    assert(StringUtils.isEqualIgnoreCase(alpha, ualpha, alpha))
  }

  it must "return true when equal strings and options are passed" in {
    assert(StringUtils.isEqualIgnoreCase(alpha, Some(ualpha), Option(alpha)))
  }

  it must "return false when non-equal strings are passed" in {
    assert(!StringUtils.isEqualIgnoreCase(alpha, "beta"))
  }

  it must "return false when non-equal strings/options are passed" in {
    assert(!StringUtils.isEqualIgnoreCase(Option(alpha), "beta"))
  }

  it must "properly handle a null string" in {
    assert(!StringUtils.isEqualIgnoreCase(hi, None.orNull.asInstanceOf[String]))
  }

  it must "throw a Runtime exception when a non-string or non-string-option are passed" in {
    assertThrows[RuntimeException](StringUtils.isEqualIgnoreCase(hi, Seq()))
  }

  it must "throw a Runtime exception when non-string option is passed" in {
    assertThrows[RuntimeException](StringUtils.isEqualIgnoreCase(hi, Some(1)))
  }

  behavior of "StringUtils.trim"

  it must "trim the string properly" in {

    assert(StringUtils.isEqual(StringUtils.trim("x "), "x"))
    assert(StringUtils.isEqual(StringUtils.trim(" x"), "x"))
    assert(StringUtils.isEqual(StringUtils.trim(null.asInstanceOf[String]), null))

    assert(StringUtils.isEqual(StringUtils.trim(Some("")), ""))
    assert(StringUtils.isEqual(StringUtils.trim(Some(" ab")), "ab"))
    assert(StringUtils.isEqual(StringUtils.trim(Some("      ab ")), "ab"))
  }

  behavior of "StringUtils.delimitedToList"

  it must "return the list properly" in {

    assert(StringUtils.delimitedToList(null).equals(List()))
    assert(StringUtils.delimitedToList("").equals(List()))
    assert(StringUtils.delimitedToList("a").equals(List("a")))
    assert(StringUtils.delimitedToList("aaaa,bbbb").equals(List("aaaa", "bbbb")))
    assert(StringUtils.delimitedToList("aa,").equals(List("aa")))
    assert(StringUtils.delimitedToList("Key1-Key2", StringUtils.Delimiters.DASH).equals(List("Key1", "Key2")))

    assert(JavaStringUtils.delimitedToList(null).equals(Collections.emptyList()))
    assert(JavaStringUtils.delimitedToList("").equals(Collections.emptyList()))
    assert(JavaStringUtils.delimitedToList("a").equals(Collections.singletonList("a")))
    assert(JavaStringUtils.delimitedToList("aaaa,bbbb").equals(util.Arrays.asList("aaaa", "bbbb")))
    assert(JavaStringUtils.delimitedToList("aa,").equals(Collections.singletonList("aa")))
    assert(JavaStringUtils.delimitedToList("Key1-Key2", StringUtils.Delimiters.DASH).equals(util.Arrays.asList("Key1", "Key2")))
  }
}
