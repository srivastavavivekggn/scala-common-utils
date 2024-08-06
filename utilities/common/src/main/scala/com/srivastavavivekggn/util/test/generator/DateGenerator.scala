package com.srivastavavivekggn.scala.util.test.generator

import java.time.LocalDate

import com.srivastavavivekggn.scala.util.lang.DateUtils
import com.srivastavavivekggn.scala.util.random.RandomUtils

// scalastyle:off magic.number
object DateGenerator {


  def getRandomBirthDate(minAge: Int = 18): LocalDate = {

    val years = RandomUtils.getRandomInt(100 - minAge) + minAge

    DateUtils
      .localDateNow
      .minusYears(years)
      .plusDays(RandomUtils.getRandomInt(365))
  }
}
// scalastyle:on magic.number
