package com.srivastavavivekggn.scala.util.crypto.aes

import com.srivastavavivekggn.scala.util.random.RandomUtils
import com.typesafe.scalalogging.StrictLogging

class SimpleKeyProvider extends KeyProvider with StrictLogging {

  private val TAKE_SIZE = 32

  /**
    * This provides a bunch of random 48 character strings that we'll piece together
    * to form a 256 bit key
    */
  private val a = "U1D7HEL7TOGBVBSZQJ9ABUTF87LNMZWL7XLVXCZBUAWB8DOF"
  private val b = "RG56LINKMFY0XXTCUNUDBYMDWPLVNOHS3BMZPYAVCZ0G76OH"
  private val c = "0ZPZO61J0M7CRJTQ8H4A37SXHKPRUNRSI1MI8QOPNMBFSFUL"
  private val d = "52V0DANFGTMK2VWFB4MIJCDFTTENKE4NCYFC15AJ01DNHWKK"
  private val e = "NT8ICVQSUNK5PQIQREOFB2QYIU29T2SBNHEZPIYXSC0HZXRM"
  private val f = "CFWIQYAZPAFHIGPCXOLEZF4GCQ1YJSCNBJ5DUALCKCIEO3VP"

  /**
    * The simple set of keys we'll use for every request
    */
  final val simpleKeys: Map[String, Array[Byte]] = Map(
    "10" -> s"${a}${c}${f}${b}${e}${d}".getBytes.take(TAKE_SIZE),
    "11" -> s"${c}${f}${a}${d}${b}${e}".getBytes.take(TAKE_SIZE),
    "12" -> s"${e}${b}${c}${d}${a}${f}".getBytes.take(TAKE_SIZE),
    "13" -> s"${b}${a}${c}${f}${d}${e}".getBytes.take(TAKE_SIZE),
    "14" -> s"${c}${d}${e}${a}${f}${b}".getBytes.take(TAKE_SIZE),
    "15" -> s"${d}${c}${f}${b}${e}${a}".getBytes.take(TAKE_SIZE)
  )

  /**
    * Retrieve a key by it's name/alias
    *
    * @param keyName the key name / alias
    * @return the key, if found
    */
  override def getKey(keyName: String): Array[Byte] = simpleKeys(keyName)

  /**
    * During encryption, we need to be able to randomly get a key
    *
    * @return the randomly selected keyId and key value
    */
  override def getRandomKey: (Integer, Array[Byte]) = {
    val key = RandomUtils.getRandomItem(simpleKeys.keySet.toSeq)
    (key.toInt, simpleKeys(key))
  }

  /**
    * Initialize the key provider
    */
  override def init: Unit = ()
}
