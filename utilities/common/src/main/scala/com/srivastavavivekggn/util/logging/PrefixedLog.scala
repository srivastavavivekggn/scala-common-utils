package com.srivastavavivekggn.scala.util.logging

import com.typesafe.scalalogging.Logger

/**
  * Simple wrapper around a Logger that enables prefixing messages
  *
  * @param logger       the logger to wrap
  * @param prefixValues the values to use as prefixes
  */
case class PrefixedLog(logger: Logger, prefixValues: Any*) {

  /**
    * Internal string that concatenates the delimiter [{}] as many times as needed
    */
  private val prefix = prefixValues.map(_ => PrefixedLog.LOG_DELIMITER).mkString

  /**
    * Log the message at INFO level
    *
    * @param msg  the message
    * @param args the arguments
    */
  def info(msg: String, args: Any*): Unit = {
    val (m, a) = getFinal(msg, args)
    logger.info(m, a: _*)
  }

  /**
    * Log the message at WARN level
    *
    * @param msg  the message
    * @param args the arguments
    */
  def warn(msg: String, args: Any*): Unit = {
    val (m, a) = getFinal(msg, args)
    logger.warn(m, a: _*)
  }

  /**
    * Log the message at DEBUG level
    *
    * @param msg  the message
    * @param args the arguments
    */
  def debug(msg: String, args: Any*): Unit = {
    val (m, a) = getFinal(msg, args)
    logger.debug(m, a: _*)
  }

  /**
    * Log the message at TRACE level
    *
    * @param msg  the message
    * @param args the arguments
    */
  def trace(msg: String, args: Any*): Unit = {
    val (m, a) = getFinal(msg, args)
    logger.trace(m, a: _*)
  }

  /**
    * Log the message at ERROR level
    *
    * @param msg       the message
    * @param throwable The throwable / cause of the error
    * @param args      the arguments
    */
  def error(msg: String, throwable: Throwable, args: Any*): Unit = {
    val (m, a) = getFinal(msg, args)

    val resolvedMessage = resolveToString(m, a)
    logger.error(resolvedMessage, throwable)
  }

  /**
    * Internal method to get the final message and argument list for printing
    *
    * @param msg  the message
    * @param args the arguments
    * @return the tuple of message plus arg list
    */
  private def getFinal(msg: String, args: Seq[Any]): (String, Seq[String]) = (s"$prefix $msg", (prefixValues ++ args).map(String.valueOf))

  /**
    * Internal method to resolve a message and arguments into a string (i.e., replaces {} placeholders with the arg value(s))
    *
    * @param msg  the message
    * @param args the arguments
    * @return the resolved string
    */
  private def resolveToString(msg: String, args: Seq[String]): String = {
    args.foldLeft(msg)((a, b) => {
      a.replaceFirst("\\{\\}", b)
    })
  }

  /**
    * Return a NEW prefixed log with the original prefix values, and this new prefix value
    *
    * @param prefixValue the prefix value to add
    * @return the new prefixed log
    */
  def withPrefixValue(newValue: Any): PrefixedLog = {
    PrefixedLog(logger, (prefixValues ++ Seq(newValue)): _*)
  }

  /**
    * Return a NEW prefixed log with the original prefix values but a different logger
    * @param newLogger the new logger to use
    * @return the new prefixed log
    */
  def withLogger(newLogger: Logger): PrefixedLog = {
    PrefixedLog(newLogger, prefixValues: _*)
  }
}


object PrefixedLog {
  final val LOG_DELIMITER = "[{}]"
}
