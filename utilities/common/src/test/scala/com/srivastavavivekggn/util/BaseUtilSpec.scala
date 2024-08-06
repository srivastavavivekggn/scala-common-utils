package com.srivastavavivekggn.scala.util

import com.srivastavavivekggn.scala.util.test.SimpleFlatSpec
import org.junit.runner.RunWith
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.junit.JUnitRunner
import org.scalatestplus.mockito.MockitoSugar

@RunWith(classOf[JUnitRunner])
abstract class BaseUtilSpec extends SimpleFlatSpec with Matchers with MockitoSugar {

}
