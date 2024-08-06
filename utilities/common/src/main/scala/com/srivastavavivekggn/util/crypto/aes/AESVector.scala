package com.srivastavavivekggn.scala.util.crypto.aes

import com.srivastavavivekggn.scala.util.random.RandomUtils

import scala.util.Random

/**
  * Initialization Vectors for AES encryption
  */
object AESVector {

  /**
    * How many bytes do we want
    */
  private val TAKE_SIZE = 16

  /**
    * The minimum vector index to use when looking up a random vector
    */
  private val minimumViableVector = 0

  /**
    * Fixed set of Initialization Vectors.
    *
    * It is unlikely needed, but additional elements may be added. However,
    * the existing values SHOULD NEVER CHANGE!
    */
  private val vectorSet: List[String] = List(
    "510a1a0e-fcf9-4695-8194-ff3e55c256b5",
    "04c67117-0c10-40b7-b29c-12e5f67b650f",
    "1b3bfc34-58df-45d3-a810-a389bdba5fd0",
    "2a7223a8-37fc-469f-a47d-baed14b6bd3a",
    "3fad1cc4-7c58-48e1-9cd0-cdcc3d176afc",
    "b4cdc85c-f570-41b9-b02f-53f312223f8c",
    "cb156fed-bbd3-429d-809d-2fef07830813",
    "28745728-3651-4ba6-8969-4bbff1544180",
    "8c6061f1-e33f-4b65-8ff5-7d82d9bb1c52",
    "fdcd667a-ba08-4103-90cb-d185e7226bb6",
    "d34f351d-d272-4c79-979e-dac9312b164b",
    "74182412-d068-4011-9e14-c2135b938b79",
    "fb7f9ac7-db47-4063-93ab-57e5243bf80f",
    "16973c6e-95a8-48f6-b6ac-b6f81c99e751",
    "f8385d59-ab03-4726-8ff3-688610bd0896",
    "3d3706a2-c40b-444f-9ac8-285e7378b045",
    "451b22f3-2e62-438d-ba08-b7fc1e52aeba",
    "774e30f7-44d8-43a0-83d5-821ebb33ffeb",
    "8315ed55-558b-4c94-be79-d7d44dc86d84",
    "181ea43f-a14c-400f-814a-0d93e645ebb4"
  )

  /**
    *
    */
  private lazy val VectorList = vectorSet.map(_.getBytes.take(TAKE_SIZE))

  /**
    * Mapped vector list for easy lookups
    *
    * The key is the Vector identifier, the value is a random 16Byte array
    *
    * Vector keys start at 10 (hence, the +10 on the index)
    */
  lazy val Vectors: Map[Int, Array[Byte]] = VectorList.zipWithIndex.map(v => (v._2 + 10, v._1)).toMap


  /**
    * Lookup a vector based on the vector key
    * @param key the numeric key, between 10 and 99
    * @return The vector as an array of bytes
    */
  def getVector(key: Int): Array[Byte] = Vectors.get(key) match {
    case Some(v) => v
    case _ => throw new RuntimeException(s"Could not find Vector with key ${key}")
  }

  /**
    * Looks up a vector using a string key (simply converts the string to Int)
    * @param key the key
    * @return the vector
    */
  def getVector(key: String): Array[Byte] = getVector(Integer.valueOf(key))


  /**
    * Returns a randomly selected vector from the map
    * @return (vector key, vector bytes) A tuple with the key and bytes
    */
  def getRandomVector: (Int, Array[Byte]) = {
    val key = RandomUtils.getRandomInt(Vectors.size - 1) + 10
    (key, getVector(key))
  }
}
