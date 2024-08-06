package com.srivastavavivekggn.scala.util.placeholder.context

/**
  * Context implementation that wraps multiple contexts
  *
  * @param providers the underlying context providers
  */
case class CompositePlaceholderContextProvider(providers: PlaceholderContextProvider*) extends PlaceholderContextProvider {
  override def contains(key: String): Boolean = providers.exists(_.contains(key))

  override def get(key: String): Option[Any] = providers.find(_.contains(key)).flatMap(_.get(key))
}
