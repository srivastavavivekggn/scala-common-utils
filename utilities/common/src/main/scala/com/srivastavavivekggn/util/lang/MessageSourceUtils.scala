package com.srivastavavivekggn.scala.util.lang

import java.util.Locale

import org.springframework.context.{MessageSource, NoSuchMessageException}

/**
  * Utilities for retrieving values from a MessageSource
  */
object MessageSourceUtils {

  /**
    * Get message or return default value
    *
    * @param messageSource the message source
    * @param locale        the locale
    * @param key           the key
    * @param args          the arguments
    * @param defaultValue  the default value
    * @return the message (or default if not found)
    */
  def getMessage(messageSource: MessageSource,
                 locale: Locale,
                 key: String,
                 args: Array[Object],
                 defaultValue: String): String = {
    try {
      messageSource.getMessage(key, args, locale)
    } catch {
      case _: NoSuchMessageException => defaultValue
    }
  }

  /**
    * Get message or return default value (no args)
    *
    * @param messageSource the message source
    * @param locale        the locale
    * @param key           the key to lookup
    * @param defaultValue  the default value if no translation found
    * @return the translated value if found, or the default
    */
  def getMessage(messageSource: MessageSource,
                 locale: Locale,
                 key: String,
                 defaultValue: String): String = getMessage(messageSource, locale, key, Array.empty[Object], defaultValue)


  /**
    * Get an optional message, falling back to the given default value
    *
    * @param messageSource the message source
    * @param locale        the locale
    * @param key           the key
    * @param args          the arguments
    * @param defaultValue  the default value
    * @return the optional message (or default value)
    */
  def getMessage(messageSource: MessageSource,
                 locale: Locale,
                 key: String,
                 args: Array[Object],
                 defaultValue: Option[String]): Option[String] = {
    getOptionalMessage(messageSource, locale, key, args).orElse(defaultValue)
  }

  /**
    * Get the translated value for the given key as an Option[String]
    *
    * @param messageSource the message source
    * @param locale        the locale
    * @param key           the key
    * @param defaultValue  the default value if no translation found
    * @return the translated value if found, or the default
    */
  def getMessage(messageSource: MessageSource,
                 locale: Locale,
                 key: String,
                 defaultValue: Option[String]
                ): Option[String] = getOptionalMessage(messageSource, locale, key).orElse(defaultValue)


  /**
    * Get an optional message (no default)
    *
    * @param messageSource the message source
    * @param locale        the locale
    * @param key           the key
    * @param args          the args
    * @return the optional message
    */
  def getOptionalMessage(messageSource: MessageSource,
                         locale: Locale,
                         key: String,
                         args: Array[Object]): Option[String] = {

    Option(
      // get message, and return null if not found
      getMessage(messageSource, locale, key, args, None.orNull[String])
    )
  }

  /**
    * Get an optional message (no default)
    *
    * @param messageSource the message source
    * @param locale        the locale
    * @param key           the key
    * @return the Option message if one was found
    */
  def getOptionalMessage(messageSource: MessageSource,
                         locale: Locale,
                         key: String): Option[String] = getOptionalMessage(messageSource, locale, key, Array.empty[Object])


  def getOptionalMessage(messageSource: MessageSource,
                         locale: Locale,
                         keyOpt: Option[String],
                         args: Array[Object]): Option[String] = {
    keyOpt.flatMap(getOptionalMessage(messageSource, locale, _, args))
  }

  /**
    * Get a message given a possibly empty key
    *
    * @param messageSource the message source
    * @param locale        the locale
    * @param keyOpt        the key option
    * @return None if no key or if message not found
    */
  def getOptionalMessage(messageSource: MessageSource,
                         locale: Locale,
                         keyOpt: Option[String]): Option[String] = {
    getOptionalMessage(messageSource, locale, keyOpt, Array.empty[Object])
  }

}
