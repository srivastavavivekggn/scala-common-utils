package com.srivastavavivekggn.scala.util.test.generator

import com.srivastavavivekggn.scala.util.random.RandomUtils

/**
  * Utility for generating email addresses.  Uses the NameGenerator to get the
  * first part of the email address, and chooses a random domain for the second part.
  *
  * This does not guarantee uniqueness in any way, though tests showed the chance of
  * the same email getting generated twice is about 1 in 50,000 (or about 0.002% of the time)
  */
object EmailGenerator {

  /**
    * The set of email domains
    */
  final val emailDomains: Seq[String] = Seq(
    "qhperform.com", "xxxxx.com", "optimum.com", "gmail.com", "yahoo.com",
    "hotmail.com", "aol.com", "comcast.net", "outlook.com", "att.net", "verizon.net",
    "icloud.com", "msn.com", "live.com", "sbcglobal.net", "ymail.com", "bellsouth.net",
    "cox.net", "mail.com", "charter.net", "rocketmail.com", "me.com", "windstream.net",
    "juno.com", "frontier.com", "centurylink.net", "roadrunner.com",
    "earthlink.net", "aim.com", "optonline.net", "netzero.com", "facebook.com", "email.com",
    "netzero.net", "twc.com", "trbvm.com", "gmx.com", "kiois.com", "tampabay.rr.com", "noemail.com"
  )

  /**
    * The set of strings that could be used as a separating character in an email address
    */
  final val emailSeparators: Seq[String] = Seq("", ".", "_")

  /**
    * Get a random number
    *
    * @return the random number
    */
  private def getRandom: Int = {
    val randomMax = 8999
    val randomOffset = 1000
    RandomUtils.getRandomInt(randomMax) + randomOffset
  }

  /**
    * Set of functions that transform the incoming name
    */
  final val scrubFunctions: Seq[((String, String)) => String] = Seq(

    /**
      * first name, last name with separator
      */ { tpl => s"${tpl._1}${getRandomSeparator}${tpl._2}${getRandom}" },

    /**
      * first name, last name with separator and random number
      */ { tpl => s"${tpl._2}${getRandomSeparator}${tpl._1}${getRandom}" },

    /**
      * take first initial and last name + random number
      */ { tpl => s"${tpl._1.take(1)}${getRandomSeparator}${tpl._2}${getRandom}" },

    /**
      * take first name and last initial + random number
      */ { tpl => s"${tpl._1}${getRandomSeparator}${tpl._2.take(1)}${getRandom}" },

    /**
      * last name, first name with separator
      */ { tpl => s"${tpl._2}${getRandomSeparator}${tpl._1}${getRandom}" },

    /**
      * last name, first name with separator and random number
      */ { tpl => s"${tpl._2}${getRandomSeparator}${tpl._1}${getRandom}" },

    /**
      * first name . random number . lastname
      */ { tpl => s"${tpl._1}.${getRandom}.${tpl._2}" }
  )

  /**
    * Get a random email domain
    *
    * @return the random domain
    */
  def getRandomDomain: String = RandomUtils.getRandomItem(emailDomains)

  /**
    * Get a random email separator
    *
    * @return the email separator
    */
  def getRandomSeparator: String = RandomUtils.getRandomItem(emailSeparators)

  /**
    * Get a randomly generated email address in mixed case.  This is useful
    * to simulate how a user might type in their email in a form
    *
    * @return the email address in MIXED CASE
    */
  def getRandomMixedCaseEmail: String = {
    getRandomMixedCaseEmail(NameGenerator.getName)
  }

  /**
    * Generate a random email using the given name
    *
    * @param name the name
    * @return the email
    */
  def getRandomMixedCaseEmail(name: (String, String), prefix: String = ""): String = {
    val n = RandomUtils.getRandomItem(scrubFunctions)(name)
    s"${n}@${prefix}${getRandomDomain}"
  }

  /**
    * Get a randomly generated email address
    *
    * @return the email address - in all LOWERCASE
    */
  def getRandomEmail: String = getRandomMixedCaseEmail.toLowerCase
}
