package com.srivastavavivekggn.scala.util.test

import org.junit.runner.RunWith
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
abstract class SimpleAsyncFlatSpec extends AsyncFlatSpec with Matchers
