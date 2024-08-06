package com.srivastavavivekggn.scala.util.placeholder.context

case class ObjectPlaceholderContextProvider[T](obj: T, providerMap: Map[String, T => Option[Any]])
  extends PlaceholderContextProvider {

  /**
    * Does this provider contain the given key
    *
    * @param key the key
    * @return true if the provider contains this key
    */
  override def contains(key: String): Boolean = providerMap.contains(key)

  /**
    * Get the value for the given key
    *
    * @param key the key
    * @return the optional value
    */
  override def get(key: String): Option[Any] = providerMap.get(key).flatMap(_.apply(obj))

}
