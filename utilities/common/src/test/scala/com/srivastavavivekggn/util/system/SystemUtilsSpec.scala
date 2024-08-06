package com.srivastavavivekggn.scala.util.system

import com.srivastavavivekggn.scala.util.BaseUtilSpec

class SystemUtilsSpec extends BaseUtilSpec {

  behavior of "SystemUtils"

  System.setProperty("systemspec-test1", "hi")
  System.setProperty("systemspec-test2", "bye")
  System.setProperty("systemspec-test3", "192")

  it should "retrieve a system property as a string" in {
    assert(
      SystemUtils.getProperty("systemspec-test1", "none") == "hi"
    )
  }

  it should "retrieve the default value when property is not found" in {
    assert(
      SystemUtils.getProperty("systemspec", "none") == "none"
    )
  }

  it should "retrieve an int value from a property" in {
    assert(
      SystemUtils.getIntProperty("systemspec-test3", 1) == 192
    )
  }

  it should "return the default integer when no property is found" in {
    assert(
      SystemUtils.getIntProperty("systemspec", 1) == 1
    )
  }
}
