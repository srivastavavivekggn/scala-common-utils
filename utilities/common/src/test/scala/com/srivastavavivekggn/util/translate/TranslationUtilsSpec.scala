package com.srivastavavivekggn.scala.util.translate

import com.srivastavavivekggn.scala.util.BaseUtilSpec

class TranslationUtilsSpec extends BaseUtilSpec {

  behavior of "TranslationUtils"

  it should "properly sanitize input" in {

    val input =
      """
        |
        |This  has multi
        | lines with multi space AND space at the end """.stripMargin

    val result = TranslationUtils.sanitize(input)

    assertResult("This has multi lines with multi space AND space at the end")(result)
  }

  it should "do nothing on empty input" in {
    val result = TranslationUtils.sanitize(None.orNull)

    assertResult(None.orNull)(result)
  }

  it should "find hash equality when 2 inputs are sanizite-same" in {

    val input1 = "  This is an            input with many spaces inside ";
    val input2 =
      """This
        |
        | is an input
        | with
        | many
        | spaces inside
        |
        | """.stripMargin

    val hash1 = TranslationUtils.sanitizeAndHash(input1)
    val hash2 = TranslationUtils.sanitizeAndHash(input2)

    assertResult(hash1)(hash2)
  }

  it should "match a simple known hash value" in {
    val text = "View your PCP"
    val hash = "bcd0205e835644dded9efb842d613fb394d9d28765d81660bfbfc488279c3c71"

    assertResult(hash)(TranslationUtils.sanitizeAndHash(text))
  }

  it should "match a complex known hash value" in {
    val hash = "e8a536b8673b81e205a337b196814b551fbd61c885121534e4b3308d632d2396"
    val originalText = "<p>Nestle employees are eligible to earn a payroll contribution for completing the following activities by December 31, 202</p><ul><li>Earn $250 when you take the RealAge health assessment.</li><li>Earn $250 when you a health screening (you can complete a health screening at one of the onsite locations, at a Quest Patient Service Center, or with your PCP).</li></ul>"

    val text =
      """<p>Nestle employees are eligible to earn a payroll contribution for completing the following activities by December 31,
        | 202</p>
        |<ul>
        |<li>Earn $250 when you take the RealAge health assessment.</li>
        |<li>Earn $250 when you a health screening (you can complete a health screening at one of the onsite locations, at a
        |         Quest Patient Service Center, or with your PCP).
        |</li>
        |</ul>""".stripMargin

    assertResult(originalText)(TranslationUtils.sanitize(text))
    assertResult(hash)(TranslationUtils.sanitizeAndHash(text))
  }
}
