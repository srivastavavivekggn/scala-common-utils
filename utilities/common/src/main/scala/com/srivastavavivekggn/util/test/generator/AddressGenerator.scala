package com.srivastavavivekggn.scala.util.test.generator

import com.srivastavavivekggn.scala.util.random.RandomUtils

import scala.collection.immutable.Stream

object AddressGenerator {

  final val POSTAL_LENGTH = 5

  // common street names
  final val streetNames = Seq(
    "Second", "Third", "First", "Fourth", "Park",
    "Fifth", "Main", "Sixth", "Oak", "Seventh", "Pine", "Maple",
    "Cedar", "Eighth", "Elm", "View", "Washington", "Ninth", "Lake", "Hill"
  )

  // common street suffixes
  final val streetSuffixes = Seq("St", "Rd", "Blvd", "Ave", "Dr", "Pl", "Ct", "Way", "Ln")

  // common city names
  final val cities = Seq(
    "Washington", "Springfield", "Franklin", "Lebanon", "Clinton", "Greenville", "Bristol", "Fairview", "Salem",
    "Madison", "Georgetown", "Arlington", "Ashland", "Dover", "Oxford", "Jackson", "Burlington", "Manchester",
    "Milton", "Newport", "Auburn", "Centerville", "Clayton", "Dayton", "Lexington", "Milford", "Mount Vernon",
    "Oakland", "Winchester", "Cleveland", "Hudson", "Kingston", "Riverside"
  )

  // state abbreviations
  final val states = Seq(
    "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA",
    "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK",
    "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"
  )

  def getRandomStreetAddress: String = {
    s"${RandomUtils.getRandomInt(1999) + 1} ${getRandom(streetNames)} ${getRandom(streetSuffixes)}" // scalastyle:ignore magic.number
  }

  def getRandomCity: String = getRandom(cities)

  def getRandomState: String = getRandom(states)

  def getRandomPostal: String = randomNumericString.take(POSTAL_LENGTH).mkString

  def getRandomAddress: String = {
    s"$getRandomStreetAddress, $getRandomCity, $getRandomState $getRandomPostal"
  }

  /**
    * Convenience wrapper for RandomUtils method
    * @param lst the list
    * @return the random element from the list
    */
  private def getRandom(lst: Seq[String]): String = RandomUtils.getRandomItem(lst)

  /**
    * Generate a random string of numbers
    *
    * @return the random string
    */
  private def randomNumericString: LazyList[Char] = {
    def nextNum: Char = {
      val chars = "0123456789"
      chars charAt (RandomUtils.getRandomInt(chars.length))
    }

    LazyList continually nextNum
  }
}
