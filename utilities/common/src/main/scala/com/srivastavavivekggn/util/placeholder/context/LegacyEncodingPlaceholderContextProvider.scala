package com.srivastavavivekggn.scala.util.placeholder.context

/**
  * Adds support for legacy placeholder logic where URLEncoded placeholders were
  * defined as {{ e:name }}
  *
  * This provider simply replaces this with the new format, which is {{ name | urlEncoded }}
  */
class LegacyEncodingPlaceholderContextProvider extends PlaceholderContextProvider {
  /**
    * Does this provider contain the given key
    *
    * @param key the key
    * @return true if the provider contains this key
    */
  override def contains(key: String): Boolean = key.startsWith("e:") && key.length > 2

  /**
    * Get the value for the given key
    *
    * @param key the key
    * @return the optional value
    */
  override def get(key: String): Option[Any] = Some(s"{{${key.drop(2)}|urlEncoded}}")
}
