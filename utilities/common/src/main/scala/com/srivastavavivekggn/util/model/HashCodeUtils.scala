package com.srivastavavivekggn.scala.util.model

object HashCodeUtils {

  final val PRIME_MULTIPLIER = 31

  /**
    * Convert a sequence of values into a hash code
    *
    * @param values the sequence of values
    * @return the corresponding hash code
    */
  def toHashCode(values: Any*): Int = foldHashCodes(values.map(toHashCode))

  /**
    * Fold a sequence of hash codes into a single hash code
    *
    * @param hashCodes the hash codes to combine
    * @return the resulting hash code
    */
  def foldHashCodes(hashCodes: Seq[Int]): Int = hashCodes.fold(1)(PRIME_MULTIPLIER * _ + _)

  /**
    * Convert a single value into a hash code
    */
  private val toHashCode: Any => Int = {
    case x if x == null => 0
    case x: Option[_] if x.isEmpty => 0
    case x: Option[_] => x.get.hashCode()
    case x => x.hashCode()
  }
}
