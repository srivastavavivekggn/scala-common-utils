package com.srivastavavivekggn.scala.util.crypto

import com.srivastavavivekggn.scala.util.crypto.aes.{AESCipher, AESVector, KeyProvider}

object EncryptionUtils {


  object AES {

    private val indicator = "_SCE"
    private val extractor = s"(.*?)$indicator([0-9]+)_([0-9]+)".r

    def encrypt(valueToEncrypt: String, keyProvider: KeyProvider): String = {
      val (vectorId, vector) = AESVector.getRandomVector
      val (keyId, key) = keyProvider.getRandomKey

      val encrypted = AESCipher(key, vector).getEncryptedMessage(valueToEncrypt)

      // return the encrypted value, and append the keyId and vectorId so we can decrypt later
      s"$encrypted$indicator${keyId}_$vectorId"
    }

    def decrypt(encryptedValue: String, keyProvider: KeyProvider): String = {

      val extractor(encryptedString, keyId, vectorId) = encryptedValue

      val key = keyProvider.getKey(keyId)
      val initialVector = AESVector.getVector(vectorId)

      // return the decrypted value
      AESCipher(key, initialVector).getDecryptedMessage(encryptedString)
    }
  }

}
