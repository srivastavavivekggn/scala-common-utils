package com.srivastavavivekggn.scala.util.crypto.aes

trait KeyProvider {

  /**
    * Retrieve a key by it's name/alias
    * @param keyName the key name / alias
    * @return the key, if found
    */
  def getKey(keyName: String): Array[Byte]

  /**
    * During encryption, we need to be able to randomly get a key
    * @return the randomly selected keyId and key value
    */
  def getRandomKey: (Integer, Array[Byte])

  /**
    * Initialize the key provider
    */
  def init: Unit

}
