package com.srivastavavivekggn.scala.util.crypto

import java.security.MessageDigest

import com.google.common.io.BaseEncoding
import com.srivastavavivekggn.scala.util.lang.StringUtils
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
  * Created by JohnDeverna on 1/29/15.
  */
object HashUtils {

  /**
    * sha 128 digest name
    */
  final val SHA128 = "SHA-1"

  /**
    * sha 256 digest name
    */
  final val SHA256 = "SHA-256"

  /**
    * the MD5 hash name
    */
  final val MD5 = "MD5"

  /**
    * the name of the SHA256 algorithm
    */
  final val HMAC_SHA256: String = "HmacSHA256"

  /**
    * The salt
    */
  private final val SALT = "zD61bf636eF1"

  /**
    * Creates a hash string (sha256) out of the concatenated elements of the list, separated by ";"
    * This should primarily be used only on simple types, like Integer, Long, String, etc.
    *
    * One use case for this is creating a unique cache key based on the list values.
    *
    * NOTE: a change in element order will result in a different hash value, so make sure your list is sorted BEFORE
    * utilizing this method
    *
    * @param keys the set of keys to use as hashkey
    * @return a hash of the element values
    */
  def toHashKey(keys: Any*): String = {
    sha256(keys.mkString(";"))
  }

  /**
    * Calculate the SHA-128 hash of the given string
    *
    * @param str the string to hash
    * @return the SHA128 hash
    */
  def sha128(str: String, useHex: Boolean = false): String = hash(SHA128, SALT + str, useHex)

  /**
    * Calculate the SHA-256 hash of the given string
    *
    * @param str the string to hash
    * @return the hashed value
    */
  def sha256(str: String, useHex: Boolean = false): String = hash(SHA256, SALT + str, useHex)

  /**
    * Calculate the SHA256 hash using the HMAC algorithm with the given key
    *
    * @param hmacKey the key
    * @param input   the input to hash
    * @return the
    */
  def sha256Hmac(hmacKey: String, input: String): String = {

    require(StringUtils.isNotEmpty(hmacKey), "HMAC Key must not be empty")
    require(Option(input).isDefined, "Input must not be empty")

    try {
      val signingKey: SecretKeySpec = new SecretKeySpec(hmacKey.getBytes, HMAC_SHA256)
      val mac: Mac = Mac.getInstance(HMAC_SHA256)
      mac.init(signingKey)

      val rawHmac: Array[Byte] = mac.doFinal(input.getBytes)

      BaseEncoding.base64().encode(rawHmac)
    }
    catch {
      case e: Exception => {
        e.printStackTrace
        None.orNull
      }
    }
  }

  /**
    * Hashes the given string using the specified hashing algorithm.
    *
    * @param encType The algorithm to use for hashing
    * @param s       the string to convert
    * @param useHex  true to return a HEX representation of the hash, false to return Base64
    * @return the hashed string, either as Hex or as Base64
    */
  def hash(encType: String, s: String, useHex: Boolean): String = {

    // make sure a valid algorithm was selected
    require(encType == SHA128 || encType == SHA256 || encType == MD5)

    try {
      val mda: MessageDigest = MessageDigest.getInstance(encType)
      val buf: Array[Byte] = mda.digest(s.getBytes)

      useHex match {
        case true => buf.map("%02X" format _).mkString
        case _ => BaseEncoding.base64().encode(buf)
      }
    }
    catch {
      case e: Exception =>
        throw new RuntimeException(s"Unable to generate hash for input string using algorithm: ${encType}", e)
    }
  }
}
