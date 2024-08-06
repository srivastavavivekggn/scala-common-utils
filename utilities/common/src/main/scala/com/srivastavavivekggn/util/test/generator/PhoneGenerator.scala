package com.srivastavavivekggn.scala.util.test.generator

import com.srivastavavivekggn.scala.util.random.RandomUtils

object PhoneGenerator {

  def getRandomPhone: String = {
    val minAreaCode = 200
    val maxAreaCode = 999
    val areaCode = RandomUtils.getRandomInt(maxAreaCode - minAreaCode) + minAreaCode

    val minLast6 = 2000000
    val maxLast6 = 9999999

    val last6 = RandomUtils.getRandomInt(maxLast6 - minLast6) + minLast6

    s"$areaCode$last6"
  }
}
