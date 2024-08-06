package com.srivastavavivekggn.scala.util.unit.convert

import com.srivastavavivekggn.scala.util.BaseUtilSpec
import com.srivastavavivekggn.scala.util.lang.NumberUtils

trait UnitConversionSpec extends BaseUtilSpec {

  def runs: List[TestRun]

  def runTests(): Unit = {
    runs.foreach(tr => {
      it should s"convert ${tr.sourceAmount} ${tr.sourceUnit} to ${tr.expectedUnit}" in {
        assertResult(tr.expectedResult)(
          NumberUtils.roundTo(
            tr.testFn.apply(tr.sourceAmount),
            tr.precision
          ))
      }
    })
  }


  case class TestRun(sourceAmount: Double,
                     sourceUnit: String,
                     precision: Int,
                     expectedResult: Double,
                     expectedUnit: String,
                     testFn: (Double) => Double)
}
