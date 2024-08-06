package com.srivastavavivekggn.scala.util

import com.srivastavavivekggn.scala.util.lang.StringUtils
import fastparse.NoWhitespace._
import fastparse.{P, StringIn, CharsWhileIn, CharIn, LiteralStr, EagerOpsStr}

/**
 * Base functionality for parsing strings using FastParse library
 */
package object parse {

  /**
   * One or more spaces - no capture
   */
  final def space[_: P]: P[Unit] = P(CharsWhileIn(" \n\t\r", 0))

  /**
   * Comma - no capture
   */
  final def comma[_: P]: P[Unit] = P(StringUtils.COMMA)

  /**
   * Period - no capture
   */
  final def period[_: P]: P[Unit] = P(StringIn("."))

  /**
   * Single / double quote matcher
   */
  final def quote[_: P]: P[Unit] = P(StringIn("\"", "'"))

  /**
   * Any digit (0-9)
   */
  final def digit[_: P]: P[Unit] = P(CharIn("0-9"))

  /**
   * Positive number
   */
  final def positiveNumber[_: P]: P[Int] = P(digit.rep(1).!.map(_.toInt))

  /**
   * Negative number
   */
  final def negativeNumber[_: P]: P[Int] = P("-" ~ digit.rep(1).!.map(_.toInt * -1))

  /**
   * Any number, captured and mapped to an Int
   */
  final def number[_: P]: P[Int] = P(negativeNumber | positiveNumber)

  /**
   * An array of numbers
   */
  final def numberArray[_: P]: P[Seq[Int]] = P("[" ~ space.? ~ (number ~ comma.? ~ space.?).rep ~ space.? ~ "]")

  /**
   * A string without surrounding quotes - captures until it encounters a character that is not
   * in the approved list
   * a-z A-Z 0-9 ?@#/:&*-_.=+
   */
  final def string[_: P]: P[String] = P(CharIn("a-z", "A-Z", "0-9", "\\?@#\\/:&\\*\\-_\\.\\=\\+").rep(1).!)

  /**
   * A string inside double quotes, captured without the quotes
   */
  final def quotedString[_: P]: P[String] = P(quote ~ string ~ quote)

  /**
   * Field name parser
   */
  final def field[_: P]: P[String] = P(CharIn("A-Z", "0-9", "\\-\\_\\.").rep(1).!)

  /**
   * An array of quoted strings, captured without the quotes
   */
  final def stringArray[_: P]: P[Seq[String]] = P("[" ~ space.? ~ ((quotedString | string) ~ comma.? ~ space.?).rep(1) ~ space.? ~ "]")
}
