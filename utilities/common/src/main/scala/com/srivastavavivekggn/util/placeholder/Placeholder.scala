package com.srivastavavivekggn.scala.util.placeholder

import java.util.regex.Pattern

import com.srivastavavivekggn.scala.util.lang.StringUtils
import com.srivastavavivekggn.scala.util.lang.StringUtils.EscapedDelimiters.{VERTICAL_BAR => EscapedPipe}
import com.srivastavavivekggn.scala.util.placeholder.PlaceholderUtils.placeholderRegexFormat

/**
  * Wrapper for a placeholder.  Placeholders can look like:
  * {{name}} or {{name|formatType}} or {{name|formatType:arg1:arg2:arg3} or {{name|formatType:arg1:arg2|formatType:arg}}
  *
  * @param key    the key
  * @param format the optional format
  */
case class Placeholder(key: String, format: List[PlaceholderFormat] = List.empty) {

  // determine the key for literal replacement
  private val replacementKeyRegex: Pattern = {
    val k = format match {
      // no format
      case Nil => Pattern.quote(key)

      // one or more formats
      case _ =>
        val mapped = format.map {
          case f: PlaceholderFormat if f.formatArgs.nonEmpty =>
            f.aliasOf.getOrElse(
              (List(f.formatType) ++ f.formatArgs).mkString("\\s*:\\s*")
            )

          case f: PlaceholderFormat => f.formatType
        }

        // separate key and formats using pipe
        (List(Pattern.quote(key)) ++ mapped).mkString(s"\\s*$EscapedPipe\\s*")
    }

    Pattern.compile(placeholderRegexFormat.format(k))
  }

  /**
    * Replace this placeholder in the input string with the provided replacement value
    *
    * @param in             the input string
    * @param formattedValue the already formatted value
    * @return the updated string
    */
  def replace(in: String, formattedValue: String): String = {
    replacementKeyRegex.matcher(in).replaceAll(formattedValue)
  }
}

object Placeholder {

  def fromString(str: String): Placeholder = str match {

    // includes a format (optionally with arguments
    case s: String if s.contains(StringUtils.PIPE) =>

      val parts = s.split(EscapedPipe).toList.map(_.trim)

      val formats = parts.tail.map {
        case f: String if f.contains(StringUtils.COLON) =>
          val formatParts = f.split(StringUtils.COLON).toList
          PlaceholderFormat.create(formatParts.head, formatParts.tail)

        case f: String => PlaceholderFormat.create(f)
      }

      Placeholder(parts.head, formats)

    // no format
    case s: String => Placeholder(s)
  }
}

case class PlaceholderFormat(formatType: String,
                             formatArgs: List[String] = List.empty,
                             aliasOf: Option[String] = None) {
  override def toString: String = (List(formatType) ++ formatArgs).mkString(StringUtils.COLON)
}

object PlaceholderFormat {

  final val aliases: Map[String, PlaceholderFormat] = Map(
    "monthName" -> PlaceholderFormat("localizedDate", List("MMMM"), Some("monthName"))
  )

  def create(formatType: String, formatArgs: List[String] = List.empty): PlaceholderFormat = {
    aliases.getOrElse(formatType, PlaceholderFormat(formatType, formatArgs))
  }

}
