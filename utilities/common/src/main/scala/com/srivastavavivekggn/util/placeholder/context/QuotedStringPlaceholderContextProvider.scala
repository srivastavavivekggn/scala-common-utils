package com.srivastavavivekggn.scala.util.placeholder.context

/**
  * Items surrounding with single quotes will can be returned as-is (minus the quotes)
  */
class QuotedStringPlaceholderContextProvider extends PlaceholderContextProvider {
  override def contains(key: String): Boolean = key.startsWith("'") && key.endsWith("'")

  override def get(key: String): Option[Any] = Option(key.drop(1).dropRight(1))
}

