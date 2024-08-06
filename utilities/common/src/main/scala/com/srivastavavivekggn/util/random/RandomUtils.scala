package com.srivastavavivekggn.scala.util.random

import java.security.SecureRandom
import java.util.UUID

import scala.collection.immutable.Stream
import scala.util.Random

object RandomUtils {

  /**
    * instance of Secure random to generate secure random passwords
    */
  lazy private final val secureRandom = new SecureRandom

  /**
    * Get a random element from the provided list
    * @param lst the list
    * @return the random element from the list
    */
  def getRandomItem[T](lst: Seq[T]): T = lst(secureRandom.nextInt(lst.length))

  /**
    * Returns the next pseudorandom, uniformly distributed int value
    * from this random number generator's sequence.
    */
  def getRandomInt: Int = secureRandom.nextInt()

  /** Returns a pseudorandom, uniformly distributed int value between 0
    * (inclusive) and the specified value (exclusive), drawn from this
    * random number generator's sequence.
    */
  def getRandomInt(n: Int): Int = secureRandom.nextInt(n)

  /**
    * Returns the next pseudorandom, uniformly distributed long value
    * from this random number generator's sequence.
    */
  def getRandomLong(): Long = secureRandom.nextLong()

  /**
    * Get a random string of the specified length
    * @param length the string length
    * @return the random string
    */
  def getRandomString(length: Int): String = alphanumChars.take(length).mkString

  /**
    * Returns the next pseudorandom, uniformly distributed boolean value
    * from this random number generator's sequence.
    */
  def getRandomBoolean: Boolean = secureRandom.nextBoolean()

  /**
    * Returns true the specified percentage of time. For example, if you pass 30 to this function, it should return
    * true 30% of the time.
    *
    * @param percentage the percentage of time.
    * @return boolean true of false
    */
  def getRandomWeightedBoolean(percentage: Double): Boolean = getRandomInt(100) < percentage // scalastyle:ignore magic.number

  /**
    * Generates random bytes and places them into a user-supplied byte
    * array.
    */
  def getRandomBytes(bytes: Array[Byte]): Unit = {
    secureRandom.nextBytes(bytes)
  }

  /**
    * Returns the next pseudorandom, uniformly distributed double value
    * between 0.0 and 1.0 from this random number generator's sequence.
    */
  def getRandomDouble: Double = secureRandom.nextDouble()

  /**
    * Returns the next pseudorandom, uniformly distributed float value
    * between 0.0 and 1.0 from this random number generator's sequence.
    */
  def getRandomFloat: Float = secureRandom.nextFloat()

  /**
    * Returns a random UUID
    */
  def getRandomId: String = UUID.randomUUID.toString


  /**
    * Get randomly generated password
    *
    * @return the password
    */
  def getSecureRandomPassword(length: Int = 26): String = passwordChars.take(length).mkString //scalastyle:off magic.number

  /**
    * Helper method to generate a stream of random characters
    * @return the stream of chars
    */
  private def passwordChars: LazyList[Char] = {
    def nextAlphaNum: Char = {
      val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()[]{}~"
      chars charAt (secureRandom nextInt chars.length)
    }

    LazyList continually nextAlphaNum
  }

  private def alphanumChars: LazyList[Char] = {
    def nextAlphaNum: Char = {
      val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
      chars charAt (secureRandom nextInt chars.length)
    }

    LazyList continually nextAlphaNum
  }
}
