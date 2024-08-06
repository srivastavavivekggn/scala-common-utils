package com.srivastavavivekggn.scala.util.placeholder

import java.util.Locale

import com.srivastavavivekggn.scala.util.TypeAlias.{JList, JMap}
import com.srivastavavivekggn.scala.util.collection.CollectionUtils
import com.srivastavavivekggn.scala.util.lang.StringUtils
import com.srivastavavivekggn.scala.util.placeholder.context.{PlaceholderContextProvider, StaticPlaceholderContextProvider}
import com.srivastavavivekggn.scala.util.placeholder.format.{LocalizedTemporalValueFormatter, PlaceholderValueFormatter, TemporalValueFormatter, UrlEncodingValueFormatter}

import scala.annotation.tailrec

/**
  * Core functionality for handling placeholder replacement in strings.
  *
  * A placeholder is anything that falls between the open and close flower braces - {{ ... }}
  */
object PlaceholderUtils {

  /**
    * re-usable null reference
    */
  private final val nullValue = None.orNull

  /**
    * format string for a placeholder (open/close flower braces)
    */
  final val placeholderRegexFormat = "\\{\\{\\s*%s\\s*}}"

  /**
    * regular expression to find placeholders (i.e., things between {{ and }} )
    */
  final val placeholderRegex = "\\{\\{\\s*([^{]+?)\\s*}}".r

  /**
    * the list of default value formatters
    */
  final val defaultFormatters = List(
    new TemporalValueFormatter,
    new LocalizedTemporalValueFormatter,
    new UrlEncodingValueFormatter
  )

  /**
    * Options for how to handle placeholders that are missing replacement values
    */
  sealed trait ReplacementMode

  /**
    * Specific replacement mode options
    */
  object ReplacementMode {

    /**
      * Remove placeholders that have no replacement values.
      * e.g., "Hi {{name}}" would become  "Hi " if there is no 'name' replacement
      */
    final object STRIP_PLACEHOLDERS extends ReplacementMode

    /**
      * Leave placeholders that have no replacement values.
      * e.g., "Hi {{name}}" would become  "Hi {{name}}" if there is no 'name' replacement
      */
    final object LEAVE_PLACEHOLDERS extends ReplacementMode

    /**
      * Leave placeholders but drop braces that have no replacement values.
      * e.g., "Hi {{name}}" would become  "Hi name" if there is no 'name' replacement
      */
    final object DROP_BRACES extends ReplacementMode
  }

  /**
    * Get the set of placeholder names. This does NOT include anything that may be nested
    *
    * @param in the input string
    * @return the set of placeholder keys (does NOT include formats)
    */
  def getPlaceholderNames(in: String): Set[String] = getPlaceholders(in).map(_.key).toSet

  /**
    * Get a distinct list of all placeholders present in the given input string
    *  - i.e., find all distinct replacement keys (things between {{ and }} )
    *
    * @param in the input string
    * @return the placeholder list
    */
  def getPlaceholders(in: String): List[Placeholder] = {
    StringUtils.nonEmpty(in, trim = true)
      .map(s => placeholderRegex.findAllMatchIn(s)
        .flatMap(_.subgroups)
        .toList
        .map(Placeholder.fromString)
        .distinct
      )
      .getOrElse(List.empty)
  }


  /**
    * Replace all placeholders in the given input string
    *
    * @param in              the input string
    * @param replacementCtx  the replacement context provider
    * @param replacementMode how do we handle placeholders with no replacements
    * @param formatters      any additional formatters to use (aside from the defaults)
    * @return the updated string
    */
  @tailrec
  def replaceAll(in: String,
                 replacementCtx: PlaceholderContextProvider,
                 replacementMode: ReplacementMode = ReplacementMode.STRIP_PLACEHOLDERS,
                 formatters: List[PlaceholderValueFormatter] = List.empty,
                 locale: Locale = Locale.getDefault): String = {

    // find all placeholders, and filter down to just those where we have replacements available
    val placeholders = getPlaceholders(in)

    // nothing to replace
    if (placeholders.isEmpty) {
      in
    }
    // we have placeholders, but nothing to replace and mode = leave placeholders
    else if (ReplacementMode.LEAVE_PLACEHOLDERS.equals(replacementMode) &&
      !placeholders.exists(p => replacementCtx.contains(p.key))) {
      in
    }
    // replace all and call recursive to replace any newly introduced placeholders
    else {

      val result = placeholders.foldLeft(in) {
        // we have a specific replacement
        case (str, placeholder) if replacementCtx.contains(placeholder.key) =>
          val formattedValue = getFormattedValue(replacementCtx.get(placeholder.key), placeholder, formatters, locale)
          placeholder.replace(str, formattedValue)

        // no replacement, stripping placeholders
        case (str, placeholder) if ReplacementMode.STRIP_PLACEHOLDERS.equals(replacementMode) =>
          val formattedValue = getFormattedValue(None, placeholder, formatters, locale)
          placeholder.replace(str, formattedValue)

        // no replacement, dropping braces
        case (str, placeholder) if ReplacementMode.DROP_BRACES.equals(replacementMode) =>
          val formattedValue = getFormattedValue(placeholder.key, placeholder, formatters, locale)
          placeholder.replace(str, formattedValue)

        // no replacement, leaving placeholders
        case (str, _) => str
      }

      // make recursive call to handle any new placeholders introduced
      replaceAll(result, replacementCtx, replacementMode, formatters, locale)
    }
  }

  /**
    * Replace all placeholders in the given input string
    *
    * @param in              the input string
    * @param replacementCtx  the replacement context
    * @param replacementMode how do we handle placeholders with no replacements
    * @param formatters      any additional formatters to use (aside from the defaults)
    * @return the updated string
    */
  def replaceAllPlaceholders(in: String,
                             replacementCtx: Map[String, Any],
                             replacementMode: ReplacementMode = ReplacementMode.STRIP_PLACEHOLDERS,
                             formatters: List[PlaceholderValueFormatter] = List.empty,
                             locale: Locale = Locale.getDefault): String = {
    replaceAll(in, StaticPlaceholderContextProvider(replacementCtx), replacementMode, formatters, locale)
  }

  /**
    * Expose as java-friendly API
    *
    * @param in              the input string
    * @param replacementCtx  the java map replacement context
    * @param replacementMode how do we handle placeholders with no replacements
    * @param formatters      any additional formatters to use (aside from the defaults)
    * @return the string with values replaced
    */
  def replaceAllPlaceholders(in: String,
                             replacementCtx: JMap[String, Object],
                             replacementMode: ReplacementMode,
                             formatters: JList[PlaceholderValueFormatter],
                             locale: Locale): String = {
    replaceAllPlaceholders(
      in,
      CollectionUtils.asScalaMapOrEmpty(replacementCtx),
      replacementMode,
      CollectionUtils.asScalaListOrEmpty(formatters),
      locale
    )
  }


  /**
    * Determine the formatted value to return
    *
    * @param value the incoming value
    * @return the formatted value
    */
  def getFormattedValue(value: Any,
                        placeholder: Placeholder,
                        formatters: List[PlaceholderValueFormatter],
                        locale: Locale): String = value match {

    // null, return empty string
    case `nullValue` => StringUtils.EMPTY

    // empty option, return empty string
    case o: Option[_] if o.isEmpty => StringUtils.EMPTY

    // option -> unwrap and recurse
    case o: Option[_] => o.map(v => getFormattedValue(v, placeholder, formatters, locale)).getOrElse(StringUtils.EMPTY)

    // we have a value that includes one or more formatters
    case x: Any if placeholder.format.nonEmpty => placeholder.format.foldLeft(x) {

      // we have a formatter based on type
      case (v: Any, f: PlaceholderFormat) if formatterExistsForType(f.formatType, formatters) =>
        doFormatForType(v, f.formatType, f.formatArgs, formatters, locale)

      // we have a formatter based on value (e.g., a date value with 'YYYY' as a formatType)
      case (v: Any, f: PlaceholderFormat) if formatterExistsForValue(v, formatters) =>
        doFormatForValue(v, List(f.formatType) ++ f.formatArgs, formatters, locale)

      // we have a defined format, but no formatter
      case (v: Any, _: PlaceholderFormat) => v.toString
    }.toString

    // no defined format, see if we can format by value
    case x: Any if formatterExistsForValue(x, formatters) => doFormatForValue(x, List.empty, formatters, locale)

    // anything else
    case x: Any => x.toString
  }

  /**
    * Determine if a formatter exists for the given type
    *
    * @param formatType the format type
    * @param formatters the formatters
    * @return true if a formatter exists for the given format type
    */
  private def formatterExistsForType(formatType: String, formatters: List[PlaceholderValueFormatter]): Boolean = {
    formatters.exists(_.canFormat(formatType)) || defaultFormatters.exists(_.canFormat(formatType))
  }

  /**
    * Format the given value based on the formatType
    *
    * @param value      the value
    * @param formatType the format type
    * @param formatArgs the formatter arguments
    * @param formatters the list of formatters
    * @return the formatted value
    */
  private def doFormatForType(value: Any,
                              formatType: String,
                              formatArgs: List[String],
                              formatters: List[PlaceholderValueFormatter],
                              locale: Locale): String = {
    formatters.find(_.canFormat(formatType))
      .orElse(defaultFormatters.find(_.canFormat(formatType)))
      .map(_.format(value, formatArgs, locale))
      .getOrElse(StringUtils.EMPTY)
  }

  /**
    * Determine if we have a formatter that can handle the provided value
    *
    * @param value      the value
    * @param formatters the list of formatters provided
    * @return true if any formatter can handle the given value
    */
  private def formatterExistsForValue(value: Any, formatters: List[PlaceholderValueFormatter]): Boolean = {
    formatters.exists(_.canFormatValue(value)) || defaultFormatters.exists(_.canFormatValue(value))
  }

  /**
    * Perform formatting based on the given value
    *
    * @param value      the value to format
    * @param formatArgs the format arguments
    * @param formatters the formatters
    * @return the formatted string value
    */
  private def doFormatForValue(value: Any,
                               formatArgs: List[String],
                               formatters: List[PlaceholderValueFormatter],
                               locale: Locale): String = {
    formatters.find(_.canFormatValue(value))
      .orElse(defaultFormatters.find(_.canFormatValue(value)))
      .map(_.format(value, formatArgs, locale))
      .getOrElse(StringUtils.EMPTY)
  }
}
