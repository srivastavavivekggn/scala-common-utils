package com.srivastavavivekggn.scala.util.crypto.aes

import java.security.Key

import com.srivastavavivekggn.scala.util.crypto.EncodeUtils
import javax.crypto.Cipher
import javax.crypto.spec.{IvParameterSpec, SecretKeySpec}

/**
  * The AESCipher
  *
  * @param key the key
  * @param iv  the initial vector
  */
case class AESCipher(key: Array[Byte], iv: Array[Byte]) {

  private val secretKeySpec: SecretKeySpec = new SecretKeySpec(key, "AES")

  private val ivParameterSpec: IvParameterSpec = new IvParameterSpec(iv)

  private val cipher = Cipher.getInstance(AESCipher.ALGORITHM_AES256)

  /**
    * Takes message and encrypts with Key
    *
    * @param message String
    * @return String Base64 encoded
    */
  def getEncryptedMessage(message: String): String = {
    val cipher: Cipher = getCipher(Cipher.ENCRYPT_MODE)
    val encryptedTextBytes: Array[Byte] = cipher.doFinal(message.getBytes)

    EncodeUtils.encodeBase64(encryptedTextBytes)
  }

  /**
    * Takes Base64 encoded String and decodes with provided key
    *
    * @param message String encoded with Base64
    * @return String
    */
  def getDecryptedMessage(message: String): String = {
    val cipher: Cipher = getCipher(Cipher.DECRYPT_MODE)
    val encryptedTextBytes: Array[Byte] = EncodeUtils.decode(message)
    val decryptedTextBytes: Array[Byte] = cipher.doFinal(encryptedTextBytes)

    // return the new string
    new String(decryptedTextBytes)
  }

  /**
    * Get IV in Base64 Encoded String
    *
    * @return String Base64 Encoded
    */
  def getIV: String = EncodeUtils.encodeBase64(ivParameterSpec.getIV)

  /**
    * Base64 encoded version of key
    *
    * @return String
    */
  def getKey: String = getKey(BASE64)

  /**
    * Get the key based on the encoding
    *
    * @param encoding the encoding to use
    * @return the key
    */
  def getKey(encoding: KeyEncoding): String = encoding match {
    case BASE64 => EncodeUtils.encodeBase64(secretKeySpec.getEncoded)
    case HEX => EncodeUtils.encodeBase16(secretKeySpec.getEncoded)
    case BASE32 => EncodeUtils.encodeBase32(secretKeySpec.getEncoded)
  }

  /**
    * Get a chiper instance
    *
    * @param encryptMode
    * @return
    */
  private def getCipher(encryptMode: Int): Cipher = {
    cipher.init(encryptMode, getSecretKeySpec, ivParameterSpec)
    cipher
  }

  /**
    * Get the secret key spec
    *
    * @return the secret key spec
    */
  private def getSecretKeySpec: SecretKeySpec = secretKeySpec
}


object AESCipher {

  val ALGORITHM_AES256: String = "AES/CBC/PKCS5Padding"

  /**
    * Create AESCipher based on existing key
    *
    * @param key Key
    */
  def apply(key: Key, iv: Array[Byte]): AESCipher = new AESCipher(key.getEncoded, iv)
}
