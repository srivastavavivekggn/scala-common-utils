package com.srivastavavivekggn.scala.util.lang

import java.util.regex.Pattern

import org.apache.commons.text.StringEscapeUtils

object StringUtils {

  /**
    * reusable null value
    */
  final val nullValue = None.orNull

  final val ENGLISH = java.util.Locale.ENGLISH

  final val EMPTY = ""

  final val BLANK = " "

  final val COMMA = ","

  final val COLON = ":"

  final val SEMI_COLON = ";"

  final val PIPE = "|"

  object Delimiters {
    final val DOT = "."
    final val AT = "@"
    final val DASH = "-"
    final val UNDERSCORE = "_"
    final val COMMA = StringUtils.COMMA
    final val STAR = "*"
    final val VERTICAL_BAR = PIPE
  }

  object EscapedDelimiters {
    final val DOT = s"\\${Delimiters.DOT}"
    final val STAR = s"\\${Delimiters.STAR}"
    final val VERTICAL_BAR = s"\\${Delimiters.VERTICAL_BAR}"
  }

  final val DELIMITERS_TO_ESCAPE = Seq(Delimiters.VERTICAL_BAR, Delimiters.STAR)

  @inline def isEmpty(str: String): Boolean = isEmpty(Option(str))

  @inline def isEmpty(str: Option[String]): Boolean = str.isEmpty || str.get.length == 0

  @inline def isNotEmpty(str: String): Boolean = !isEmpty(str)

  @inline def isNotEmpty(str: Option[String]): Boolean = !isEmpty(str)

  /**
    * Get the value or an empty string (similar to orNull but get's an empty string instead)
    *
    * @param str the string option
    * @return the string value or an empty string
    */
  def orEmpty(str: Option[String]): String = nonEmpty(str).getOrElse(EMPTY)

  /**
    * Determine if the given string is empty (optionally trimming)
    *
    * @param str  the string to check
    * @param trim true to trim
    * @return None if the string is empty, or an Option(str) if non-empty
    */
  def nonEmpty(str: String, trim: Boolean): Option[String] = nonEmpty(Option(str), trim)

  /**
    * Determine if the given Option[String] is empty  (optionally trimming)
    *
    * @param str  the string to check
    * @param trim true to trim
    * @return None if the string is empty, or an Option(str) if non-empty
    */
  def nonEmpty(str: Option[String], trim: Boolean = true): Option[String] = str match {
    case Some(s) if s == nullValue => None
    case Some(s) if trim && isNotEmpty(s.trim) => str
    case Some(s) if !trim && isNotEmpty(s) => str
    case _ => None
  }

  def nonEmptyString(v: Any): String = v match {
    case `nullValue` => EMPTY
    case o: Option[_] if o.isEmpty => EMPTY
    case o: Option[_] => o.map(_.toString).getOrElse(EMPTY)
    case x: Any => x.toString
  }

  /**
   * Trim the given string
   *
   * @param s the string to trim
   * @return the trimmed string
   */
  def trim(s: String): String = trim(Option(s)).getOrElse(s)

  /**
   * Trim the given option[string]
   *
   * @param s the option string
   * @return the option trimmed string
   */
  def trim(s: Option[String]): Option[String] = s.map(_.trim)

  def capitalize(str: String): String = str match {
    case s: String if !isEmpty(s) => s"${s.head.toUpper}${s.tail}"
    case _ => str
  }

  def uncapitalize(str: String): String = str match {
    case s: String if !isEmpty(s) => s"${s.head.toLower}${s.tail}"
    case _ => str
  }

  def split(str: String, delimiter: String = COMMA): Array[String] = {

    val d = if (DELIMITERS_TO_ESCAPE.contains(delimiter)) {
      s"\\$delimiter"
    }
    else {
      delimiter
    }

    str.split(d)
  }


  /**
    * Determine if a passed set of String/Option[String] have equal values
    *
    * @param str the strings to check
    * @return true if all passed values are equal
    */
  def isEqual(str: AnyRef*): Boolean = isEqual(str.toList, ignoreCase = false)

  /**
    * Determine if a passed set of String/Option[String] have equal values to lowercase for comparison
    *
    * @param str the strings to check
    * @return true if all passed values are equal
    */
  def isEqualIgnoreCase(str: AnyRef*): Boolean = isEqual(str.toList, ignoreCase = true)

  def normalizeType(s: AnyRef): String = {
    s match {
      case `nullValue` => nullValue.asInstanceOf[String]

      // string value
      case s: String => s

      // guard against a Some(null)
      case o: Option[_] if o.isDefined && o.get == nullValue => nullValue.asInstanceOf[String]

      // an option that has a value, ensure it's a string
      case o: Option[_] if o.isDefined && o.exists(_.isInstanceOf[String]) => o.map(_.asInstanceOf[String]).orNull

      // empty option, assume null
      case o: Option[_] if o.isEmpty => nullValue.asInstanceOf[String]

      // all other cases, throw error
      case _ => throw new RuntimeException("Cannot call StringUtils.isEqual on a non-string value")
    }
  }

  // scalastyle:off null
  /**
    * Check string equality of a list of values, optionally ignoring case
    *
    * @param vals       the values to check
    * @param ignoreCase true to ignore case, false to do case-sensitive matching
    * @return true if all values are equal, false otherwise
    */
  def isEqual(vals: List[AnyRef], ignoreCase: Boolean): Boolean = {
    val valsToCheck = vals.map(normalizeType)

    // if all values are equal there is only 1 distinct.  if list is empty, there are 0 distincts.
    if (ignoreCase) {
      // map all non-null values to lowercase for comparison
      valsToCheck.map(Option(_).map(_.toLowerCase).orNull).distinct.size <= 1
    }
    else {
      valsToCheck.distinct.size <= 1
    }
  }

  // scalastyle:on null

  /**
    * Convert camel case word(s) to spaces
    *
    * @param str the string to process
    * @return the converted string
    */
  def camelCaseToSpace(str: String): String = {
    capitalize(
      camelCaseTo(str, " ")
    )
  }

  /**
    * Convert camelCase to underscores
    *
    * @param str the string to convert
    * @return the converted string
    */
  def camelCaseToUnderscore(str: String): String = camelCaseTo(str, Delimiters.UNDERSCORE)

  /**
    * Convert a camel case string to a string separated by "to"
    *
    * @param str the camel case string
    * @param to  the separator
    * @return the separated string
    */
  def camelCaseTo(str: String, to: String): String = {
    str.replaceAll(
      String.format("%s|%s|%s",
        "(?<=[A-Z])(?=[A-Z][a-z])",
        "(?<=[^A-Z])(?=[A-Z])",
        "(?<=[A-Za-z])(?=[^A-Za-z])"
      ),
      to
    )
  }

  def toAlphaNumericOnly(str: String): String = {
    str
      .replaceAll("[^A-Za-z0-9_\\s]", EMPTY)
      .replaceAll("\\s+", Delimiters.UNDERSCORE)
  }

  /**
    * Determine if the given string is all uppercase
    *
    * @param str the string to check
    * @return true if all uppercase, false otherwise
    */
  def isAllUpperCase(str: String): Boolean = {
    Option(str).exists(_.toUpperCase.equals(str))
  }

  /**
    * Convert a character to it's unicode representation
    *
    * @param c the character
    * @return the unicode value
    */
  def toUnicode(c: Char): String = {
    val hex = Integer.toHexString(c).toUpperCase.reverse.padTo(4, '0').reverse
    s"\\u$hex"
  }

  /**
    * Unescapes a string containing entity escapes to a string
    * containing the actual Unicode characters corresponding to the
    *  escapes. Supports HTML 4.0 entities.
    *
    * @param str the string to unescape
    * @return @return a new unescaped { @code String}, { @code null} if null string input
    */
  def unescapeHtml(str: String): String = {
    StringEscapeUtils.unescapeHtml4(str)
  }

  /**
    * Convert a given string to a list of strings using
    * Comma as a default delimiter
    *
    * @param str       the string to convert
    * @param delimiter the delimiter
    * @return list of string values
    */
  def delimitedToList(str: String, delimiter: String = StringUtils.COMMA): List[String] = {
    StringUtils.nonEmpty(str, trim = true)
      .map(_.split(delimiter).map(_.trim))
      .map(_.toList)
      .getOrElse(List.empty)
  }
}
