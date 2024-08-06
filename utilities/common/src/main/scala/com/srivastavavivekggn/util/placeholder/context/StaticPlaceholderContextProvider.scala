package com.srivastavavivekggn.scala.util.placeholder.context

/**
  * Context provider implementation that uses an underlying map
  *
  * @param map the map
  */
case class StaticPlaceholderContextProvider(map: Map[String, Any]) extends PlaceholderContextProvider {
  override def contains(key: String): Boolean = map.contains(key)

  override def get(key: String): Option[Any] = map.get(key)
}
