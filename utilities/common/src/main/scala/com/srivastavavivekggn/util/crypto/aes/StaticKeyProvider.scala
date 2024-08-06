package com.srivastavavivekggn.scala.util.crypto.aes

import com.srivastavavivekggn.scala.util.TypeAlias.JList
import com.srivastavavivekggn.scala.util.collection.CollectionUtils
import com.srivastavavivekggn.scala.util.random.RandomUtils

/**
  * Static key provider (i.e., keys from properties, file, etc.)
  *
  * @param keys         the list of keys
  * @param minViableKey the minimum key index that can be used for new encryptions
  */
case class StaticKeyProvider(keys: List[String],
                             minViableKey: Int = 0) extends KeyProvider {

  /**
    * Number of bytes required for each key
    */
  final val KEY_SIZE = 32

  /**
    * Internal placeholder for keys
    */
  private val keysInternal: Map[Int, Array[Byte]] = keys.zipWithIndex.map {
    case (key, idx) if key.getBytes.length < KEY_SIZE => throw new RuntimeException(s"Invalid Key at index $idx")
    case (key, idx) => idx -> key.getBytes.take(KEY_SIZE)
  }.toMap

  /**
    * Retrieve a key by it's name/alias
    *
    * @param keyName the key name / alias
    * @return the key, if found
    */
  override def getKey(keyName: String): Array[Byte] = if (keysInternal.contains(keyName.toInt)) {
    keysInternal.getOrElse(keyName.toInt, Array.empty[Byte])
  }
  else {
    throw new RuntimeException(s"Invalid key: $keyName")
  }

  /**
    * During encryption, we need to be able to randomly get a key
    *
    * @return the randomly selected keyId and key value
    */
  override def getRandomKey: (Integer, Array[Byte]) = {
    val keys = keysInternal.keys.filter(_ >= minViableKey)

    if (keys.isEmpty) {
      throw new RuntimeException(s"No encryption keys available for minimum: ${minViableKey}")
    }
    else {
      val randomKey = RandomUtils.getRandomItem(keys.toSeq)
      (randomKey, keysInternal(randomKey))
    }
  }

  /**
    * Initialize the key provider
    */
  override def init: Unit = ()
}

object StaticKeyProvider {

  def apply(keys: JList[String], minViableKey: Int): StaticKeyProvider = {
    require(Option(keys).isDefined, "One or more keys must be present")
    new StaticKeyProvider(CollectionUtils.asScalaListOrEmpty(keys), minViableKey)
  }
}
