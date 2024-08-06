package com.srivastavavivekggn.scala.util.crypto

import com.srivastavavivekggn.scala.util.BaseUtilSpec
import com.srivastavavivekggn.scala.util.crypto.aes.{SimpleKeyProvider, StaticKeyProvider}

class EncryptionUtilsSpec extends BaseUtilSpec {

  val simpleKeyProvider = new SimpleKeyProvider

  behavior of "EncryptionUtils"

  it should "encrypt a string" in {

    val message = "s0m3 m3ssage !$@##&$%^U*%I* lkdsfj12$234#$2q!@#$"

    (0 to 10).foreach(_ => {

      val encrypted = EncryptionUtils.AES.encrypt(message, simpleKeyProvider)
      val decrypted = EncryptionUtils.AES.decrypt(encrypted, simpleKeyProvider)

      assert(encrypted != message)
      assert(decrypted == message)
    })
  }

  it should "decrypt an existing string" in {
    val result = EncryptionUtils.AES.decrypt("TdDi0pva0GyaloaD6sCmDg==_SCE11_27", simpleKeyProvider)
    assert(result == "hi there!")
  }


  it should "fail when an invalid vector is present" in {
    intercept[RuntimeException] {
      EncryptionUtils.AES.decrypt("85llpGFHdrdvzY3fnW3uYg==_SCE13_1999", simpleKeyProvider)
    }
  }

  it should "fail when an invalid key is present" in {
    intercept[RuntimeException] {
      EncryptionUtils.AES.decrypt("85llpGFHdrdvzY3fnW3uYg==_SCE949393_18", simpleKeyProvider)
    }
  }


  behavior of "StaticKeyProvider"

  it should "create a static key provider" in {
    val kp = StaticKeyProvider(List("b9a0050c-d873-4697-834c-85e2dd24eb60"))
    val encrypted = EncryptionUtils.AES.encrypt("static provider", kp)
    val decrypted = EncryptionUtils.AES.decrypt(encrypted, kp)

    assert(encrypted.contains("_SCE0_"))
    assert(decrypted == "static provider")
  }

  it should "fail to create a provider when key < 32 bytes" in {
    intercept[RuntimeException] {
      StaticKeyProvider(List("b9a0050c-d873-4697-834c-85e2dd2"))
    }
  }

  it should "never use a key where index < min viable" in {
    val kp = StaticKeyProvider(List(
      "b9a0050c-d873-4697-834c-85e2dd24eb60",
      "28745728-3651-4ba6-8969-4bbff1544180"
    ), minViableKey = 1)

    (0 to 100).foreach(_ => {
      val encrypted = EncryptionUtils.AES.encrypt("static provider", kp)
      assert(encrypted.contains("_SCE1_"))
    })
  }
}
