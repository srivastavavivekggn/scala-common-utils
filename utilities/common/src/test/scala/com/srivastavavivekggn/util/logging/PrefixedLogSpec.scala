package com.srivastavavivekggn.scala.util.logging

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.srivastavavivekggn.scala.util.BaseUtilSpec
import com.srivastavavivekggn.scala.util.collection.CollectionUtils
import com.typesafe.scalalogging.Logger
import org.scalatest.BeforeAndAfterAll
import org.slf4j.LoggerFactory

class PrefixedLogSpec extends BaseUtilSpec with BeforeAndAfterAll {

  val underlying = LoggerFactory.getLogger(getClass).asInstanceOf[ch.qos.logback.classic.Logger]
  val listAppender: ListAppender[ILoggingEvent] = new ListAppender[ILoggingEvent]()
  val logger = Logger(underlying)

  override protected def beforeAll(): Unit = {
    listAppender.start()
    underlying.addAppender(listAppender)
  }

  override protected def afterAll(): Unit = {
    listAppender.stop()

    // spit out log messages recorded
    CollectionUtils.asScalaListOrEmpty(listAppender.list)
      .map(_.getFormattedMessage).foreach(System.out.println)
  }

  private def getLog(expectedMessage: String, expectedLevel: Level): Option[ILoggingEvent] = {
    CollectionUtils.asScalaListOrEmpty(listAppender.list)
      .find(i => expectedMessage.equals(i.getFormattedMessage) && expectedLevel.equals(i.getLevel))
  }

  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  behavior of "PrefixedLog"

  it should "add a simple log prefix" in {
    val log = PrefixedLog(logger, "A")

    val result = getLog("[A] Hi ", Level.INFO)
    assert(result.isDefined)
  }

  it should "add multiple log prefixes" in {
    val log = PrefixedLog(logger, "A", "B", "C")
    log.debug("Hi ", "")

    val result = getLog("[A][B][C] Hi !", Level.DEBUG)
    assert(result.isDefined)
  }

  it should "compose multiple PrefixedLogs" in {
    val log = PrefixedLog(logger, "A", "B")
    val updated = log.withPrefixValue("Q")
    updated.warn("Hi , {}, and {}!",  "Tim", "Bob")

    val result = getLog("[B][Q] Hi  Tim, and Bob!", Level.WARN)
    assert(result.isDefined)
  }

  it should "handle simple exception logging" in {
    val log = PrefixedLog(logger, "E")
    val t = new RuntimeException("Bad!")

    log.error("Something bad happened", t)

    val result = getLog("[E] Something bad happened", Level.ERROR)
    assert(result.isDefined)
  }

  it should "handle null argument values" in {
    val log = PrefixedLog(logger, "N")

    log.warn("Some args have no value {}, {}, and {}", 1, null, "third")

    val result = getLog("[N] Some args have no value 1, null, and third", Level.WARN)
    assert(result.isDefined)
  }

  it should "handle exception logging with arguments" in {
    val log = PrefixedLog(logger, "E")
    val t = new RuntimeException("Bad!")

    log.error("Something bad happened to {}", t, "Tommy")

    val result = getLog("[E] Something bad happened to Tommy", Level.ERROR)
    assert(result.isDefined)
  }

  it should "handle exception logging with null arguments" in {
    val log = PrefixedLog(logger, "E")
    val t = new RuntimeException("Bad!")

    log.error("Something bad happened to {} and {}", t, "Tommy", None.orNull)

    val result = getLog("[E] Something bad happened to Tommy and null", Level.ERROR)
    assert(result.isDefined)
  }
}
