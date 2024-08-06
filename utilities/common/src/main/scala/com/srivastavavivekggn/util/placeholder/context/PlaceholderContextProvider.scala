package com.srivastavavivekggn.scala.util.placeholder.context

/**
  * Simple interface for context provider
  */
trait PlaceholderContextProvider {

  /**
    * Does this provider contain the given key
    *
    * @param key the key
    * @return true if the provider contains this key
    */
  def contains(key: String): Boolean

  /**
    * Get the value for the given key
    *
    * @param key the key
    * @return the optional value
    */
  def get(key: String): Option[Any]
}
