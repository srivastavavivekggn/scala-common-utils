package com.srivastavavivekggn.scala.util.crypto

import com.srivastavavivekggn.scala.util.BaseUtilSpec
import com.srivastavavivekggn.scala.util.random.RandomUtils
import com.srivastavavivekggn.scala.util.test.generator.EmailGenerator

class HashUtilsSpec extends BaseUtilSpec {

  "HashUtils" should "return the sha-256 of the string" in {

    val result = HashUtils.sha256(EmailGenerator.getRandomEmail)
    assert(result != null)
    assert(result.length <= 64)
  }

  it should "return the sha-256 HMAC for a known input and key" in {

    val content = "test"
    val key = "ZZ===WWWYYYY"
    val result = HashUtils.sha256Hmac(key, content)

    assert(result != null)
    assert(result == "Qc7vzmGAqW9DUF3gmPHKYHsEjnhL5p2RLiBO5cp/LEE=")
  }

  it should "return the same SHA256 hash, regardless of the number of times called" in {

    val eml = EmailGenerator.getRandomEmail
    val result = HashUtils.sha256(eml)

    for(i <- (1 to 10000 filter(_ % 10 == 0))) {
      assert(result == HashUtils.sha256(eml))
    }
  }

  it should "return a fixed length Base64 string regardless of the input size" in {
    for (i <- (1 to 4000 filter(_ % 10 == 0))) {
      assert(HashUtils.sha256(RandomUtils.getRandomString(i)).length == 44)
    }
  }

  it should "return a fixed length Hex string regardless of the input size" in {
    for (i <- (1 to 4000 filter(_ % 10 == 0))) {
      assert(HashUtils.sha256(RandomUtils.getRandomString(i), true).length == 64)
    }
  }

  it should "return the MD5 hash in HEX format for a known input" in {

    val testValue = "hexTest"
    val md5Hash = HashUtils.hash(HashUtils.MD5, testValue, true)

    assert(md5Hash != null)
    assert(md5Hash == "C0C242B1A547D409BAF5BE8919FD0B69")
  }

  it should "return the MD5 hash base64 encoded format for a known input" in {

    val testValue = "base64test"
    val md5Hash = HashUtils.hash(HashUtils.MD5, testValue, false)

    assert(md5Hash != null)
    assert(md5Hash == "lWfkJp6ZdOiyjC+QMDf1AQ==")
  }

}
