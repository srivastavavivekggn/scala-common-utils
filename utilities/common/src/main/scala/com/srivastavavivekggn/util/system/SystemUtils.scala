package com.srivastavavivekggn.scala.util.system

import com.srivastavavivekggn.scala.util.lang.StringUtils

object SystemUtils {

  /**
   * Get a named system property
   *
   * @param name    the property name
   * @param default the default value
   * @return the property if found, or the default
   */
  def getProperty(name: String, default: String): String = try System.getProperty(name, default) catch {
    case _: SecurityException => default
  }

  /**
   * Get a system property as an Int value
   *
   * @param name    the property name
   * @param default the default value
   * @return the found value as an int, or the default
   */
  def getIntProperty(name: String, default: Int): Int = {
    StringUtils.nonEmpty(getProperty(name, StringUtils.EMPTY), trim = true).map(_.toInt).getOrElse(default)
  }

  /**
   * Get's the system property for 'name' and determines if it is a processor multiplier (i.e., starts with 'x') or an Int
   *
   * @param name    the property name
   * @param default the default value
   * @return the found value, if an Int, or the n umber of processors * the found value (if it starts with 'x')
   */
  def getIntMultiplier(name: String, default: String): Int = getProperty(name, default) match {
    case s: String if s.charAt(0) == 'x' => (Runtime.getRuntime.availableProcessors * s.substring(1).toDouble).ceil.toInt
    case other: String => other.toInt
  }
}
