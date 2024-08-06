package com.srivastavavivekggn.scala.util.collection


private[collection] object Decorators {

  /**
    * Generic converter class containing the `asJavaOrEmpty` method
    */
  class AsJavaOrEmpty[A](op: => A) {

    /**
      * Converts a Scala collection to the corresponding Java collection with null safety
      */
    def asJavaOrEmpty: A = op
  }

  /**
    * Generic converter class containing the `asScalaOrEmpty` method
    */
  class AsScalaOrEmpty[A](op: => A) {

    /**
      * Converts a Java collection to the corresponding Scala collection with null safety
      */
    def asScalaOrEmpty: A = op
  }
}
