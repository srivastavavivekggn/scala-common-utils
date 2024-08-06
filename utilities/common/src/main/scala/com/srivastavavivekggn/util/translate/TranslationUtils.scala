package com.srivastavavivekggn.scala.util.translate

import java.util.regex.Pattern

import com.srivastavavivekggn.scala.util.crypto.HashUtils
import com.srivastavavivekggn.scala.util.lang.StringUtils

/**
  * Utilities for sanitizing and hashing values for purpose of Translating
  */
object TranslationUtils {

  // pattern for replacing newline characters
  final val NEWLINE_REGEX = Pattern.compile("\\n")

  // pattern for replacing multiple spaces
  final val MULTI_SPACE_REGEX = Pattern.compile("\\s{2,}")

  /**
    * Collection of functions to sanitize a string used for hashing / translation
    */
  private val sanitizers: Seq[String => String] = Seq(

    // replace newlines with empty string
    (v: String) => NEWLINE_REGEX.matcher(v).replaceAll(StringUtils.EMPTY),

    // replace multiple spaces with single spaces
    (v: String) => MULTI_SPACE_REGEX.matcher(v).replaceAll(StringUtils.BLANK),

    // trim leading and trailing spaces
    (v: String) => v.trim
  )

  /**
    * Sanitize the input being translated to trim, remove newlines, etc.
    *
    * @param toSanitize the value
    * @return the updated value
    */
  def sanitize(toSanitize: String): String = Option(toSanitize)
    .map(v => sanitizers.foldLeft(v)((value, op) => op(value)))
    .getOrElse(toSanitize)

  /**
    * Hash the given string.  This creates a SHA256 hash as hex values, and also lowercases the result.
    *
    * @param toHash the string to hash
    * @return the hashed value
    */
  def hash(toHash: String): String = Option(toHash)
    .map(HashUtils.hash(HashUtils.SHA256, _, useHex = true).toLowerCase)
    .getOrElse(toHash)

  /**
    * Sanitize and hash the given string
    *
    * @param toSanitize the string to sanitize and hash
    * @return the sanitized and hashed value
    */
  def sanitizeAndHash(toSanitize: String): String = hash(sanitize(toSanitize))
}
